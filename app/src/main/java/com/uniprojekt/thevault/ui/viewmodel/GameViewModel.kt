// PROMPT-REFERENZ: [REF-ISSUE09-CORE-ARCH]
// PROMPT-REFERENZ: [REF-ISSUE10-NET-BASE]
package com.uniprojekt.thevault.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniprojekt.thevault.network.NetworkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * GameViewModel verwaltet den globalen Spielzustand und die State Machine von "The Vault".
 * Es steuert den Übergang zwischen Lobby, Minispielen und dem Spielende.
 */
class GameViewModel : ViewModel() {

    // AI-Generated: Core Architecture & State Machine Strategy

    /**
     * Definiert die möglichen Zustände des Spiels.
     */
    sealed interface GameState {
        /** Spieler befindet sich in der Lobby/Startmenü. */
        object Lobby : GameState
        
        /** 
         * Ein Minispiel wird gerade gespielt.
         * @param index Der aktuelle Index in der Liste der Minispiele.
         * @param name Der Anzeigename des aktuellen Minispiels.
         */
        data class Playing(val index: Int, val name: String) : GameState
        
        /** 
         * Das Spiel ist beendet.
         * @param isWin True, wenn der Tresor erfolgreich geknackt wurde.
         */
        data class GameOver(val isWin: Boolean) : GameState
    }

    // AI-Generated: Local P2P Socket Foundation

    private val _networkStatus = MutableStateFlow("Bereit für Verbindung")
    /** Aktueller Status der Netzwerkverbindung als Text. */
    val networkStatus: StateFlow<String> = _networkStatus.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    /** Gibt an, ob der Handshake erfolgreich abgeschlossen wurde. */
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    /**
     * Startet den Hosting-Prozess im Hintergrund.
     */
    fun startHosting() {
        viewModelScope.launch {
            NetworkManager.startHost(
                onStatusUpdate = { _networkStatus.value = it },
                onHandshakeDone = { success -> 
                    _isConnected.value = success
                    if (success) startGame() // Testweise direkt starten bei Erfolg
                }
            )
        }
    }

    /**
     * Versucht einer bestehenden Session beizutreten.
     * @param ip Die IP-Adresse des Hosts.
     */
    fun joinGame(ip: String) {
        viewModelScope.launch {
            NetworkManager.connectToHost(
                hostIp = ip,
                onStatusUpdate = { _networkStatus.value = it },
                onHandshakeDone = { success -> 
                    _isConnected.value = success
                    if (success) startGame() // Testweise direkt starten bei Erfolg
                }
            )
        }
    }

    // Liste der Dummy-Minispiele für den Prototyp
    private val minigames = listOf("Gyro-Lock", "Laser Barrier", "Voice Scanner", "Final Swipe")

    // Interner State der State Machine
    private val _gameState = MutableStateFlow<GameState>(GameState.Lobby)
    
    /** Öffentlicher State-Stream für die UI. */
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    /**
     * Startet das Spiel und wechselt zum ersten Minispiel.
     */
    fun startGame() {
        // Initialer Wechsel von Lobby zu Playing
        _gameState.value = GameState.Playing(0, minigames[0])
    }

    /**
     * Schließt das aktuelle Minispiel ab und wechselt zum nächsten oder beendet das Spiel siegreich.
     */
    fun completeCurrentMinigame() {
        val currentState = _gameState.value
        if (currentState is GameState.Playing) {
            val nextIndex = currentState.index + 1
            if (nextIndex < minigames.size) {
                // Nächstes Minispiel in der Liste
                _gameState.value = GameState.Playing(nextIndex, minigames[nextIndex])
            } else {
                // Alle Minispiele (inkl. Final Swipe) geschafft -> Sieg!
                _gameState.value = GameState.GameOver(isWin = true)
            }
        }
    }

    /**
     * Triggert das Spielende (Sieg oder Niederlage).
     * @param isWin Gibt an, ob das Spiel gewonnen wurde.
     */
    fun triggerGameOver(isWin: Boolean) {
        _gameState.value = GameState.GameOver(isWin)
    }

    /**
     * Setzt das Spiel zurück in den Lobby-Zustand.
     */
    fun resetToLobby() {
        _gameState.value = GameState.Lobby
    }
}
