// PROMPT-REFERENZ: [REF-ISSUE09-CORE-ARCH]
package com.uniprojekt.thevault.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// AI-Generated: Core Architecture & State Machine Strategy

/**
 * Der Startbildschirm (Lobby) von "The Vault".
 * Hier beginnt der Spieler seine Mission.
 */
@Composable
fun StartScreen(onStartGame: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "THE VAULT", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onStartGame) {
            Text(text = "Einbruch starten")
        }
    }
}
