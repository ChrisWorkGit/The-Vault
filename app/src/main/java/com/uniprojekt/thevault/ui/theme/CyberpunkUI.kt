// PROMPT-REFERENZ: [REF-ISSUE20-CYBERPUNK-THEME]
// PROMPT-REFERENZ: [REF-FEATURE-APP-LOGO-INTEGRATION]
// PROMPT-REFERENZ: [REF-ISSUE47-MINIGAME-BUGS-FIX]
package com.uniprojekt.thevault.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// AI-Generated: Cyberpunk Design System & Neon UI Layer
// AI-Generated: Cyberpunk Logo Component & App Launcher Icon
// AI-Generated: Fix minigame lifecycle permissions, notification overload mechanics, and decibel bypass exploit

// AI-Generated: Cyberpunk Design System & Neon UI Layer
// AI-Generated: Cyberpunk Logo Component & App Launcher Icon

/**
 * Eine schräge Parallelogramm-Form für Buttons im Cyberpunk-Stil.
 * @param skewWidth Der Versatz der Schrägstellung in Pixeln.
 */
class CyberpunkShape(private val skewWidth: Float = 40f) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(skewWidth, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width - skewWidth, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}

/**
 * Eine Form mit abgeschnittenen Ecken für Container im Cyberpunk-Stil.
 * @param cornerSize Die Größe der abgeschnittenen Ecken in Pixeln.
 */
class CyberpunkCutShape(private val cornerSize: Float = 30f) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(cornerSize, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - cornerSize)
            lineTo(size.width - cornerSize, size.height)
            lineTo(0f, size.height)
            lineTo(0f, cornerSize)
            close()
        }
        return Outline.Generic(path)
    }
}


/**
 * Erzeugt einen leuchtenden Text-Stil (Neon Glow).
 */
fun cyberpunkGlowStyle(color: Color, radius: Float = 10f) = TextStyle(
    shadow = Shadow(
        color = color,
        blurRadius = radius
    )
)

/**
 * Ein Modifier, der CRT-Scanlines und ein Gitternetz (Grid) zeichnet.
 */
fun Modifier.crtOverlay(): Modifier = this.then(
    Modifier.drawBehind {
        val gridStep = 40.dp.toPx()
        val scanlineStep = 4.dp.toPx()
        
        // Zeichne Hintergrund-Grid
        for (x in 0..(size.width / gridStep).toInt()) {
            drawLine(
                color = DarkGreen.copy(alpha = 0.3f),
                start = Offset(x * gridStep, 0f),
                end = Offset(x * gridStep, size.height),
                strokeWidth = 1f
            )
        }
        for (y in 0..(size.height / gridStep).toInt()) {
            drawLine(
                color = DarkGreen.copy(alpha = 0.3f),
                start = Offset(0f, y * gridStep),
                end = Offset(size.width, y * gridStep),
                strokeWidth = 1f
            )
        }

        // Zeichne horizontale Scanlines
        for (y in 0..(size.height / scanlineStep).toInt()) {
            drawLine(
                color = Color.Black.copy(alpha = 0.2f),
                start = Offset(0f, y * scanlineStep),
                end = Offset(size.width, y * scanlineStep),
                strokeWidth = 2f
            )
        }
    }
)

/**
 * Ein Modifier, der einen Neon-Leuchteffekt hinzufügt.
 */
fun Modifier.neonGlow(color: Color = NeonGreen, radius: Float = 20f): Modifier = this.then(
    Modifier.drawBehind {
        val paint = Paint().asFrameworkPaint().apply {
            setShadowLayer(radius, 0f, 0f, color.toArgb())
        }
        drawContext.canvas.nativeCanvas.drawRect(0f, 0f, size.width, size.height, paint)
    }
)

/**
 * Zeigt ein Lade-Overlay, das auf den Spielstart durch andere Agenten wartet.
 * Wird angezeigt, wenn der lokale Spieler bereit ist (z.B. Berechtigungen erteilt),
 * aber das Spiel noch nicht für alle gestartet wurde.
 */
@Composable
fun WaitingForTeamOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .crtOverlay(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            CircularProgressIndicator(color = NeonGreen, strokeWidth = 3.dp)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "ESTABLISHING UPLINK...",
                color = NeonGreen,
                fontFamily = FontFamily.Monospace,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                style = cyberpunkGlowStyle(NeonGreen)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "WAITING FOR ALL AGENTS TO AUTHORIZE PERMISSIONS",
                color = TextGreen,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}
