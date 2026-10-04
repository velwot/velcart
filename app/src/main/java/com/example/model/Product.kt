package com.example.model

data class Product(
    val barcode: String,
    val name: String,
    val brand: String,
    val imageUrl: String? = null,
    val category: String,
    val ingredientsText: String
)

data class EvaluatedIngredient(
    val name: String,
    val canonicalName: String,
    val concernLevel: String, // "none", "low", "moderate", "high"
    val score: Int, // 0 to 100 individual rating
    val isFlagged: Boolean,
    val explanation: String,
    val category: String = "General",
    val description: String = explanation,
    val evidenceSummary: String = "",
    val sources: List<String> = emptyList(),
    val healthProblems: String = "",
    val healthRisks: List<String> = emptyList()
)

data class ProductAnalysis(
    val score: Int,
    val statusLabel: String = "CAUTION", // "CLEAN", "CAUTION", "AVOID"
    val keyConcerns: List<String> = emptyList(),
    val summaryNarrative: String = "",
    val totalIngredients: Int,
    val flaggedIngredients: List<EvaluatedIngredient> = emptyList(),
    val allIngredients: List<EvaluatedIngredient> = emptyList(),
    val matchedIngredients: List<EvaluatedIngredient> = allIngredients,
    val unknownIngredients: List<String> = emptyList()
)
