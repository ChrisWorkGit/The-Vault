// PROMPT-REFERENZ: [REF-ISSUE09-CORE-ARCH]
// PROMPT-REFERENZ: [REF-ISSUE20-CYBERPUNK-THEME]
package com.uniprojekt.thevault.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// AI-Generated: Core Architecture & State Machine Strategy
// AI-Generated: Cyberpunk Design System & Neon UI Layer

private val DarkColorScheme = darkColorScheme(
    primary = NeonGreen,
    secondary = TextGreen,
    tertiary = DarkGreen,
    background = CyberBackground,
    surface = CyberBackground,
    onPrimary = CyberBackground,
    onSecondary = CyberBackground,
    onTertiary = NeonGreen,
    onBackground = NeonGreen,
    onSurface = NeonGreen,
)

/**
 * Das Haupt-Theme für die App "The Vault" im Cyberpunk-Look.
 */
@Composable
fun TheVaultTheme(
    darkTheme: Boolean = true, // Immer Dark-Mode für Cyberpunk
    dynamicColor: Boolean = false, // Deaktiviert für konsistenten Look
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> DarkColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

