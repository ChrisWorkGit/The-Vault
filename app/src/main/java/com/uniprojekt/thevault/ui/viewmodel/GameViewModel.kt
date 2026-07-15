// PROMPT-REFERENZ: [REF-ISSUE09-CORE-ARCH]
// PROMPT-REFERENZ: [REF-ISSUE02-NET-BASE]
// PROMPT-REFERENZ: [REF-ISSUE17-QR-CONNECT]
// PROMPT-REFERENZ: [REF-ISSUE03-ROOM-SETUP]
// PROMPT-REFERENZ: [REF-ISSUE23-INGAME-MENU]
// PROMPT-REFERENZ: [REF-ISSUE23-LOBBY-SYSTEM]
package com.uniprojekt.thevault.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniprojekt.thevault.data.VaultRepository
import com.uniprojekt.thevault.data.model.GameSession
import com.uniprojekt.thevault.data.model.MinigameResult
import com.uniprojekt.thevault.network.NetworkManager
import com.uniprojekt.thevault.network.NetworkUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * GameViewModel verwaltet den globalen Spielzustand und die State Machine von "The Vault".
 */
class GameViewModel : ViewModel() {

    // AI-Generated: Core Architecture & State Machine Strategy
    // AI-Generated: Local P2P Socket Foundation
    // AI-Generated: QR-Code P2P Onboarding Layer with Manual Fallback
    // AI-Generated: Multiplayer Lobby & Synchronized In-Game Menu

    /**
     * Definiert die möglichen Zustände des Spiels.
     */
    sealed interface GameState {
        object Lobby : GameState
        object StartScreen : GameState
        data class InLobby(val players: List<String>, val isHost: Boolean) : GameState
        data class Playing(val index: Int, val name: String) : GameState
        data class GameOver(val isWin: Boolean, val reason: String? = null) : GameState
    }

    private val _networkStatus = MutableStateFlow("Bereit für Verbindung")
    val networkStatus: StateFlow<String> = _networkStatus.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _hostIp = MutableStateFlow<String?>(null)
    val hostIp: StateFlow<String?> = _hostIp.asStateFlow()

    private val _showScanner = MutableStateFlow(false)
    val showScanner: StateFlow<Boolean> = _showScanner.asStateFlow()

    private val _playerName = MutableStateFlow("AGENT_${(1000..9999).random()}")
    val playerName: StateFlow<String> = _playerName.asStateFlow()

    private val _players = MutableStateFlow<List<String>>(emptyList())
    private var isHost = false

    private val _gameState = MutableStateFlow<GameState>(GameState.StartScreen)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val minigames = listOf("DecibelBypass", "ShakeDecrypt", "LockPick")
    private var vaultRepository: VaultRepository? = null

    fun updatePlayerName(newName: String) {
        _playerName.value = newName
        if (isHost) {
            updateHostPlayerList()
        } else {
            NetworkManager.sendMessage("NAME_UPDATE:$newName")
        }
    }

    private fun updateHostPlayerList() {
        // Host ist immer an Position 0
        val currentPlayers = _players.value.toMutableList()
        if (currentPlayers.isEmpty()) {
            currentPlayers.add(_playerName.value)
        } else {
            currentPlayers[0] = _playerName.value
        }
        _players.value = currentPlayers
        broadcastPlayerList()
        updateLobbyState()
    }

    private fun broadcastPlayerList() {
        val listString = _players.value.joinToString(",")
        NetworkManager.sendMessage("PLAYER_LIST_UPDATE:$listString")
    }

    private fun updateLobbyState() {
        // Der Host wechselt erst in die Lobby, wenn mindestens ein Client verbunden ist.
        // Ein Client wechselt sofort nach erfolgreichem Handshake in die Lobby.
        if (isHost) {
            if (_players.value.size >= 2 || _gameState.value is GameState.InLobby) {
                _gameState.value = GameState.InLobby(_players.value, true)
            }
        } else {
            _gameState.value = GameState.InLobby(_players.value, false)
        }
    }

    fun startHosting() {
        isHost = true
        _players.value = listOf(_playerName.value)
        // Keinen sofortigen Wechsel in die Lobby - Host bleibt auf StartScreen bis Client kommt
        val ip = NetworkUtils.getLocalIpv4Address()
        _hostIp.value = ip ?: "IP nicht gefunden"
        
        viewModelScope.launch {
            NetworkManager.startHost(
                onStatusUpdate = { _networkStatus.value = it },
                onHandshakeDone = { success -> 
                    _isConnected.value = success
                    // Status-Updates werden über handleNetworkMessage verarbeitet
                },
                onMessageReceived = { handleNetworkMessage(it) }
            )
        }
    }

    fun joinGame(ip: String) {
        isHost = false
        _showScanner.value = false
        viewModelScope.launch {
            NetworkManager.connectToHost(
                hostIp = ip,
                onStatusUpdate = { _networkStatus.value = it },
                onHandshakeDone = { success -> 
                    _isConnected.value = success
                    if (success) {
                        // Client: Sofort in den Lobby-Zustand wechseln
                        _gameState.value = GameState.InLobby(listOf(_playerName.value), false)
                        NetworkManager.sendMessage("NAME_UPDATE:${_playerName.value}")
                    }
                },
                onMessageReceived = { handleNetworkMessage(it) }
            )
        }
    }

    private fun handleNetworkMessage(message: String) {
        when {
            message.startsWith("NAME_UPDATE:") -> {
                if (isHost) {
                    val name = message.substringAfter("NAME_UPDATE:")
                    val currentList = _players.value.toMutableList()
                    if (!currentList.contains(name)) {
                        currentList.add(name)
                        _players.value = currentList
                        broadcastPlayerList()
                        updateLobbyState()
                    }
                }
            }
            message.startsWith("PLAYER_LIST_UPDATE:") -> {
                if (!isHost) {
                    val list = message.substringAfter("PLAYER_LIST_UPDATE:").split(",")
                    _players.value = list
                    updateLobbyState()
                }
            }
            message == "START_GAME_TRIGGER" -> {
                startGame()
            }
            message == "COMPLETE_MINIGAME_TRIGGER" -> {
                completeCurrentMinigame()
            }
            message == "GAME_OVER:DISCONNECTED_BY_USER" -> {
                triggerGameOver(isWin = false, reason = "VERBINDUNG UNTERBROCHEN")
            }
            message == "CONNECTION_LOST" -> {
                if (_gameState.value is GameState.Playing) {
                    triggerGameOver(isWin = false, reason = "VERBINDUNG VERLOREN")
                }
            }
        }
    }

    fun initiateHeist() {
        if (isHost) {
            NetworkManager.sendMessage("START_GAME_TRIGGER")
            startGame()
        }
    }

    fun startGame() {
        _gameState.value = GameState.Playing(0, minigames[0])
    }

    fun completeCurrentMinigame() {
        val currentState = _gameState.value
        if (currentState is GameState.Playing) {
            val nextIndex = currentState.index + 1
            if (nextIndex < minigames.size) {
                _gameState.value = GameState.Playing(nextIndex, minigames[nextIndex])
            } else {
                _gameState.value = GameState.GameOver(isWin = true)
            }
        }
    }

    /**
     * Debug-Funktion: Beendet das aktuelle Minispiel für das gesamte Team synchron.
     */
    fun debugCompleteForTeam() {
        NetworkManager.sendMessage("COMPLETE_MINIGAME_TRIGGER")
        completeCurrentMinigame()
    }

    fun failCurrentMinigame() {
        triggerGameOver(isWin = false)
    }

    fun abortGame() {
        NetworkManager.sendMessage("GAME_OVER:DISCONNECTED_BY_USER")
        resetToLobby()
    }

    fun triggerGameOver(isWin: Boolean, reason: String? = null) {
        _gameState.value = GameState.GameOver(isWin, reason)
    }

    fun resetToLobby() {
        NetworkManager.closeConnection()
        _gameState.value = GameState.StartScreen
        _isConnected.value = false
        _networkStatus.value = "Bereit für Verbindung"
        _hostIp.value = null
        _showScanner.value = false
        _players.value = emptyList()
    }

    fun openScanner() = viewModelScope.launch { _showScanner.value = true }
    fun closeScanner() = viewModelScope.launch { _showScanner.value = false }
    fun initRepository(repository: VaultRepository) { this.vaultRepository = repository }
    fun saveFinalSession(session: GameSession, results: List<MinigameResult>) {
        viewModelScope.launch { vaultRepository?.saveFullSession(session, results) }
    }
}
