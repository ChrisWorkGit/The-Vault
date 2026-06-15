// PROMPT-REFERENZ: [REF-ISSUE09-CORE-ARCH]
package com.uniprojekt.thevault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.uniprojekt.thevault.ui.screens.MainApp
import com.uniprojekt.thevault.ui.theme.TheVaultTheme

/**
 * Haupteinstiegspunkt der Anwendung "The Vault".
 */
class MainActivity : ComponentActivity() {

    // AI-Generated: Core Architecture & State Machine Strategy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Aktiviert Edge-to-Edge Design für modernes UI
        enableEdgeToEdge()
        
        setContent {
            TheVaultTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // MainApp übernimmt die Navigation basierend auf dem GameState
                    MainApp(paddingValues = innerPadding)
                }
            }
        }
    }
}
