// PROMPT-REFERENZ: [REF-ISSUE09-CORE-ARCH]
// PROMPT-REFERENZ: [REF-ISSUE02-NET-BASE]
// PROMPT-REFERENZ: [REF-ISSUE17-QR-CONNECT]
// PROMPT-REFERENZ: [REF-ISSUE23-INGAME-MENU]
package com.uniprojekt.thevault.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniprojekt.thevault.ui.screens.minigames.LockpickScreen
import com.uniprojekt.thevault.ui.screens.minigames.ShakeDecryptScreen
import com.uniprojekt.thevault.ui.theme.NeonGreen
import com.uniprojekt.thevault.ui.viewmodel.GameViewModel

// AI-Generated: Core Architecture & State Machine Strategy
// AI-Generated: Cyberpunk In-Game Menu & Conditional Debug Overlay

/**
 * Die zentrale App-Komponente, die den GameState ausliest und zwischen den Screens navigiert.
 */
@Composable
fun MainApp(
    viewModel: GameViewModel = viewModel(),
    paddingValues: PaddingValues = PaddingValues()
) {
    // Beobachte den aktuellen Zustand der State Machine
    val gameState by viewModel.gameState.collectAsState()

    Surface(modifier = Modifier.padding(paddingValues)) {
        // Navigation basierend auf dem aktuellen Zustand (State Machine Pattern)
        when (val state = gameState) {
            is GameViewModel.GameState.Lobby -> {
                StartScreen(
                    onStartGame = { viewModel.startGame() }
                )
            }
            is GameViewModel.GameState.Playing -> {
                var showMenu by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxSize()) {
                    // Minispiel-Inhalt
                    when (state.name) {
                        "ShakeDecrypt" -> {
                            ShakeDecryptScreen(
                                onComplete = { viewModel.completeCurrentMinigame() },
                                onFail = { viewModel.triggerGameOver(isWin = false) }
                            )
                        }
                        "LockPick" -> {
                            LockpickScreen(
                                onComplete = { viewModel.completeCurrentMinigame() },
                                onFail = { viewModel.triggerGameOver(isWin = false) }
                            )
                        }
                        else -> {
                            GameScreen(
                                minigameName = state.name,
                                onComplete = { viewModel.completeCurrentMinigame() },
                                onFail = { viewModel.triggerGameOver(isWin = false) }
                            )
                        }
                    }

                    // Globales In-Game-Menü Icon
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
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
                            onDebugFail = { viewModel.failCurrentMinigame() }
                        )
                    }
                }
            }
            is GameViewModel.GameState.GameOver -> {
                GameOverScreen(
                    isWin = state.isWin,
                    reason = state.reason,
                    onRestart = { viewModel.resetToLobby() }
                )
            }
        }
    }
}