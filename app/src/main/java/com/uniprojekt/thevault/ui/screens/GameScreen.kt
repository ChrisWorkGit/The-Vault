// PROMPT-REFERENZ: [REF-ISSUE09-CORE-ARCH]
// PROMPT-REFERENZ: [REF-ISSUE23-INGAME-MENU]
package com.uniprojekt.thevault.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.uniprojekt.thevault.BuildConfig
import com.uniprojekt.thevault.ui.theme.CyberBackground
import com.uniprojekt.thevault.ui.theme.CyberpunkShape
import com.uniprojekt.thevault.ui.theme.DarkGreen
import com.uniprojekt.thevault.ui.theme.NeonGreen
import com.uniprojekt.thevault.ui.theme.crtOverlay

// AI-Generated: Core Architecture & State Machine Strategy
// AI-Generated: Cyberpunk In-Game Menu & Conditional Debug Overlay

/**
 * Der Hauptspielbildschirm, der das aktuelle Minispiel anzeigt.
 * Diese Komponente ist generisch und zeigt je nach State ein anderes Minispiel.
 */
@Composable
fun GameScreen(
    minigameName: String, 
    onComplete: () -> Unit, 
    onFail: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Aktuelle Barriere:", 
            fontSize = 18.sp, 
            fontFamily = FontFamily.Monospace,
            color = NeonGreen.copy(alpha = 0.7f)
        )
        Text(
            text = minigameName.uppercase(), 
            fontSize = 28.sp, 
            color = NeonGreen, 
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Temporäre Buttons zur State-Simulation (werden später durch echte Minispiel-Logik ersetzt)
        Button(
            onClick = onComplete,
            shape = CyberpunkShape(),
            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
        ) {
            Text(text = "MINISPIEL ERFOLGREICH", color = NeonGreen)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = onFail,
            shape = CyberpunkShape(),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
        ) {
            Text(text = "ERWISCHT WERDEN (LOSE)", color = Color.Red)
        }
    }
}

