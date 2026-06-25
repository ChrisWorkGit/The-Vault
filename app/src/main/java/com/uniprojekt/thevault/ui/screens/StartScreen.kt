// PROMPT-REFERENZ: [REF-ISSUE09-CORE-ARCH]
// PROMPT-REFERENZ: [REF-ISSUE10-NET-BASE]
// PROMPT-REFERENZ: [REF-ISSUE11-QR-CONNECT]
package com.uniprojekt.thevault.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniprojekt.thevault.network.NetworkUtils
import com.uniprojekt.thevault.ui.viewmodel.GameViewModel

// AI-Generated: Core Architecture & State Machine Strategy
// AI-Generated: Local P2P Socket Foundation
// AI-Generated: QR-Code P2P Onboarding Layer with Manual Fallback

/**
 * Der Startbildschirm (Lobby) von "The Vault".
 * Hier beginnt der Spieler seine Mission und wählt seinen Netzwerk-Modus.
 */
@Composable
fun StartScreen(
    viewModel: GameViewModel = viewModel(),
    onStartGame: () -> Unit
) {
    val context = LocalContext.current
    val networkStatus by viewModel.networkStatus.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val hostIp by viewModel.hostIp.collectAsState()
    val showScanner by viewModel.showScanner.collectAsState()
    
    // Lokaler State für manuelles Fallback
    var showManualInput by remember { mutableStateOf(false) }
    var ipAddressInput by remember { mutableStateOf("") }

    if (showScanner) {
        ScannerScreen(
            onResult = { ip -> viewModel.joinGame(ip) },
            onManualInput = { 
                showManualInput = true 
                viewModel.closeScanner()
            },
            onCancel = { viewModel.closeScanner() }
        )
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "THE VAULT", fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Text(text = "P2P Multiplayer Onboarding", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
            
            Spacer(modifier = Modifier.height(32.dp))

            // Netzwerk-Status Anzeige
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

            Spacer(modifier = Modifier.height(16.dp))

            if (!isConnected) {
                if (hostIp != null) {
                    // Host-Ansicht: Zeige QR-Code
                    HostQrView(hostIp!!, context)
                } else if (showManualInput) {
                    // Manueller Fallback
                    ManualIpView(
                        ip = ipAddressInput,
                        onIpChange = { ipAddressInput = it },
                        onJoin = { viewModel.joinGame(ipAddressInput) },
                        onBack = { showManualInput = false }
                    )
                } else {
                    // Haupt-Optionen
                    Button(
                        onClick = { viewModel.startHosting() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Als Host starten (Server)")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { viewModel.openScanner() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Als Client beitreten (Scanner)")
                    }
                    
                    TextButton(onClick = { showManualInput = true }) {
                        Text("Oder IP manuell eingeben")
                    }
                }
            } else {
                Button(onClick = onStartGame) {
                    Text(text = "Mission beginnen")
                }
            }
        }
    }
}

@Composable
fun HostQrView(ip: String, context: Context) {
    val qrBitmap = remember(ip) { NetworkUtils.generateQrCode(ip) }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Scan mich zum Beitreten:", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            bitmap = qrBitmap.asImageBitmap(),
            contentDescription = "QR Code Host IP",
            modifier = Modifier
                .size(200.dp)
                .clickable {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Vault IP", ip)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "IP-Adresse in Zwischenablage kopiert!", Toast.LENGTH_SHORT).show()
                }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("IP: $ip (Klick zum Kopieren)", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
fun ManualIpView(
    ip: String,
    onIpChange: (String) -> Unit,
    onJoin: () -> Unit,
    onBack: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            value = ip,
            onValueChange = onIpChange,
            label = { Text("Host IP-Adresse") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("z.B. 192.168.1.100") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onJoin, modifier = Modifier.fillMaxWidth()) {
            Text("Beitreten")
        }
        TextButton(onClick = onBack) {
            Text("Zurück")
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
