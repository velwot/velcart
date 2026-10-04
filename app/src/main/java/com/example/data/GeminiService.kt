package com.example.data

import android.util.Log
import com.example.BuildConfig
import com.example.model.ProductAnalysis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

data class GeminiProductLookup(
    val productName: String,
    val brand: String,
    val category: String,
    val ingredientsText: String,
    val isFound: Boolean
)

class GeminiService(
    // 60-second timeouts mandated by gemini-api/SKILL.md
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
) {

    private fun resolveApiKey(): String {
        val buildKey = BuildConfig.GEMINI_API_KEY.trim()
        if (buildKey.isNotBlank() && buildKey != "PLACEHOLDER_KEY") {
            return buildKey
        }
        val envKey = (System.getenv("GEMINI_API_KEY") ?: "").trim()
        if (envKey.isNotBlank() && envKey != "PLACEHOLDER_KEY") {
            return envKey
        }
        return ""
    }

    suspend fun lookupProductByBarcode(
        barcode: String,
        knownName: String? = null,
        knownBrand: String? = null
    ): GeminiProductLookup? = withContext(Dispatchers.IO) {
        val apiKey = resolveApiKey()
        if (apiKey.isBlank()) {
            Log.w("GeminiService", "GEMINI_API_KEY is blank or placeholder, skipping barcode AI lookup")
            return@withContext null
        }

        val prompt = buildString {
            append("You are an expert global and Indian product identifier and barcode lookup specialist.\n")
            append("Investigate this product barcode:\n")
            append("Barcode: $barcode\n")
            if (!knownName.isNullOrBlank() && !knownName.startsWith("Product #")) {
                append("Known Name: $knownName\n")
            }
            if (!knownBrand.isNullOrBlank() && knownBrand != "Indian Market Item" && knownBrand != "Indian Retail Product") {
                append("Known Brand / Manufacturer: $knownBrand\n")
            }
            append("\nYour job:\n")
            append("1. Identify what specific retail product corresponds to barcode '$barcode' (especially in the Indian market where 890... prefix is used, or globally).\n")
            append("2. If and only if you identify the exact product, provide its genuine packaging ingredient list.\n")
            append("3. Return ONLY a single raw JSON object with this exact structure (no markdown, no code block backticks):\n")
            append("{\n")
            append("  \"found\": true,\n")
            append("  \"product_name\": \"Exact Name\",\n")
            append("  \"brand\": \"Exact Brand\",\n")
            append("  \"category\": \"Category\",\n")
            append("  \"ingredients_text\": \"Comma-separated official ingredients\"\n")
            append("}\n\n")
            append("CRITICAL INSTRUCTIONS:\n")
            append("- If you do not recognize barcode '$barcode' or cannot verify its genuine ingredients with high confidence, you MUST return:\n")
            append("{\"found\": false, \"product_name\": \"\", \"brand\": \"\", \"category\": \"\", \"ingredients_text\": \"\"}\n")
            append("- NEVER assume or fabricate ingredients. NEVER default to chickpea flour, besan, snack oils, or generic ingredients.\n")
            append("- A beverage, shampoo, soap, or different snack MUST NOT have snack or besan ingredients.")
        }

        try {
            // Use gemini-3.5-flash as specified by gemini-api/SKILL.md
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val partObj = JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    }
                    put(partObj)
                }
                put("contents", contents)
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val err = response.body?.string() ?: ""
                    Log.e("GeminiService", "Gemini HTTP error ${response.code}: $err")
                    return@withContext null
                }

                val resBody = response.body?.string() ?: return@withContext null
                val resJson = JSONObject(resBody)
                val candidates = resJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val rawText = parts.getJSONObject(0).optString("text").trim()
                        return@withContext parseLookupJson(rawText)
                    }
                }
                null
            }
        } catch (e: Exception) {
            Log.e("GeminiService", "Error calling Gemini barcode lookup", e)
            null
        }
    }

    private fun parseLookupJson(rawText: String): GeminiProductLookup? {
        try {
            val cleaned = rawText
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val json = JSONObject(cleaned)
            val found = json.optBoolean("found", false)
            val name = json.optString("product_name").trim()
            val brand = json.optString("brand").trim()
            val category = json.optString("category", "General Packaged Product").trim()
            val ingredients = json.optString("ingredients_text").trim()

            if (found && ingredients.isNotBlank()) {
                return GeminiProductLookup(
                    productName = name.ifEmpty { "Product" },
                    brand = brand.ifEmpty { "Retail Brand" },
                    category = category.ifEmpty { "Packaged Product" },
                    ingredientsText = ingredients,
                    isFound = true
                )
            }
            return GeminiProductLookup(
                productName = name,
                brand = brand,
                category = category,
                ingredientsText = "",
                isFound = false
            )
        } catch (e: Exception) {
            Log.w("GeminiService", "Failed to parse Gemini JSON: $rawText", e)
            return null
        }
    }

    suspend fun fetchIngredientsForProduct(productName: String, brand: String, barcode: String): String? = withContext(Dispatchers.IO) {
        val lookup = lookupProductByBarcode(barcode, productName, brand)
        if (lookup != null && lookup.isFound && lookup.ingredientsText.isNotBlank()) {
            return@withContext lookup.ingredientsText
        }
        null
    }

    /**
     * Explains the product analysis and scientific health risks of flagged ingredients.
     * Complies strictly with skills/gemini/SKILL.md:
     * - Gemini is an explanation layer, not the scoring engine.
     * - Explains ingredient evidence and uncertainty.
     * - Details specific health problems for flagged ingredients.
     * - Provides an expert evidence-based fallback synthesis if API key is invalid or network is offline.
     */
    suspend fun explainAnalysis(analysis: ProductAnalysis): String = withContext(Dispatchers.IO) {
        val apiKey = resolveApiKey()

        if (apiKey.isNotBlank()) {
            val prompt = buildString {
                append("You are PureScan's ingredient toxicology scientific communicator.\n")
                append("PureScan has deterministically computed a PureScan Analysis Score of ${analysis.score}/100 for this formulation.\n")
                append("Status: ${analysis.statusLabel}. Total ingredients: ${analysis.totalIngredients}.\n\n")

                if (analysis.flaggedIngredients.isNotEmpty()) {
                    append("FLAGGED INGREDIENTS TO ANALYZE:\n")
                    analysis.flaggedIngredients.forEach { ing ->
                        append("- ${ing.canonicalName} (${ing.concernLevel.uppercase()} concern, Category: ${ing.category}): ")
                        append("${ing.description} ")
                        if (ing.healthProblems.isNotEmpty()) {
                            append("Reported health concerns: ${ing.healthProblems}. ")
                        }
                        if (ing.evidenceSummary.isNotEmpty()) {
                            append("Evidence summary: ${ing.evidenceSummary}. ")
                        }
                        append("\n")
                    }
                } else {
                    append("No ingredients were flagged as high or moderate concern.\n")
                }

                if (analysis.unknownIngredients.isNotEmpty()) {
                    append("\nUncataloged ingredients (not automatically harmful): ${analysis.unknownIngredients.joinToString(", ")}.\n")
                }

                append("\nTASK REQUIREMENTS:\n")
                append("1. Specifically explain the HEALTH PROBLEMS and biological risks associated with the FLAGGED ingredients (e.g., cardiovascular disease from palm/trans fats, insulin resistance/glucose spikes from maida/sugars, gut barrier damage, hyperuricemia from flavor enhancers, hyperactivity/allergic sensitivity from dyes, or potential carcinogen 4-MEI in caramel color).\n")
                append("2. Explain what this means for daily consumer consumption and recommend whole-food or unrefined alternatives.\n")
                append("3. Keep the tone objective, scientific, and clear. Format with concise paragraphs and bullet points for readability. Do NOT diagnose medical conditions.")
            }

            try {
                // gemini-3.5-flash per gemini-api/SKILL.md
                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

                val jsonBody = JSONObject().apply {
                    val contents = JSONArray().apply {
                        val partObj = JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", prompt)
                                })
                            })
                        }
                        put(partObj)
                    }
                    put("contents", contents)
                }

                val request = Request.Builder()
                    .url(url)
                    .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val resBody = response.body?.string() ?: ""
                        val resJson = JSONObject(resBody)
                        val candidates = resJson.optJSONArray("candidates")
                        if (candidates != null && candidates.length() > 0) {
                            val firstCandidate = candidates.getJSONObject(0)
                            val content = firstCandidate.optJSONObject("content")
                            val parts = content?.optJSONArray("parts")
                            if (parts != null && parts.length() > 0) {
                                val text = parts.getJSONObject(0).optString("text").trim()
                                if (text.isNotBlank()) {
                                    return@withContext text
                                }
                            }
                        }
                    } else {
                        val err = response.body?.string() ?: ""
                        Log.e("GeminiService", "Gemini API HTTP ${response.code}: $err")
                    }
                }
            } catch (e: Exception) {
                Log.w("GeminiService", "Gemini live API call encountered error, falling back to scientific synthesis", e)
            }
        }

        // Fallback: Comprehensive, scientifically grounded toxicology synthesis
        // Ensures the user ALWAYS receives a deep explanation of health problems even without active API credentials
        return@withContext buildScientificToxicologySynthesis(analysis)
    }

    private fun buildScientificToxicologySynthesis(analysis: ProductAnalysis): String {
        return buildString {
            if (analysis.flaggedIngredients.isEmpty()) {
                append("Scientific Toxicology Summary:\n\n")
                append("This product formulation demonstrates a clean profile with a PureScan Analysis Score of ${analysis.score}/100. No synthetic dyes, industrial palm oil fractions, trans fats, or high-concern chemical preservatives were detected. Whole-food and minimally processed components predominate, making this formulation suitable for regular dietary consumption.")
                return@buildString
            }

            append("Scientific Health Risk Assessment (Score: ${analysis.score}/100):\n\n")
            append("PureScan evaluated ${analysis.totalIngredients} ingredients and identified ${analysis.flaggedIngredients.size} component(s) with documented health and metabolic concerns:\n\n")

            analysis.flaggedIngredients.forEach { item ->
                append("• ${item.canonicalName} (${item.concernLevel.uppercase()} Concern):\n")
                if (item.healthProblems.isNotEmpty()) {
                    append("  Health Problems: ${item.healthProblems}\n")
                }
                append("  Biological Mechanism: ${item.explanation}\n")
                if (item.evidenceSummary.isNotEmpty()) {
                    append("  Scientific Evidence: ${item.evidenceSummary}\n")
                }
                append("\n")
            }

            append("Daily Consumer Impact & Recommendations:\n")
            when {
                analysis.score < 50 -> {
                    append("This formulation combines multiple high-concern industrial additives and refined lipid/carbohydrate fractions. Regular daily consumption may accelerate metabolic endotoxemia, induce rapid insulin spikes, and promote chronic arterial inflammation. It is recommended to restrict this product to rare occasional intake and substitute with whole-grain, cold-pressed, or home-cooked alternatives.")
                }
                analysis.score < 80 -> {
                    append("This formulation contains moderate-concern processed components. While safe for occasional consumption within regulatory limits, cumulative dietary exposure to refined flours, added sugars, and synthetic stabilizers can alter gut microbiome diversity. Balance your daily diet with fiber-rich whole vegetables, fruits, and unrefined grains.")
                }
                else -> {
                    append("This formulation has minimal toxicological flags. Continue enjoying as part of a balanced diet.")
                }
            }
        }
    }
}
