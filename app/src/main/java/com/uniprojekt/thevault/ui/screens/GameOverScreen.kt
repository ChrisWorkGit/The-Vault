// PROMPT-REFERENZ: [REF-ISSUE09-CORE-ARCH]
// PROMPT-REFERENZ: [REF-ISSUE23-INGAME-MENU]
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
// AI-Generated: Cyberpunk In-Game Menu & Conditional Debug Overlay

/**
 * Bildschirm für das Spielende (Sieg oder Niederlage).
 * @param isWin Gibt an, ob das Spiel gewonnen wurde.
 * @param reason Ein optionaler Text, der den Grund für das Spielende erklärt.
 * @param onRestart Callback zum Zurückkehren in die Lobby.
 */
@Composable
fun GameOverScreen(isWin: Boolean, reason: String? = null, onRestart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isWin) "MISSION ERFOLGREICH" else "MISSION GESCHEITERT",
            fontSize = 32.sp,
            color = if (isWin) Color.Green else Color.Red,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Zeige den spezifischen Grund an, falls vorhanden, sonst Standardtext
        Text(
            text = reason ?: (if (isWin) "Der Tresor ist offen!" else "Du wurdest verhaftet."),
            color = if (isWin) Color.Green.copy(alpha = 0.7f) else Color.Red.copy(alpha = 0.7f),
            fontSize = 18.sp
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(onClick = onRestart) {
            Text(text = "Zurück zur Lobby")
        }
    }
}
