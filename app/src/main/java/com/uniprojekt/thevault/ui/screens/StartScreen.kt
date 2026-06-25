// PROMPT-REFERENZ: [REF-ISSUE09-CORE-ARCH]
// PROMPT-REFERENZ: [REF-ISSUE10-NET-BASE]
package com.uniprojekt.thevault.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniprojekt.thevault.ui.viewmodel.GameViewModel

// AI-Generated: Core Architecture & State Machine Strategy

/**
 * Der Startbildschirm (Lobby) von "The Vault".
 * Hier beginnt der Spieler seine Mission und wählt seinen Netzwerk-Modus.
 */
@Composable
fun StartScreen(
    viewModel: GameViewModel = viewModel(),
    onStartGame: () -> Unit // Beibehalten für Kompatibilität, wird jetzt intern getriggert
) {
    // AI-Generated: Local P2P Socket Foundation
    
    val networkStatus by viewModel.networkStatus.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    
    // Lokaler State für die IP-Eingabe (Default localhost für Tests)
    var ipAddress by remember { mutableStateOf("127.0.0.1") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "THE VAULT", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text(text = "P2P Multiplayer Base", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
        
        Spacer(modifier = Modifier.height(32.dp))

        // Anzeige des aktuellen Netzwerk-Status
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = networkStatus,
                modifier = Modifier.padding(16.dp),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (!isConnected) {
            // Optionen wenn noch nicht verbunden
            Button(
                onClick = { viewModel.startHosting() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Als Host starten (Server)")
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            TextField(
                value = ipAddress,
                onValueChange = { ipAddress = it },
                label = { Text("Host IP-Adresse") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.joinGame(ipAddress) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Als Client beitreten")
            }
        } else {
            // Button zum Starten, falls der automatische Start nicht gewünscht ist
            Button(onClick = onStartGame) {
                Text(text = "Mission beginnen")
            }
        }
    }
}
