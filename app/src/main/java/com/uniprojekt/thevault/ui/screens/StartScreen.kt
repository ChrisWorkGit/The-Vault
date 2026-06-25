// PROMPT-REFERENZ: [REF-ISSUE09-CORE-ARCH]
// PROMPT-REFERENZ: [REF-ISSUE02-NET-BASE]
// PROMPT-REFERENZ: [REF-ISSUE17-QR-CONNECT]
// PROMPT-REFERENZ: [REF-ISSUE20-CYBERPUNK-THEME]
package com.uniprojekt.thevault.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniprojekt.thevault.network.NetworkUtils
import com.uniprojekt.thevault.ui.theme.*
import com.uniprojekt.thevault.ui.viewmodel.GameViewModel

// AI-Generated: Core Architecture & State Machine Strategy
// AI-Generated: Local P2P Socket Foundation
// AI-Generated: QR-Code P2P Onboarding Layer with Manual Fallback
// AI-Generated: Cyberpunk Design System & Neon UI Layer

/**
 * Der Startbildschirm (Lobby) von "The Vault" im Cyberpunk-Look.
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
            onManualInput = { ip -> 
                viewModel.joinGame(ip)
                viewModel.closeScanner()
            },
            onCancel = { viewModel.closeScanner() }
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberBackground)
                .crtOverlay()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Titel mit Neon Glow
                Text(
                    text = "THE VAULT",
                    style = MaterialTheme.typography.displayLarge.merge(cyberpunkGlowStyle(NeonGreen)),
                    color = NeonGreen
                )
                
                Text(
                    text = "P2P MULTIPLAYER :: SECURE_LINK",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGreen,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Spacer(modifier = Modifier.height(48.dp))

                // Netzwerk-Status Anzeige (Terminal-Look)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeonGreen)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "> ${networkStatus.ifEmpty { "WAITING_FOR_UPLINK..." }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeonGreen
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

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
                        // Haupt-Optionen (Cyberpunk Buttons)
                        CyberButton(
                            text = "START AS HOST",
                            onClick = { viewModel.startHosting() }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        CyberButton(
                            text = "JOIN AS CLIENT",
                            onClick = { viewModel.openScanner() }
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(onClick = { showManualInput = true }) {
                            Text(
                                "MANUAL OVERRIDE (IP)",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGreen
                            )
                        }
                    }
                } else {
                    CyberButton(
                        text = "INITIATE MISSION",
                        onClick = onStartGame,
                        primary = true
                    )
                }
            }
        }
    }
}

@Composable
fun CyberButton(
    text: String,
    onClick: () -> Unit,
    primary: Boolean = false
) {
    Button(
        onClick = onClick,
        shape = CyberpunkShape(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (primary) NeonGreen else TextGreen,
            contentColor = CyberBackground
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(
            text = "-> $text",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
        )
    }
}

@Composable
fun HostQrView(ip: String, context: Context) {
    val qrBitmap = remember(ip) { NetworkUtils.generateQrCode(ip) }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.border(1.dp, DarkGreen).padding(24.dp)
    ) {
        Text(
            "ACCESS TOKEN:",
            style = MaterialTheme.typography.bodySmall,
            color = TextGreen
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(210.dp)
                .border(2.dp, NeonGreen) // Neon Rahmen um den QR Code
                .padding(5.dp)
        ) {
            Image(
                bitmap = qrBitmap.asImageBitmap(),
                contentDescription = "QR Code Host IP",
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Vault IP", ip)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "IP-Adresse kopiert!", Toast.LENGTH_SHORT).show()
                    }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "IP: $ip",
            style = MaterialTheme.typography.bodySmall,
            color = DarkGreen
        )
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
        OutlinedTextField(
            value = ip,
            onValueChange = onIpChange,
            label = { Text("HOST_IP") },
            textStyle = TextStyle(color = NeonGreen, fontFamily = FontFamily.Monospace),
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("0.0.0.0", color = DarkGreen) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonGreen,
                unfocusedBorderColor = DarkGreen,
                focusedLabelColor = NeonGreen,
                unfocusedLabelColor = TextGreen
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        CyberButton(text = "CONNECT", onClick = onJoin, primary = true)
        TextButton(onClick = onBack) {
            Text("CANCEL", color = DarkGreen, style = MaterialTheme.typography.bodySmall)
        }
    }
}

