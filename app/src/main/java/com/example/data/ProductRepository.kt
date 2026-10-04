package com.example.data

import android.util.Log
import com.example.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

sealed class ProductResult {
    data class Success(val product: Product) : ProductResult()
    data class MissingIngredients(val product: Product) : ProductResult()
    data class NotFound(val barcode: String) : ProductResult()
    data class NetworkError(val message: String) : ProductResult()
    data class UnknownError(val message: String) : ProductResult()
}

class ProductRepository(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
) {

    suspend fun getProductByBarcode(barcode: String): ProductResult = withContext(Dispatchers.IO) {
        val cleanBarcode = barcode.trim()
        if (cleanBarcode.isEmpty()) {
            return@withContext ProductResult.UnknownError("Barcode cannot be empty")
        }

        // 1. Primary: Open Food Facts India (https://in.openfoodfacts.org)
        val indiaResult = fetchFromEndpoint("https://in.openfoodfacts.org/api/v2/product/$cleanBarcode.json", cleanBarcode)
        if (indiaResult is ProductResult.Success) {
            return@withContext indiaResult
        }

        // 2. Open Food Facts India v0 endpoint
        val indiaV0Result = fetchFromEndpoint("https://in.openfoodfacts.org/api/v0/product/$cleanBarcode.json", cleanBarcode)
        if (indiaV0Result is ProductResult.Success) {
            return@withContext indiaV0Result
        }

        // 3. Global Open Food Facts
        val globalFoodResult = fetchFromEndpoint("https://world.openfoodfacts.org/api/v2/product/$cleanBarcode.json", cleanBarcode)
        if (globalFoodResult is ProductResult.Success) {
            return@withContext globalFoodResult
        }

        // 4. Open Beauty Facts
        val beautyResult = fetchFromEndpoint("https://world.openbeautyfacts.org/api/v2/product/$cleanBarcode.json", cleanBarcode)
        if (beautyResult is ProductResult.Success) {
            return@withContext beautyResult
        }

        // Check if any returned MissingIngredients
        val missingResult = listOf(indiaResult, indiaV0Result, globalFoodResult, beautyResult)
            .filterIsInstance<ProductResult.MissingIngredients>()
            .firstOrNull()
        if (missingResult != null) {
            return@withContext missingResult
        }

        // Check for Network Error
        val networkErr = listOf(indiaResult, indiaV0Result, globalFoodResult, beautyResult)
            .filterIsInstance<ProductResult.NetworkError>()
            .firstOrNull()
        if (networkErr != null) {
            return@withContext networkErr
        }

        return@withContext ProductResult.NotFound(cleanBarcode)
    }

    suspend fun searchProducts(query: String): List<Product> = withContext(Dispatchers.IO) {
        val clean = query.trim()
        if (clean.isEmpty()) return@withContext emptyList()

        try {
            val url = "https://in.openfoodfacts.org/cgi/search.pl?search_terms=${clean}&search_simple=1&action=process&json=1&page_size=20"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "PureScan-Android - OpenFoodFacts India Client")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext emptyList()
                val body = response.body?.string() ?: return@withContext emptyList()
                val json = JSONObject(body)
                val productsArray = json.optJSONArray("products") ?: return@withContext emptyList()

                val results = mutableListOf<Product>()
                for (i in 0 until productsArray.length()) {
                    val p = productsArray.getJSONObject(i)
                    val code = p.optString("code")
                    if (code.isBlank()) continue

                    val name = p.optString("product_name").ifBlank {
                        p.optString("product_name_en").ifBlank { "Indian Product #$code" }
                    }
                    val brand = p.optString("brands").ifBlank {
                        p.optString("brand_owner").ifBlank { "Indian Brand" }
                    }
                    val category = p.optString("categories").split(",").firstOrNull()?.trim() ?: "Food & Grocery"
                    val imageUrl = p.optString("image_url").ifBlank {
                        p.optString("image_front_url").ifBlank { null }
                    }
                    val ingredients = p.optString("ingredients_text").ifBlank {
                        p.optString("ingredients_text_en").ifBlank { "" }
                    }

                    results.add(
                        Product(
                            barcode = code,
                            name = name,
                            brand = brand,
                            imageUrl = imageUrl,
                            category = category,
                            ingredientsText = ingredients
                        )
                    )
                }
                results
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Search error on in.openfoodfacts.org", e)
            emptyList()
        }
    }

    private fun fetchFromEndpoint(url: String, barcode: String): ProductResult {
        return try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "PureScan-Android - OpenFoodFacts India Client")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    if (response.code == 404) {
                        return ProductResult.NotFound(barcode)
                    }
                    return ProductResult.NetworkError("Server responded with HTTP ${response.code}")
                }

                val body = response.body?.string() ?: return ProductResult.UnknownError("Empty server response")
                val json = JSONObject(body)

                val status = json.optInt("status", 0)
                if (status != 1 || !json.has("product")) {
                    return ProductResult.NotFound(barcode)
                }

                val productJson = json.getJSONObject("product")
                val name = productJson.optString("product_name").ifBlank {
                    productJson.optString("product_name_en").ifBlank {
                        productJson.optString("generic_name").ifBlank { "Product #$barcode" }
                    }
                }

                val brand = productJson.optString("brands").ifBlank {
                    productJson.optString("brand_owner").ifBlank { "Indian Brand" }
                }

                val category = productJson.optString("categories").split(",").firstOrNull()?.trim()
                    ?.ifBlank { "Indian Packaged Food" } ?: "Indian Packaged Food"

                val imageUrl = productJson.optString("image_url").ifBlank {
                    productJson.optString("image_front_url").ifBlank { null }
                }

                val ingredientsText = productJson.optString("ingredients_text").ifBlank {
                    productJson.optString("ingredients_text_en").ifBlank {
                        productJson.optString("ingredients_text_with_allergens").ifBlank { "" }
                    }
                }.trim()

                val product = Product(
                    barcode = barcode,
                    name = name,
                    brand = brand,
                    imageUrl = imageUrl,
                    category = category,
                    ingredientsText = ingredientsText
                )

                if (ingredientsText.isBlank()) {
                    ProductResult.MissingIngredients(product)
                } else {
                    ProductResult.Success(product)
                }
            }
        } catch (e: IOException) {
            Log.e("ProductRepository", "Network error fetching product from $url", e)
            ProductResult.NetworkError("Network connection error: ${e.localizedMessage ?: "Failed to connect to in.openfoodfacts.org"}")
        } catch (e: Exception) {
            Log.e("ProductRepository", "Parsing error", e)
            ProductResult.UnknownError("Error processing product data: ${e.localizedMessage}")
        }
    }
}
