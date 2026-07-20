// PROMPT-REFERENZ: [REF-ISSUE09-CORE-ARCH]
// PROMPT-REFERENZ: [REF-ISSUE23-INGAME-MENU]
// PROMPT-REFERENZ: [REF-FEATURE-APP-LOGO-INTEGRATION]
package com.uniprojekt.thevault.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uniprojekt.thevault.ui.components.VaultLogo
import com.uniprojekt.thevault.ui.theme.CyberBackground
import com.uniprojekt.thevault.ui.theme.crtOverlay

// AI-Generated: Core Architecture & State Machine Strategy
// AI-Generated: Cyberpunk In-Game Menu & Conditional Debug Overlay
// AI-Generated: Cyberpunk Logo Component & App Launcher Icon

/**
 * Bildschirm für das Spielende (Sieg oder Niederlage).
 * @param isWin Gibt an, ob das Spiel gewonnen wurde.
 * @param reason Ein optionaler Text, der den Grund für das Spielende erklärt.
 * @param onRestart Callback zum Zurückkehren in die Lobby.
 */
@Composable
fun GameOverScreen(isWin: Boolean, reason: String? = null, onRestart: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .crtOverlay()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // AI-Generated: Cyberpunk Logo Component & App Launcher Icon
            VaultLogo(
                showText = false,
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 24.dp)
            )

            Text(
                text = if (isWin) "MISSION ERFOLGREICH" else "MISSION GESCHEITERT",
                fontSize = 28.sp,
                color = if (isWin) Color.Green else Color.Red,
                fontWeight = FontWeight.Bold,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Zeige den spezifischen Grund an, falls vorhanden, sonst Standardtext
            Text(
                text = reason ?: (if (isWin) "Der Tresor ist offen!" else "Du wurdest verhaftet."),
                color = if (isWin) Color.Green.copy(alpha = 0.7f) else Color.Red.copy(alpha = 0.7f),
                fontSize = 16.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            
            Spacer(modifier = Modifier.height(64.dp))
            
            Button(
                onClick = onRestart,
                shape = com.uniprojekt.thevault.ui.theme.CyberpunkShape(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isWin) Color.Green else Color.Red,
                    contentColor = CyberBackground
                )
            ) {
                Text(text = "ZURÜCK ZUR LOBBY", fontWeight = FontWeight.Bold)
            }
        }
    }
}
