// PROMPT-REFERENZ: [REF-ISSUE09-CORE-ARCH]
// PROMPT-REFERENZ: [REF-ISSUE10-NET-BASE]
package com.uniprojekt.thevault.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniprojekt.thevault.ui.viewmodel.GameViewModel

// AI-Generated: Core Architecture & State Machine Strategy

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
                GameScreen(
                    minigameName = state.name,
                    onComplete = { viewModel.completeCurrentMinigame() },
                    onFail = { viewModel.triggerGameOver(isWin = false) }
                )
            }
            is GameViewModel.GameState.GameOver -> {
                GameOverScreen(
                    isWin = state.isWin,
                    onRestart = { viewModel.resetToLobby() }
                )
            }
        }
    }
}
