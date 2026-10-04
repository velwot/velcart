package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GeminiService
import com.example.model.EvaluatedIngredient
import com.example.model.Product
import com.example.model.ProductAnalysis
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProductAnalysisDetailsScreen(
    product: Product,
    analysis: ProductAnalysis,
    geminiService: GeminiService = remember { GeminiService() },
    onNavigateToHistory: () -> Unit,
    onScanAnotherProduct: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isDetailsExpanded by remember { mutableStateOf(false) }
    var expandedIngredientNames by remember { mutableStateOf(setOf<String>()) }
    var geminiExplanation by remember { mutableStateOf<String?>(null) }
    var isGeminiLoading by remember { mutableStateOf(false) }

    // Proactively load Gemini explanation or scientific synthesis so it's ready when user opens details
    LaunchedEffect(analysis) {
        if (geminiExplanation == null) {
            isGeminiLoading = true
            geminiExplanation = geminiService.explainAnalysis(analysis)
            isGeminiLoading = false
        }
    }

    val statusColor = when (analysis.statusLabel) {
        "CLEAN" -> Color(0xFF10E575)
        "CAUTION" -> Color(0xFFFFAA00)
        else -> Color(0xFFEF4444)
    }

    Scaffold(
        containerColor = Color(0xFF0C0C0C),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // "= HISTORY" pill button
                Surface(
                    onClick = onNavigateToHistory,
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF1E1E1E),
                    modifier = Modifier.height(36.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = null,
                            tint = Color(0xFF9E9E9E),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "HISTORY",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = Color(0xFFCCCCCC)
                        )
                    }
                }

                // Flag icon button
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF1E1E1E),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Flag,
                            contentDescription = "Report",
                            tint = Color(0xFF757575),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Coral / Red "Scan another product →" button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0C0C0C))
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = onScanAnotherProduct,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("scan_another_product_button")
                ) {
                    Text(
                        text = "Scan another product",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Giant Status Header: "CAUTION", "CLEAN", "AVOID"
            Text(
                text = analysis.statusLabel,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 44.sp,
                    letterSpacing = 2.sp
                ),
                color = statusColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle: "PURESCAN ANALYSIS SCORE" (Per AGENTS.md)
            Text(
                text = "PURESCAN ANALYSIS SCORE",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = Color(0xFF888888)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Dynamic Deterministic Score: e.g. "76/100"
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "${analysis.score}",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 62.sp
                    ),
                    color = statusColor
                )
                Text(
                    text = "/100",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 32.sp
                    ),
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Product Name
            Text(
                text = product.name.uppercase(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = Color(0xFF222222), thickness = 1.dp)

            Spacer(modifier = Modifier.height(20.dp))

            // Product Icon Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1A1A1A),
                modifier = Modifier
                    .size(width = 240.dp, height = 180.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.Fastfood,
                        contentDescription = "Product Image",
                        tint = statusColor,
                        modifier = Modifier.size(72.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Key Concerns Highlights
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                analysis.keyConcerns.forEach { concern ->
                    Text(
                        text = concern,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // "Details" / "Hide details" Pill Button
            Surface(
                onClick = { isDetailsExpanded = !isDetailsExpanded },
                shape = RoundedCornerShape(24.dp),
                color = Color.Transparent,
                modifier = Modifier
                    .border(1.5.dp, Color(0xFF10E575), RoundedCornerShape(24.dp))
                    .height(42.dp)
                    .testTag("toggle_details_button")
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isDetailsExpanded) "Hide details" else "Details",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF10E575)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Expanded Details Section
            AnimatedVisibility(visible = isDetailsExpanded) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Summary Narrative Card
                    Text(
                        text = analysis.summaryNarrative,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 22.sp,
                            fontSize = 15.sp
                        ),
                        color = Color(0xFFD1D5DB)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // AI Scientific Explanation Card (Per skills/gemini/SKILL.md)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF161F1A),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10E575).copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFF10E575),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "AI Scientific Explanation & Toxicology",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Gemini synthesizes toxicological, metabolic, and epidemiological health evidence for consumer daily intake. Gemini does not calculate or modify the PureScan Analysis Score.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF9E9E9E)
                            )

                            if (geminiExplanation != null) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF0F1410),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1F2E23)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = geminiExplanation!!,
                                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                        color = Color(0xFFE2E8F0),
                                        modifier = Modifier.padding(14.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedButton(
                                onClick = {
                                    if (!isGeminiLoading) {
                                        isGeminiLoading = true
                                        coroutineScope.launch {
                                            geminiExplanation = geminiService.explainAnalysis(analysis)
                                            isGeminiLoading = false
                                        }
                                    }
                                },
                                enabled = !isGeminiLoading,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF10E575)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (isGeminiLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp,
                                        color = Color(0xFF10E575)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Consulting Scientific Evidence...")
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (geminiExplanation == null) "Explain Findings with Gemini" else "Refresh AI Explanation")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // FLAGGED CONCERNS SECTION (With Prominent Health Problems)
                    if (analysis.flaggedIngredients.isNotEmpty()) {
                        Text(
                            text = "FLAGGED CONCERNS & HEALTH PROBLEMS (${analysis.flaggedIngredients.size})",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            ),
                            color = Color(0xFFFF5252)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            analysis.flaggedIngredients.forEach { item ->
                                FlaggedIngredientCard(ingredient = item)
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    // ALL INGREDIENTS Section
                    Text(
                        text = "ALL INGREDIENTS (${analysis.allIngredients.size})",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        ),
                        color = Color(0xFF6B7280)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        analysis.allIngredients.forEach { item ->
                            IngredientRow(
                                ingredient = item,
                                isExpanded = expandedIngredientNames.contains(item.name),
                                onToggle = {
                                    expandedIngredientNames = if (expandedIngredientNames.contains(item.name)) {
                                        expandedIngredientNames - item.name
                                    } else {
                                        expandedIngredientNames + item.name
                                    }
                                }
                            )
                        }
                    }

                    // UNCATALOGED INGREDIENTS Section (Unknown is not harmful per AGENTS.md)
                    if (analysis.unknownIngredients.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = "UNCATALOGED INGREDIENTS (${analysis.unknownIngredients.size})",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            ),
                            color = Color(0xFF38BDF8)
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Unknown ingredients are not automatically harmful (0 penalty applied).",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            analysis.unknownIngredients.forEach { name ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF1E293B),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                                ) {
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        color = Color(0xFFE2E8F0),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

/**
 * Dedicated Card for Flagged Ingredients with Explicit Health Problems Callout
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlaggedIngredientCard(ingredient: EvaluatedIngredient) {
    val isHighConcern = ingredient.concernLevel.equals("high", true)
    val cardBorderColor = if (isHighConcern) Color(0xFFEF4444) else Color(0xFFFFAA00)
    val badgeBgColor = if (isHighConcern) Color(0xFFEF4444) else Color(0xFFFFAA00)

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF1A1313),
        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorderColor.copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Name + Score/Concern Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ingredient.canonicalName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = ingredient.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9E9E9E)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeBgColor.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, badgeBgColor)
                ) {
                    Text(
                        text = "${ingredient.concernLevel.uppercase()} CONCERN",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = badgeBgColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Health Risks Tag Chips
            if (ingredient.healthRisks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ingredient.healthRisks.forEach { risk ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF2C1515),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFEF4444).copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = risk,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                color = Color(0xFFFFA4A4),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // Associated Health Problems Callout Box
            if (ingredient.healthProblems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF231414),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Associated Health Problems:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFFF6B6B)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = ingredient.healthProblems,
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                color = Color(0xFFE2E8F0)
                            )
                        }
                    }
                }
            }

            // Biological Mechanism & Toxicological Description
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = ingredient.explanation,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                color = Color(0xFFB0B0B0)
            )

            // Evidence Summary
            if (ingredient.evidenceSummary.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Evidence: ${ingredient.evidenceSummary}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = Color(0xFF888888)
                )
            }
        }
    }
}

@Composable
private fun IngredientRow(
    ingredient: EvaluatedIngredient,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val itemDotColor = when (ingredient.concernLevel.lowercase()) {
        "high" -> Color(0xFFFF4D4D)
        "moderate" -> Color(0xFFFFAA00)
        "low" -> Color(0xFF38BDF8)
        else -> Color(0xFF10E575)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dot + Ingredient Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(itemDotColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = ingredient.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 17.sp
                    ),
                    color = Color.White
                )
            }

            // Score + Arrow (e.g. "35 >" or "70 v")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${ingredient.score}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = itemDotColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isExpanded) "v" else ">",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    color = itemDotColor
                )
            }
        }

        // Accordion Details
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 22.dp, top = 8.dp, bottom = 4.dp)
            ) {
                Text(
                    text = "${ingredient.category} • Concern: ${ingredient.concernLevel.uppercase()}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = itemDotColor
                )

                // Health Problems in Accordion (if present)
                if (ingredient.healthProblems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Health Problems: ${ingredient.healthProblems}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = Color(0xFFFF8A80)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = ingredient.explanation,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 20.sp,
                        fontSize = 14.sp
                    ),
                    color = Color(0xFFB0B0B0)
                )
                if (ingredient.evidenceSummary.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Evidence: ${ingredient.evidenceSummary}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF888888)
                    )
                }
            }
        }
    }
}
