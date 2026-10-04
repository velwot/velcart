package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun AnalyzingLoadingScreen(
    onCancel: () -> Unit,
    onComplete: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }

    // Sequential step advancement
    LaunchedEffect(Unit) {
        delay(400)
        currentStep = 2
        delay(500)
        currentStep = 3
        delay(550)
        currentStep = 4
        delay(500)
        currentStep = 5
        delay(550)
        onComplete()
    }

    // Continuous rotation for outer dashed circle
    val infiniteTransition = rememberInfiniteTransition(label = "ring_rotation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Pulsing alpha for active step
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val neonGreen = Color(0xFF10E575)
    val mutedGray = Color(0xFF6B7280)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        // Top Close "X" Button
        IconButton(
            onClick = onCancel,
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(44.dp)
                .background(Color(0xFF1E1E1E), CircleShape)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Cancel Analysis",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        // Center Content
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Neon Emblem with rotating dashed border
            Box(
                modifier = Modifier.size(130.dp),
                contentAlignment = Alignment.Center
            ) {
                // Rotating dashed outer ring
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(rotationAngle)
                ) {
                    drawCircle(
                        color = neonGreen,
                        style = Stroke(
                            width = 3.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 16f), 0f)
                        )
                    )
                }

                // Inner solid circle
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF0F291E),
                    modifier = Modifier
                        .size(100.dp)
                        .border(1.5.dp, neonGreen.copy(alpha = 0.6f), CircleShape)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Outlined.WarningAmber,
                            contentDescription = "Warning Indicator",
                            tint = neonGreen,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Glowing "ANALYZING" Title
            Text(
                text = "A N A L Y Z I N G",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 6.sp
                ),
                color = neonGreen,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Animated Checklist Steps
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                ChecklistStep(
                    label = "BARCODE SCAN",
                    stepNumber = 1,
                    currentStep = currentStep,
                    neonGreen = neonGreen,
                    mutedGray = mutedGray,
                    pulseAlpha = pulseAlpha
                )
                ChecklistStep(
                    label = "DATABASE SEARCH",
                    stepNumber = 2,
                    currentStep = currentStep,
                    neonGreen = neonGreen,
                    mutedGray = mutedGray,
                    pulseAlpha = pulseAlpha
                )
                ChecklistStep(
                    label = "INGREDIENT READ",
                    stepNumber = 3,
                    currentStep = currentStep,
                    neonGreen = neonGreen,
                    mutedGray = mutedGray,
                    pulseAlpha = pulseAlpha
                )
                ChecklistStep(
                    label = "TOXICOLOGY ANALYSIS",
                    stepNumber = 4,
                    currentStep = currentStep,
                    neonGreen = neonGreen,
                    mutedGray = mutedGray,
                    pulseAlpha = pulseAlpha
                )
                ChecklistStep(
                    label = "TOXSCORE CALCULATION",
                    stepNumber = 5,
                    currentStep = currentStep,
                    neonGreen = neonGreen,
                    mutedGray = mutedGray,
                    pulseAlpha = pulseAlpha
                )
            }
        }
    }
}

@Composable
private fun ChecklistStep(
    label: String,
    stepNumber: Int,
    currentStep: Int,
    neonGreen: Color,
    mutedGray: Color,
    pulseAlpha: Float
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                stepNumber < currentStep -> {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = neonGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                stepNumber == currentStep -> {
                    // Active pulsing vertical dotted loader
                    Text(
                        text = "⠇",
                        color = Color.White.copy(alpha = pulseAlpha),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                else -> {
                    // Pending bullet dot
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(mutedGray, CircleShape)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(18.dp))

        val textColor = when {
            stepNumber < currentStep -> neonGreen
            stepNumber == currentStep -> Color.White
            else -> mutedGray
        }

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (stepNumber <= currentStep) FontWeight.Bold else FontWeight.Medium,
                letterSpacing = 2.sp
            ),
            color = textColor
        )
    }
}
