// PROMPT-REFERENZ: [REF-ISSUE23-LOBBY-SYSTEM]
// PROMPT-REFERENZ: [REF-ISSUE03-ROOM-SETUP]
// PROMPT-REFERENZ: [REF-ISSUE28-HIGHSCORE-SCREEN]
// PROMPT-REFERENZ: [REF-ISSUE27-NOTIFICATION-OVERLOAD]
// PROMPT-REFERENZ: [REF-ISSUE37-RENAME-AGENT-FIX]
// PROMPT-REFERENZ: [REF-ISSUE-SYNC-ANALYSIS-AND-FIX]
package com.uniprojekt.thevault.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniprojekt.thevault.data.VaultDatabase
import com.uniprojekt.thevault.data.VaultRepository
import com.uniprojekt.thevault.ui.screens.minigames.*
import com.uniprojekt.thevault.ui.theme.NeonGreen
import com.uniprojekt.thevault.ui.theme.TextGreen
import com.uniprojekt.thevault.ui.theme.crtOverlay
import com.uniprojekt.thevault.ui.theme.cyberpunkGlowStyle
import com.uniprojekt.thevault.ui.viewmodel.GameViewModel

// AI-Generated: Core Architecture & State Machine Strategy
// AI-Generated: Cyberpunk In-Game Menu & Conditional Debug Overlay
// AI-Generated: Multiplayer Lobby & Synchronized In-Game Menu
// AI-Generated: Room Database Statistics & Shareable Highscore Screen
// AI-Generated: Fix agent renaming logic to update existing record instead of creating duplicate

/**
 * Die zentrale App-Komponente, die den GameState ausliest und zwischen den Screens navigiert.
 */
@Composable
fun MainApp(
    viewModel: GameViewModel = viewModel(),
    paddingValues: PaddingValues = PaddingValues()
) {
    val context = LocalContext.current
    
    // AI-Generated: Fix agent renaming logic to update existing record instead of creating duplicate
    LaunchedEffect(Unit) {
        val database = VaultDatabase.getDatabase(context)
        val repository = VaultRepository(
            database.vaultDao(),
            database.heistStatDao(),
            database.playerProfileDao()
        )
        viewModel.initRepository(repository)
        viewModel.startWearSync(context)
    }

    val gameState by viewModel.gameState.collectAsState()
    val localPlayer by viewModel.localPlayer.collectAsState()
    val playerName = localPlayer.name
    val players by viewModel.players.collectAsState()
    val hostIp by viewModel.hostIp.collectAsState()
    val timerSeconds by viewModel.timerSeconds.collectAsState()
    val isGameActive by viewModel.isGameActive.collectAsState()
    val archivedStats by viewModel.archivedStats.collectAsState()
    val isWatchConnected by viewModel.isWatchConnected.collectAsState()

    Surface(modifier = Modifier.padding(paddingValues)) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = gameState) {
            is GameViewModel.GameState.StartScreen -> {
                StartScreen(
                    onStartGame = { viewModel.startHosting() }
                )
            }
            is GameViewModel.GameState.Archive -> {
                ArchiveScreen(
                    stats = archivedStats,
                    onSelectStat = { viewModel.viewStat(it) },
                    onDeleteStat = { viewModel.deleteStat(it) },
                    onBack = { viewModel.backToStart() }
                )
            }
            is GameViewModel.GameState.InLobby -> {
                LobbyScreen(
                    players = players,
                    isHost = state.isHost,
                    currentPlayerName = playerName,
                    hostIp = hostIp,
                    onNameChange = { viewModel.updatePlayerName(it) },
                    onInitiate = { viewModel.initiateHeist() }
                )
            }
            is GameViewModel.GameState.Playing -> {
                var showMenu by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxSize()) {
                    // Minispiel-Inhalt
                    Box(modifier = Modifier.fillMaxSize().padding(top = 40.dp)) {
                        when (state.name) {
                            "DecibelBypass" -> {
                                DecibelBypassScreen(
                                    onComplete = { viewModel.completeCurrentMinigame() },
                                    onFail = { viewModel.failCurrentMinigame() },
                                    onMistake = { viewModel.addError() },
                                    onReady = { viewModel.reportReadyToStart() },
                                    isGameActive = isGameActive
                                )
                            }
                            "ShakeDecrypt" -> {
                                ShakeDecryptScreen(
                                    onComplete = { viewModel.completeCurrentMinigame() },
                                    onFail = { viewModel.failCurrentMinigame() },
                                    onReady = { viewModel.reportReadyToStart() },
                                    isGameActive = isGameActive
                                )
                            }
                            "LockPick" -> {
                                LockpickScreen(
                                    onComplete = { viewModel.completeCurrentMinigame() },
                                    onFail = { viewModel.failCurrentMinigame() },
                                    onReady = { viewModel.reportReadyToStart() },
                                    isGameActive = isGameActive
                                )
                            }
                            "GyroLock" -> {
                                GyroLockScreen(
                                    onComplete = { viewModel.completeCurrentMinigame() },
                                    onFail = { viewModel.failCurrentMinigame() },
                                    onReady = { viewModel.reportReadyToStart() },
                                    isGameActive = isGameActive
                                )
                            }
                            "LaserBarrier" -> {
                                LaserBarrierScreen(
                                    onComplete = { viewModel.completeCurrentMinigame() },
                                    onFail = { viewModel.failCurrentMinigame() },
                                    onReady = { viewModel.reportReadyToStart() },
                                    isGameActive = isGameActive
                                )
                            }
                            "RotationLock" -> {
                                RotationLockScreen(
                                    onComplete = { viewModel.completeCurrentMinigame() },
                                    onFail = { viewModel.failCurrentMinigame() },
                                    onReady = { viewModel.reportReadyToStart() },
                                    isGameActive = isGameActive
                                )
                            }
                            "NotificationOverload" -> {
                                // AI-Generated: Immersive Android System Notification Overload Game - UI Binding
                                NotificationOverloadScreen(
                                    role = viewModel.notificationRole.collectAsState().value,
                                    content = viewModel.notificationContent.collectAsState().value,
                                    isCompleted = state.isCompleted,
                                    onSuccess = { viewModel.completeCurrentMinigame() },
                                    onFail = { viewModel.failCurrentMinigame() },
                                    onMistake = { viewModel.addError() },
                                    onReady = { viewModel.reportReadyToStart() },
                                    isGameActive = isGameActive
                                )
                            }
                            else -> {
                                GameScreen(
                                    minigameName = state.name,
                                    onComplete = { viewModel.completeCurrentMinigame() },
                                    onFail = { viewModel.failCurrentMinigame() }
                                )
                            }
                        }
                    }

                    // Globaler Spiel-Timer (Neon-Leiste oben)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(Color.Black.copy(alpha = 0.8f))
                            .align(Alignment.TopCenter),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "MISSION_TIME: ${viewModel.formatTime(timerSeconds)}",
                            color = NeonGreen,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Globales In-Game-Menü Icon
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 0.dp, end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Menü öffnen",
                            tint = NeonGreen
                        )
                    }

                    if (showMenu) {
                        InGameMenu(
                            onDismiss = { showMenu = false },
                            onAbort = { viewModel.abortGame() },
                            onDebugComplete = { viewModel.completeCurrentMinigame() },
                            onDebugCompleteTeam = { viewModel.debugCompleteForTeam() },
                            onDebugFail = { viewModel.failCurrentMinigame() }
                        )
                    }
                }
            }
            is GameViewModel.GameState.WaitingForTeam -> {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black).crtOverlay(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        CircularProgressIndicator(color = NeonGreen)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "WAITING FOR TEAM...",
                            color = NeonGreen,
                            fontFamily = FontFamily.Monospace,
                            style = cyberpunkGlowStyle(NeonGreen)
                        )
                        
                        // AI-Generated: [REF-ISSUE-SYNC-ANALYSIS-AND-FIX] - Relay-Info für Partner im zentralen Screen
                        val relayRole = viewModel.notificationRole.collectAsState().value
                        val relayContent = viewModel.notificationContent.collectAsState().value
                        
                        if (state.currentMinigameName == "NotificationOverload" && relayRole == "NEURAL_RELAY" && relayContent != null) {
                            val parts = relayContent.split("|")
                            if (parts.size >= 3) {
                                Spacer(modifier = Modifier.height(32.dp))
                                Text(text = "CRITICAL DATA BROADCAST:", color = TextGreen, fontSize = 12.sp)
                                Text(
                                    text = "AGENT ${parts[1]} NEEDS CODE:",
                                    color = Color.White,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "#${parts[2]}",
                                    color = NeonGreen,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    style = cyberpunkGlowStyle(NeonGreen)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "ESTABLISHING UPLINK TO AGENTS",
                            color = NeonGreen.copy(alpha = 0.5f),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
            is GameViewModel.GameState.GameOver -> {
                HighscoreScreen(
                    stat = state.stat,
                    isArchiveMode = true,
                    onBack = { 
                        if (state.stat != null && archivedStats.contains(state.stat)) {
                             viewModel.openArchive()
                        } else {
                             viewModel.resetToLobby()
                        }
                    }
                )
            }
            else -> {}
        }

        // AI-Generated: Cyberpunk Wear OS Status Indicator
        if (isWatchConnected) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .border(1.dp, NeonGreen.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Watch,
                        contentDescription = null,
                        tint = NeonGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "WEAR_LINK: OK",
                        color = NeonGreen,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
}
