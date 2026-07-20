// PROMPT-REFERENZ: [REF-FEATURE-WEAR-OS-COMPANION]
package com.uniprojekt.thevault.wear.ui.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Colors

// AI-Generated: Wear OS Companion App & Wrist Debug Terminal
private val WearColorPalette = Colors(
    primary = NeonGreen,
    primaryVariant = TextGreen,
    secondary = DarkGreen,
    background = CyberBlack,
    surface = CyberBlack,
    onPrimary = CyberBlack,
    onSecondary = NeonGreen,
    onBackground = TextGreen,
    onSurface = TextGreen,
)

/**
 * Cyberpunk Theme für die Wear OS App.
 */
@Composable
fun VaultWearTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = WearColorPalette,
        content = content
    )
}
