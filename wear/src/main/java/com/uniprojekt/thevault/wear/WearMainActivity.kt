// PROMPT-REFERENZ: [REF-FEATURE-WEAR-OS-COMPANION]
package com.uniprojekt.thevault.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.uniprojekt.thevault.wear.ui.screens.WearMainScreen
import com.uniprojekt.thevault.wear.ui.theme.VaultWearTheme

/**
 * Haupteinstiegspunkt für die Wear OS App.
 * // AI-Generated: Wear OS Companion App & Wrist Debug Terminal
 */
class WearMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            VaultWearTheme {
                WearMainScreen()
            }
        }
    }
}
