// PROMPT-REFERENZ: [REF-ISSUE23-INGAME-MENU]
// PROMPT-REFERENZ: [REF-ISSUE23-LOBBY-SYSTEM]
package com.uniprojekt.thevault.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.uniprojekt.thevault.BuildConfig
import com.uniprojekt.thevault.ui.theme.*

// AI-Generated: Cyberpunk In-Game Menu & Conditional Debug Overlay

/**
 * Das modale In-Game-Menü im Cyberpunk-Stil.
 * @param onDismiss Schließt das Menü.
 * @param onAbort Bricht das gesamte Spiel ab.
 * @param onDebugComplete Debug: Beendet das aktuelle Minispiel nur lokal.
 * @param onDebugCompleteTeam Debug: Beendet das aktuelle Minispiel für alle Agenten.
 * @param onDebugFail Debug: Lässt das aktuelle Minispiel fehlschlagen.
 */
@Composable
fun InGameMenu(
    onDismiss: () -> Unit,
    onAbort: () -> Unit,
    onDebugComplete: () -> Unit,
    onDebugCompleteTeam: () -> Unit,
    onDebugFail: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(1.dp, NeonGreen, CyberpunkShape())
                .background(CyberBackground.copy(alpha = 0.9f), CyberpunkShape())
                .crtOverlay()
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "SYSTEM-MENÜ",
                    color = NeonGreen,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Reguläre Optionen
                Button(
                    onClick = {
                        onDismiss()
                        onAbort()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = CyberpunkShape(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.2f))
                ) {
                    Text(text = "MISSION ABBRECHEN", color = Color.Red, fontFamily = FontFamily.Monospace)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = CyberpunkShape(),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                ) {
                    Text(text = "ZURÜCK ZUM SYSTEM", color = NeonGreen, fontFamily = FontFamily.Monospace)
                }

                // Debug-Sektion: Nur in Debug-Builds sichtbar
                if (BuildConfig.DEBUG) {
                    Spacer(modifier = Modifier.height(32.dp))
                    HorizontalDivider(color = Color.Red.copy(alpha = 0.5f), thickness = 1.dp)
                    Text(
                        text = "DEBUG INTERVENTIONS",
                        color = Color.Red,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Column(
                        modifier = Modifier
                            .border(1.dp, Color.Red, CyberpunkShape())
                            .padding(8.dp)
                    ) {
                        Button(
                            onClick = {
                                onDebugCompleteTeam()
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = CyberpunkShape(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF442200))
                        ) {
                            Text(text = "TEAM: MISSION ERFOLGREICH", color = Color(0xFFFFA500), fontSize = 10.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                onDebugComplete()
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = CyberpunkShape(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF221100))
                        ) {
                            Text(text = "LOCAL: NODE BYPASS", color = Color(0xFFFFA500), fontSize = 10.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                onDebugFail()
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = CyberpunkShape(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF442200))
                        ) {
                            Text(text = "DEBUG: SIMULATE ALARM", color = Color(0xFFFFA500), fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
