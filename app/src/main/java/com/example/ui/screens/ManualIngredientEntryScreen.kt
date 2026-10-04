package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualIngredientEntryScreen(
    initialBarcode: String? = null,
    initialName: String? = null,
    onNavigateBack: () -> Unit,
    onAnalyze: (productName: String, ingredientsText: String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var productName by remember(initialName) { mutableStateOf(initialName ?: "") }
    var ingredientsText by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color(0xFF0C0C0C),
        topBar = {
            TopAppBar(
                title = { Text("Manual Ingredient Analysis", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0C0C0C))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header Hero Banner
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF161616),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF10E575).copy(alpha = 0.15f),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Outlined.EditNote,
                                contentDescription = null,
                                tint = Color(0xFF10E575),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Barcode Failed or Unavailable?",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Type or paste the ingredient list printed on product packaging to evaluate safety.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF9E9E9E)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Product Name (Optional)
            Text(
                text = "Product Name (Optional)",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFFCCCCCC)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = productName,
                onValueChange = { productName = it },
                placeholder = { Text("e.g. Ratlami Sev, Botanical Face Wash...", color = Color(0xFF666666)) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF10E575),
                    unfocusedBorderColor = Color(0xFF2A2A2A),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF141414),
                    unfocusedContainerColor = Color(0xFF141414)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Ingredient List Text Area Header with Paste button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ingredient List *",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFCCCCCC)
                )

                Row {
                    if (ingredientsText.isNotEmpty()) {
                        TextButton(onClick = { ingredientsText = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null, tint = Color(0xFF9E9E9E), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear", color = Color(0xFF9E9E9E), fontSize = 12.sp)
                        }
                    }

                    TextButton(
                        onClick = {
                            clipboardManager.getText()?.text?.let { clipText ->
                                if (clipText.isNotBlank()) {
                                    ingredientsText = clipText
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.ContentPaste, contentDescription = null, tint = Color(0xFF10E575), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Paste", color = Color(0xFF10E575), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = ingredientsText,
                onValueChange = { ingredientsText = it },
                placeholder = {
                    Text(
                        "Paste ingredients separated by commas or newlines:\n\ne.g. Cornflour, Salt, Sugar, Whey Powder, Butter, Edible Vegetable Oil, Rice Bran Oil, Maltodextrin, Turmeric, Garlic Powder...",
                        color = Color(0xFF555555),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                },
                minLines = 6,
                maxLines = 10,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF10E575),
                    unfocusedBorderColor = Color(0xFF2A2A2A),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF141414),
                    unfocusedContainerColor = Color(0xFF141414)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("manual_ingredients_input")
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Samples for Instant Testing
            Text(
                text = "Quick Sample Formulations",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF888888)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuggestionChip(
                    onClick = {
                        productName = "Ratlami Sev"
                        ingredientsText = "Cornflour, Salt, Sugar, Whey Powder, Butter, Edible Vegetable Oil, Rice Bran Oil, Maltodextrin, Yeast Extract, Corn Flour, Turmeric, Garlic Powder, Onion Powder, Chili Powder, Citric Acid, Ginger, Cumin, Coriander, Clove, Cinnamon, Black Pepper, Nutmeg"
                    },
                    label = { Text("Ratlami Sev", fontSize = 11.sp, color = Color.White) },
                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFF1F1F1F))
                )

                SuggestionChip(
                    onClick = {
                        productName = "Hydrating Facial Foam"
                        ingredientsText = "Water, Glycerin, Sodium Lauryl Sulfate, Methylparaben, Propylparaben, Fragrance, Tocopherol, Niacinamide"
                    },
                    label = { Text("Facial Foam", fontSize = 11.sp, color = Color.White) },
                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFF1F1F1F))
                )

                SuggestionChip(
                    onClick = {
                        productName = "Pure Botanical Balm"
                        ingredientsText = "Water, Glycerin, Niacinamide, Sodium Hyaluronate, Tocopherol, Turmeric, Ginger"
                    },
                    label = { Text("Clean Balm", fontSize = 11.sp, color = Color.White) },
                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFF1F1F1F))
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // "Analyze Ingredients" Submit Button
            Button(
                onClick = {
                    val finalName = productName.trim().ifEmpty { "Custom Product" }
                    val finalIngredients = ingredientsText.trim()
                    if (finalIngredients.isNotEmpty()) {
                        onAnalyze(finalName, finalIngredients)
                    }
                },
                enabled = ingredientsText.trim().isNotEmpty(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10E575),
                    disabledContainerColor = Color(0xFF222222)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("analyze_manual_ingredients_button")
            ) {
                Icon(Icons.Outlined.Shield, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Analyze Ingredients",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
            }
        }
    }
}
