// PROMPT-REFERENZ: [REF-ISSUE09-CORE-ARCH]
package com.uniprojekt.thevault.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// AI-Generated: Core Architecture & State Machine Strategy

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
        Text(text = "Aktuelle Barriere:", fontSize = 18.sp)
        Text(
            text = minigameName, 
            fontSize = 28.sp, 
            color = MaterialTheme.colorScheme.primary, 
            fontWeight = FontWeight.ExtraBold
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Temporäre Buttons zur State-Simulation (werden später durch echte Minispiel-Logik ersetzt)
        Button(onClick = onComplete) {
            Text(text = "Minispiel geschafft")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onFail) {
            Text(text = "Erwischt werden (Lose)", color = Color.Red)
        }
    }
}
