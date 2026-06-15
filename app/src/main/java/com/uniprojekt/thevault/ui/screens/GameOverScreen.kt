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
 * Bildschirm für das Spielende (Sieg oder Niederlage).
 */
@Composable
fun GameOverScreen(isWin: Boolean, onRestart: () -> Unit) {
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
        Text(text = if (isWin) "Der Tresor ist offen!" else "Du wurdest verhaftet.")
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(onClick = onRestart) {
            Text(text = "Zurück zur Lobby")
        }
    }
}
