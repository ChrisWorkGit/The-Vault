// PROMPT-REFERENZ: [REF-ISSUE23-LOBBY-SYSTEM]
// PROMPT-REFERENZ: [REF-ISSUE37-RENAME-AGENT-FIX]
package com.uniprojekt.thevault.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uniprojekt.thevault.data.model.PlayerProfile
import com.uniprojekt.thevault.network.NetworkUtils
import com.uniprojekt.thevault.ui.theme.*

// AI-Generated: Multiplayer Lobby & Synchronized In-Game Menu
// AI-Generated: Fix agent renaming logic to update existing record instead of creating duplicate

/**
 * Lobby-Bildschirm für den Heist. Zeigt verbundene Agenten und ermöglicht dem Host den Start.
 */
@Composable
fun LobbyScreen(
    players: List<PlayerProfile>,
    isHost: Boolean,
    currentPlayerName: String,
    hostIp: String?,
    onNameChange: (String) -> Unit,
    onInitiate: () -> Unit
) {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .crtOverlay()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ACCESSING VAULT LOBBY",
            style = cyberpunkGlowStyle(NeonGreen),
            color = NeonGreen,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Host zeigt Connection-Info für weitere Spieler
        if (isHost && hostIp != null) {
            HostConnectionInfo(hostIp, context)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Grid für 4 Spieler-Slots
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(4) { index ->
                val player = players.getOrNull(index)
                PlayerSlot(
                    index = index,
                    name = player?.name,
                    isSelf = (index == 0 && isHost) || (player?.name == currentPlayerName && !isHost),
                    currentPlayerName = currentPlayerName,
                    onNameChange = onNameChange
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isHost) {
            Button(
                onClick = onInitiate,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = CyberpunkShape(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                enabled = players.size >= 2 // Host + mindestens 1 Client
            ) {
                Text(
                    text = "-> INITIATE HEIST",
                    color = CyberBackground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
            }
        } else {
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "alpha"
            )
            
            Text(
                text = "WAITING FOR HOST TO START NODE...",
                color = NeonGreen.copy(alpha = alpha),
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun HostConnectionInfo(ip: String, context: Context) {
    val qrBitmap = remember(ip) { NetworkUtils.generateQrCode(ip, size = 300) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkGreen)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = qrBitmap.asImageBitmap(),
            contentDescription = "Lobby QR Code",
            modifier = Modifier
                .size(140.dp)
                .clickable {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Vault IP", ip)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "IP kopiert: $ip", Toast.LENGTH_SHORT).show()
                }
        )
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Text("INVITE AGENTS", color = TextGreen, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
            Text("IP: $ip", color = NeonGreen, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
            Text("SCAN QR TO JOIN NODE", color = DarkGreen, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
fun PlayerSlot(
    index: Int,
    name: String?,
    isSelf: Boolean,
    currentPlayerName: String,
    onNameChange: (String) -> Unit
) {
    val isOccupied = name != null
    var localName by remember(currentPlayerName) { mutableStateOf(currentPlayerName) }
    var isEditing by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier
            .aspectRatio(1.2f)
            .border(
                width = 1.dp,
                color = if (isOccupied) NeonGreen else DarkGreen,
                shape = CyberpunkShape()
            )
            .background(
                color = if (isOccupied) DarkGreen.copy(alpha = 0.1f) else Color.Transparent,
                shape = CyberpunkShape()
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "NODE_0${index + 1}",
                color = if (isOccupied) NeonGreen else DarkGreen,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            if (isOccupied) {
                if (isSelf) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        BasicTextField(
                            value = localName,
                            onValueChange = { 
                                localName = it
                                isEditing = true
                            },
                            textStyle = TextStyle(
                                color = NeonGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            ),
                            cursorBrush = SolidColor(NeonGreen),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    onNameChange(localName)
                                    isEditing = false
                                    focusManager.clearFocus()
                                }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester)
                                .onFocusChanged { if (it.isFocused) isEditing = true }
                        )
                        
                        // Kleiner Bestätigungs-Button oder Edit-Icon
                        IconButton(
                            onClick = {
                                if (isEditing) {
                                    onNameChange(localName)
                                    isEditing = false
                                    focusManager.clearFocus()
                                } else {
                                    focusRequester.requestFocus()
                                }
                            },
                            modifier = Modifier.size(16.dp)
                        ) {
                            Icon(
                                imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                                contentDescription = "Rename",
                                tint = NeonGreen,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                    Text(
                        text = "[YOU]",
                        color = NeonGreen.copy(alpha = 0.6f),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                } else {
                    Text(
                        text = name,
                        color = NeonGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        maxLines = 1
                    )
                    Text(
                        text = "[CONNECTED]",
                        color = NeonGreen.copy(alpha = 0.6f),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            } else {
                Text(
                    text = "[EMPTY_SLOT]",
                    color = DarkGreen.copy(alpha = 0.4f),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
