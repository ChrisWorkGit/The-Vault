// PROMPT-REFERENZ: [REF-ISSUE23-LOBBY-SYSTEM]
// PROMPT-REFERENZ: [REF-ISSUE03-ROOM-SETUP]
// PROMPT-REFERENZ: [REF-ISSUE28-HIGHSCORE-SCREEN]
// PROMPT-REFERENZ: [REF-ISSUE27-NOTIFICATION-OVERLOAD]
package com.uniprojekt.thevault.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniprojekt.thevault.data.VaultDatabase
import com.uniprojekt.thevault.data.VaultRepository
import com.uniprojekt.thevault.ui.screens.minigames.DecibelBypassScreen
import com.uniprojekt.thevault.ui.screens.minigames.GyroLockScreen
import com.uniprojekt.thevault.ui.screens.minigames.LaserBarrierScreen
import com.uniprojekt.thevault.ui.screens.minigames.LockpickScreen
import com.uniprojekt.thevault.ui.screens.minigames.NotificationOverloadScreen
import com.uniprojekt.thevault.ui.screens.minigames.ShakeDecryptScreen
import com.uniprojekt.thevault.ui.theme.NeonGreen
import com.uniprojekt.thevault.ui.theme.crtOverlay
import com.uniprojekt.thevault.ui.theme.cyberpunkGlowStyle
import com.uniprojekt.thevault.ui.viewmodel.GameViewModel

// AI-Generated: Core Architecture & State Machine Strategy
// AI-Generated: Cyberpunk In-Game Menu & Conditional Debug Overlay
// AI-Generated: Multiplayer Lobby & Synchronized In-Game Menu
// AI-Generated: Room Database Statistics & Shareable Highscore Screen

/**
 * Die zentrale App-Komponente, die den GameState ausliest und zwischen den Screens navigiert.
 */
@Composable
fun MainApp(
    viewModel: GameViewModel = viewModel(),
    paddingValues: PaddingValues = PaddingValues()
) {
    val context = LocalContext.current
    
    // AI-Generated: Initialize Repository for Room Persistence
    LaunchedEffect(Unit) {
        val database = VaultDatabase.getDatabase(context)
        val repository = VaultRepository(database.vaultDao(), database.heistStatDao())
        viewModel.initRepository(repository)
    }

    val gameState by viewModel.gameState.collectAsState()
    val playerName by viewModel.playerName.collectAsState()
    val hostIp by viewModel.hostIp.collectAsState()
    val timerSeconds by viewModel.timerSeconds.collectAsState()
    val isGameActive by viewModel.isGameActive.collectAsState()
    val archivedStats by viewModel.archivedStats.collectAsState()

    Surface(modifier = Modifier.padding(paddingValues)) {
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
                    players = state.players,
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
                            "NotificationOverload" -> {
                                // AI-Generated: Immersive Android System Notification Overload Game - UI Binding
                                NotificationOverloadScreen(
                                    role = viewModel.notificationRole.collectAsState().value,
                                    content = viewModel.notificationContent.collectAsState().value,
                                    isCompleted = state.isCompleted,
                                    onSuccess = { viewModel.completeCurrentMinigame() },
                                    onFail = { viewModel.failCurrentMinigame() },
                                    onMistake = { viewModel.addError() }
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
                            text = "TIME_REMAINING: ${viewModel.formatTime(timerSeconds)}",
                            color = NeonGreen,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = NeonGreen)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "WAITING FOR TEAM...",
                            color = NeonGreen,
                            fontFamily = FontFamily.Monospace,
                            style = cyberpunkGlowStyle(NeonGreen)
                        )
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
    }
}
