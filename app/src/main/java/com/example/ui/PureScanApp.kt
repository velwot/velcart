package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.data.GeminiService
import com.example.data.ProductRepository
import com.example.data.ProductResult
import com.example.domain.IngredientAnalysisService
import com.example.model.Product
import com.example.model.ProductAnalysis
import com.example.ui.screens.*
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    object Scanner : Screen()
    data class ProductDetail(val barcode: String) : Screen()
    data class Analyzing(val product: Product, val ingredientsText: String) : Screen()
    data class AnalysisResult(val product: Product, val analysis: ProductAnalysis) : Screen()
    data class ManualEntry(val initialBarcode: String? = null, val initialName: String? = null) : Screen()
    object History : Screen()
    object Search : Screen()
    object Ocr : Screen()
    object Settings : Screen()
}

@Composable
fun PureScanApp() {
    val coroutineScope = rememberCoroutineScope()
    val productRepository = remember { ProductRepository() }
    val geminiService = remember { GeminiService() }
    val ingredientAnalysisService = remember { IngredientAnalysisService() }

    var screenStack by remember { mutableStateOf(listOf<Screen>(Screen.Home)) }
    val currentScreen = screenStack.lastOrNull() ?: Screen.Home

    // Real scan history kept local by default
    val recentScans = remember { mutableStateListOf<Product>() }

    // Active product lookup state
    var activeProductResult by remember { mutableStateOf<ProductResult?>(null) }
    var isProductLoading by remember { mutableStateOf(false) }
    var activeBarcode by remember { mutableStateOf("") }

    fun navigateTo(screen: Screen) {
        screenStack = screenStack + screen
    }

    fun navigateBack() {
        if (screenStack.size > 1) {
            screenStack = screenStack.dropLast(1)
        }
    }

    fun fetchProductAndAnalyze(barcode: String) {
        activeBarcode = barcode
        isProductLoading = true
        activeProductResult = null

        coroutineScope.launch {
            val result = productRepository.getProductByBarcode(barcode)
            activeProductResult = result

            when (result) {
                is ProductResult.Success -> {
                    isProductLoading = false
                    if (recentScans.none { it.barcode == result.product.barcode }) {
                        recentScans.add(0, result.product)
                    }
                    navigateTo(Screen.Analyzing(result.product, result.product.ingredientsText))
                }

                is ProductResult.MissingIngredients -> {
                    // Try looking up packaging ingredients via Gemini for this specific named product
                    val lookup = geminiService.lookupProductByBarcode(
                        barcode = barcode,
                        knownName = result.product.name,
                        knownBrand = result.product.brand
                    )
                    isProductLoading = false

                    if (lookup != null && lookup.isFound && lookup.ingredientsText.isNotBlank()) {
                        val updatedProduct = result.product.copy(
                            name = if (result.product.name.startsWith("Product #") && lookup.productName.isNotBlank()) lookup.productName else result.product.name,
                            brand = if (result.product.brand.isBlank() && lookup.brand.isNotBlank()) lookup.brand else result.product.brand,
                            ingredientsText = lookup.ingredientsText
                        )
                        if (recentScans.none { it.barcode == updatedProduct.barcode }) {
                            recentScans.add(0, updatedProduct)
                        }
                        navigateTo(Screen.Analyzing(updatedProduct, lookup.ingredientsText))
                    } else {
                        if (recentScans.none { it.barcode == result.product.barcode }) {
                            recentScans.add(0, result.product)
                        }
                        navigateTo(Screen.ProductDetail(barcode))
                    }
                }

                is ProductResult.NotFound -> {
                    // Query Gemini strictly for this specific barcode
                    val lookup = geminiService.lookupProductByBarcode(barcode = barcode)
                    isProductLoading = false

                    if (lookup != null && lookup.isFound && lookup.ingredientsText.isNotBlank()) {
                        val product = Product(
                            barcode = barcode,
                            name = lookup.productName.ifEmpty { "Product #$barcode" },
                            brand = lookup.brand.ifEmpty { "Retail Brand" },
                            category = lookup.category.ifEmpty { "Packaged Item" },
                            ingredientsText = lookup.ingredientsText
                        )
                        if (recentScans.none { it.barcode == barcode }) {
                            recentScans.add(0, product)
                        }
                        navigateTo(Screen.Analyzing(product, lookup.ingredientsText))
                    } else {
                        // Do NOT assume or fake ingredients! Show Product Details with Manual Entry option
                        navigateTo(Screen.ProductDetail(barcode))
                    }
                }

                else -> {
                    isProductLoading = false
                    navigateTo(Screen.ProductDetail(barcode))
                }
            }
        }
    }

    // Handle back button for secondary screens
    if (screenStack.size > 1) {
        BackHandler {
            navigateBack()
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (val screen = currentScreen) {
            is Screen.Home -> {
                HomeScreen(
                    recentScans = recentScans,
                    onNavigateToScanner = { navigateTo(Screen.Scanner) },
                    onNavigateToManualEntry = { navigateTo(Screen.ManualEntry()) },
                    onNavigateToSearch = { navigateTo(Screen.Search) },
                    onNavigateToOcr = { navigateTo(Screen.Ocr) },
                    onNavigateToHistory = { navigateTo(Screen.History) },
                    onNavigateToSettings = { navigateTo(Screen.Settings) },
                    onSelectProduct = { barcode ->
                        fetchProductAndAnalyze(barcode)
                    }
                )
            }
            is Screen.Scanner -> {
                ScannerScreen(
                    onNavigateBack = { navigateBack() },
                    onNavigateToManualIngredientEntry = { navigateTo(Screen.ManualEntry()) },
                    onBarcodeScanned = { barcode ->
                        fetchProductAndAnalyze(barcode)
                    }
                )
            }
            is Screen.ManualEntry -> {
                ManualIngredientEntryScreen(
                    initialBarcode = screen.initialBarcode,
                    initialName = screen.initialName,
                    onNavigateBack = { navigateBack() },
                    onAnalyze = { productName, ingredientsText ->
                        val product = Product(
                            barcode = screen.initialBarcode ?: "MANUAL-${System.currentTimeMillis().toString().takeLast(6)}",
                            name = productName,
                            brand = "Manual Entry",
                            category = "Formulation",
                            ingredientsText = ingredientsText
                        )
                        navigateTo(Screen.Analyzing(product, ingredientsText))
                    }
                )
            }
            is Screen.Analyzing -> {
                AnalyzingLoadingScreen(
                    onCancel = { navigateBack() },
                    onComplete = {
                        val analysis = ingredientAnalysisService.analyze(screen.ingredientsText, screen.product.name)
                        if (recentScans.none { it.barcode == screen.product.barcode }) {
                            recentScans.add(0, screen.product)
                        }
                        // Pop analyzing screen and push result
                        screenStack = screenStack.dropLast(1) + Screen.AnalysisResult(screen.product, analysis)
                    }
                )
            }
            is Screen.AnalysisResult -> {
                ProductAnalysisDetailsScreen(
                    product = screen.product,
                    analysis = screen.analysis,
                    onNavigateToHistory = { navigateTo(Screen.History) },
                    onScanAnotherProduct = {
                        screenStack = listOf(Screen.Home, Screen.Scanner)
                    }
                )
            }
            is Screen.ProductDetail -> {
                ProductScreen(
                    barcode = activeBarcode.ifEmpty { screen.barcode },
                    result = activeProductResult,
                    isLoading = isProductLoading,
                    onRetry = {
                        fetchProductAndAnalyze(activeBarcode.ifEmpty { screen.barcode })
                    },
                    onNavigateBack = { navigateBack() },
                    onNavigateToOcr = { navigateTo(Screen.Ocr) },
                    onNavigateToManualEntry = {
                        val currentName = when (val res = activeProductResult) {
                            is ProductResult.MissingIngredients -> res.product.name
                            is ProductResult.Success -> res.product.name
                            else -> null
                        }
                        navigateTo(Screen.ManualEntry(initialBarcode = activeBarcode, initialName = currentName))
                    },
                    onFetchViaGemini = { product ->
                        isProductLoading = true
                        coroutineScope.launch {
                            val lookup = geminiService.lookupProductByBarcode(
                                barcode = product.barcode,
                                knownName = product.name,
                                knownBrand = product.brand
                            )
                            isProductLoading = false

                            if (lookup != null && lookup.isFound && lookup.ingredientsText.isNotBlank()) {
                                val updated = product.copy(
                                    name = if (product.name.startsWith("Product #") && lookup.productName.isNotBlank()) lookup.productName else product.name,
                                    brand = if (product.brand == "Indian Market Item" && lookup.brand.isNotBlank()) lookup.brand else product.brand,
                                    ingredientsText = lookup.ingredientsText
                                )
                                if (recentScans.none { it.barcode == updated.barcode }) {
                                    recentScans.add(0, updated)
                                }
                                navigateTo(Screen.Analyzing(updated, lookup.ingredientsText))
                            } else {
                                // If not recognized by Gemini, route to Manual Entry with prefilled product details
                                navigateTo(
                                    Screen.ManualEntry(
                                        initialBarcode = product.barcode,
                                        initialName = if (product.name.startsWith("Product #")) "" else product.name
                                    )
                                )
                            }
                        }
                    },
                    onAnalyzeIngredients = { product, ingredientsText ->
                        navigateTo(Screen.Analyzing(product, ingredientsText))
                    }
                )
            }
            is Screen.History -> {
                HistoryScreen(
                    history = recentScans,
                    onNavigateBack = { navigateBack() },
                    onNavigateToScanner = { navigateTo(Screen.Scanner) },
                    onClearHistory = { recentScans.clear() },
                    onSelectProduct = { barcode ->
                        fetchProductAndAnalyze(barcode)
                    }
                )
            }
            is Screen.Search -> {
                SearchScreen(
                    onNavigateBack = { navigateBack() },
                    onNavigateToScanner = { navigateTo(Screen.Scanner) },
                    onSelectProduct = { barcode ->
                        fetchProductAndAnalyze(barcode)
                    }
                )
            }
            is Screen.Ocr -> {
                OcrScreen(
                    onNavigateBack = { navigateBack() },
                    onAnalyzeIngredients = { ingredientsText ->
                        val product = Product(
                            barcode = "OCR-${System.currentTimeMillis().toString().takeLast(6)}",
                            name = "OCR Label Product",
                            brand = "Packaging Scan",
                            category = "Packaging Scan",
                            ingredientsText = ingredientsText
                        )
                        navigateTo(Screen.Analyzing(product, ingredientsText))
                    }
                )
            }
            is Screen.Settings -> {
                SettingsScreen(
                    onNavigateBack = { navigateBack() },
                    onClearHistory = { recentScans.clear() }
                )
            }
        }
    }
}
