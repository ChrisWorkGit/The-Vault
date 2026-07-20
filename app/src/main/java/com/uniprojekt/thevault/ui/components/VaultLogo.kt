// PROMPT-REFERENZ: [REF-FEATURE-APP-LOGO-INTEGRATION]
package com.uniprojekt.thevault.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uniprojekt.thevault.ui.theme.*

// AI-Generated: Cyberpunk Logo Component & App Launcher Icon

/**
 * Das offizielle "The Vault" Cyberpunk-Logo als Composable.
 * Enthält ein zentrales "V"-Emblem, Viewfinder-Ecken und ein Gitternetz.
 * Das Logo nutzt einen dezent pulsierenden Neon-Glow-Effekt.
 *
 * @param modifier Der Modifier für das Äußere des Logos.
 * @param showText Falls true, wird "THE VAULT // SYSTEM_LOCKED" unter dem Logo angezeigt.
 */
@Composable
fun VaultLogo(
    modifier: Modifier = Modifier,
    showText: Boolean = true
) {
    // Animation für den pulsierenden Neon-Glow (Sanftes Atmen des Grüns)
    val infiniteTransition = rememberInfiniteTransition(label = "VaultLogoGlow")
    val glowIntensity by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowIntensity"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo Container mit CyberpunkCutShape
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(CyberBackground, CyberpunkCutShape(40f))
                .border(1.dp, DarkGreen, CyberpunkCutShape(40f))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            // 1. Hintergrund-Grid (Statisch)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridCells = 4
                val step = size.width / gridCells
                // Horizontale und vertikale Linien für den technischen Look
                for (i in 0..gridCells) {
                    drawLine(
                        color = DarkGreen.copy(alpha = 0.4f),
                        start = Offset(i * step, 0f),
                        end = Offset(i * step, size.height),
                        strokeWidth = 1f
                    )
                    drawLine(
                        color = DarkGreen.copy(alpha = 0.4f),
                        start = Offset(0f, i * step),
                        end = Offset(size.width, i * step),
                        strokeWidth = 1f
                    )
                }
            }

            // 2. Fadenkreuz-Ecken und "V"-Emblem (Mit Glow)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokePx = 2.dp.toPx()
                val cornerSize = 25.dp.toPx()

                // --- Ecken zeichnen ---
                val cornerPath = Path().apply {
                    // Oben Links
                    moveTo(0f, cornerSize); lineTo(0f, 0f); lineTo(cornerSize, 0f)
                    // Oben Rechts
                    moveTo(size.width - cornerSize, 0f); lineTo(size.width, 0f); lineTo(size.width, cornerSize)
                    // Unten Links
                    moveTo(0f, size.height - cornerSize); lineTo(0f, size.height); lineTo(cornerSize, size.height)
                    // Unten Rechts
                    moveTo(size.width - cornerSize, size.height); lineTo(size.width, size.height); lineTo(size.width, size.height - cornerSize)
                }

                // Äußerer Glow der Ecken
                drawPath(
                    path = cornerPath,
                    color = NeonGreen.copy(alpha = 0.2f * glowIntensity),
                    style = Stroke(width = strokePx * 3f, cap = StrokeCap.Round)
                )
                // Kern der Ecken
                drawPath(
                    path = cornerPath,
                    color = NeonGreen,
                    style = Stroke(width = strokePx, cap = StrokeCap.Square)
                )

                // --- Zentrales V-Emblem ---
                val vPath = Path().apply {
                    moveTo(size.width * 0.3f, size.height * 0.35f)
                    lineTo(size.width * 0.5f, size.height * 0.7f)
                    lineTo(size.width * 0.7f, size.height * 0.35f)
                    lineTo(size.width * 0.62f, size.height * 0.35f)
                    lineTo(size.width * 0.5f, size.height * 0.55f)
                    lineTo(size.width * 0.38f, size.height * 0.35f)
                    close()
                }

                // Pulsierender Halo-Effekt um das V
                drawPath(
                    path = vPath,
                    color = NeonGreen.copy(alpha = 0.4f * glowIntensity),
                    style = Stroke(width = strokePx * 6f)
                )
                // Das solide V-Emblem
                drawPath(
                    path = vPath,
                    color = NeonGreen
                )
            }
        }

        // Optionaler Text-Bereich
        if (showText) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "THE VAULT",
                color = NeonGreen,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 4.sp,
                style = cyberpunkGlowStyle(NeonGreen, radius = 12f * glowIntensity)
            )
            Text(
                text = "// SYSTEM_LOCKED",
                color = TextGreen.copy(alpha = 0.8f),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
