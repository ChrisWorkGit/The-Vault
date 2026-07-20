// PROMPT-REFERENZ: [REF-FIX-RANDOM-MINIGAME]
// PROMPT-REFERENZ: [REF-ISSUE30-REAL-DEVICE-FIX]
// PROMPT-REFERENZ: [REF-ISSUE27-NOTIFICATION-OVERLOAD]
package com.uniprojekt.thevault.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniprojekt.thevault.data.VaultRepository
import com.uniprojekt.thevault.data.model.GameSession
import com.uniprojekt.thevault.data.model.HeistStat
import com.uniprojekt.thevault.data.model.MinigameResult
import com.uniprojekt.thevault.network.NetworkManager
import com.uniprojekt.thevault.network.NetworkUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * GameViewModel verwaltet den globalen Spielzustand und die State Machine von "The Vault".
 */
class GameViewModel : ViewModel() {

    // AI-Generated: Core Architecture & State Machine Strategy
    // AI-Generated: Local P2P Socket Foundation
    // AI-Generated: QR-Code P2P Onboarding Layer with Manual Fallback
    // AI-Generated: Multiplayer Lobby & Synchronized In-Game Menu
    // AI-Generated: Room Database Statistics & Shareable Highscore Screen

    /**
     * Definiert die möglichen Zustände des Spiels.
     */
    sealed interface GameState {
        object Lobby : GameState
        object StartScreen : GameState
        object Archive : GameState
        data class InLobby(val players: List<String>, val isHost: Boolean) : GameState
        data class Playing(val index: Int, val name: String, val isCompleted: Boolean = false) : GameState
        data class WaitingForTeam(val nextIndex: Int) : GameState
        data class GameOver(val isWin: Boolean, val reason: String? = null, val stat: HeistStat? = null) : GameState
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

    private val _timerSeconds = MutableStateFlow(0L)
    val timerSeconds: StateFlow<Long> = _timerSeconds.asStateFlow()
    private var timerJob: Job? = null

    private val _isGameActive = MutableStateFlow(false)
    val isGameActive: StateFlow<Boolean> = _isGameActive.asStateFlow()

    private val _totalErrors = MutableStateFlow(0)
    val totalErrors: StateFlow<Int> = _totalErrors.asStateFlow()

    private val playedGames = mutableListOf<String>()

    // AI-Generated: [REF-ISSUE27-NOTIFICATION-OVERLOAD] - Game state for role distribution
    private val _notificationRole = MutableStateFlow<String?>(null)
    val notificationRole: StateFlow<String?> = _notificationRole.asStateFlow()

    private val _notificationContent = MutableStateFlow<String?>(null)
    val notificationContent: StateFlow<String?> = _notificationContent.asStateFlow()

    // AI-Generated: Dynamic Minigame Sequence handling
    private val activeMinigameSequence = mutableListOf<String>()
    private val readyPlayers = mutableSetOf<String>()
    private val startReadyPlayers = mutableSetOf<String>()
    private val defaultMinigames = listOf("DecibelBypass", "ShakeDecrypt", "LockPick", "GyroLock", "LaserBarrier", "RotationLock", "NotificationOverload")
    private var vaultRepository: VaultRepository? = null

    private val _archivedStats = MutableStateFlow<List<HeistStat>>(emptyList())
    val archivedStats: StateFlow<List<HeistStat>> = _archivedStats.asStateFlow()

    // AI-Generated: Immersive Android System Notification Overload Game
    /**
     * Wählt zufällig einen Target Node und generiert den Golden Key.
     * V2: Jeder Spieler bekommt eine Aufgabe und muss einem anderen helfen.
     */
    private fun setupNotificationOverload() {
        if (!isHost) return
        val allPlayers = _players.value
        val n = allPlayers.size
        if (n == 0) return

        if (n == 1) {
            // Fallback für Single Player (Debug)
            val targetPlayer = allPlayers[0]
            val goldenKey = (1000..9999).random().toString()
            NetworkManager.sendMessage("NOTIF_SETUP:$targetPlayer|$goldenKey")
            handleNotificationSetup(targetPlayer, goldenKey)
            return
        }

        // Erzeuge für jeden Spieler einen Key, den er finden muss
        val keys = List(n) { (1000..9999).random().toString() }
        
        // Wir bauen eine Zuweisung:
        // Spieler[i] muss keys[i] finden.
        // Spieler[(i+1)%n] bekommt die Info über keys[i].
        // Format: NAME:MY_KEY:AGENT_TO_HELP:KEY_FOR_THEM
        val assignments = allPlayers.mapIndexed { i, name ->
            val myKey = keys[i]
            val prevIndex = if (i == 0) n - 1 else i - 1
            val nextIndex = (i + 1) % n
            
            val agentToHelp = allPlayers[nextIndex]
            val keyToTellThem = keys[nextIndex]
            "$name:$myKey:$agentToHelp:$keyToTellThem"
        }.joinToString("|")

        NetworkManager.sendMessage("NOTIF_SETUP_V2:$assignments")
        handleNotificationSetupV2(assignments)
    }

    /**
     * Bestimmt lokal die Rolle (Target vs Analyst) und den anzuzeigenden Inhalt.
     */
    private fun handleNotificationSetup(targetPlayer: String, goldenKey: String) {
        val myName = _playerName.value
        val allPlayers = _players.value
        val isTarget = myName == targetPlayer

        _notificationRole.value = if (isTarget) "TARGET" else "ANALYST"

        if (isTarget) {
            // Der Target Node muss den Golden Key in seinen Notifications finden
            _notificationContent.value = goldenKey
        } else {
            // Analysten erhalten Teil-Informationen zur Kommunikation
            val analysts = allPlayers.filter { it != targetPlayer }
            val myAnalystIndex = analysts.indexOf(myName)

            if (analysts.size <= 1) {
                // Bei nur einem Analysten: Vollständiger Code
                _notificationContent.value = "TARGET ID: #$goldenKey"
            } else {
                // Bei mehreren Analysten: Info-Splitting erzwingt Kommunikation
                when (myAnalystIndex) {
                    0 -> _notificationContent.value = "SECTOR: #${goldenKey.take(2)}XX"
                    1 -> _notificationContent.value = "CODE ENDS WITH: ...${goldenKey.drop(2)}"
                    else -> _notificationContent.value = "PRIORITY: CRITICAL SECURITY BREACH"
                }
            }
        }
    }

    // AI-Generated: Immersive Android System Notification Overload Game - V2 Multi-Target Logic
    /**
     * Setup für V2: Jeder ist Target, jeder ist Analyst.
     * Assignments-Format: "NAME:MY_KEY:TARGET_NAME:TARGET_KEY|..."
     */
    private fun handleNotificationSetupV2(assignments: String) {
        val myName = _playerName.value
        val myEntry = assignments.split("|").find { it.startsWith("$myName:") } ?: return
        
        // Format: NAME : MY_KEY : TARGET_NAME : TARGET_KEY
        val parts = myEntry.split(":")
        if (parts.size < 4) return
        
        val myKeyToFind = parts[1]
        val agentToHelp = parts[2]
        val keyToTellAgent = parts[3]
        
        _notificationRole.value = "NEURAL_RELAY"
        _notificationContent.value = "$myKeyToFind|$agentToHelp|$keyToTellAgent"
    }

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
                },
                onMessageReceived = { msg, sender -> handleNetworkMessage(msg, sender) }
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
                        _gameState.value = GameState.InLobby(listOf(_playerName.value), false)
                        NetworkManager.sendMessage("NAME_UPDATE:${_playerName.value}")
                    }
                },
                onMessageReceived = { msg, sender -> handleNetworkMessage(msg, sender) }
            )
        }
    }

    private fun handleNetworkMessage(message: String, senderId: String?) {
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
            message.startsWith("START_GAME_TRIGGER:") -> {
                val seq = message.substringAfter("START_GAME_TRIGGER:").split(",")
                startGame(seq)
            }
            message == "START_GAME_TRIGGER" -> {
                startGame()
            }
            message == "MEMBER_START_READY" -> {
                if (isHost && senderId != null) {
                    handleMemberStartReady(false, senderId)
                }
            }
            message == "START_LEVEL_NOW" -> {
                _isGameActive.value = true
            }
            message == "MINIGAME_READY" -> {
                if (isHost && senderId != null) {
                    // AI-Generated: Real Device Connection & Sync Patch
                    // Host registriert, dass ein Client fertig ist über seine eindeutige IP
                    handleClientReady(hostReady = false, senderId = senderId)
                }
            }
            message == "COMPLETE_MINIGAME_TRIGGER" -> {
                // Finale Bestätigung vom Host, dass alle fertig sind
                proceedToNextMinigame()
            }
            message.startsWith("HEIST_STAT_SUMMARY:") -> {
                val json = message.substringAfter("HEIST_STAT_SUMMARY:")
                val stat = parseHeistStat(json)
                if (stat != null) {
                    saveHeistStatLocally(stat)
                    triggerGameOver(isWin = stat.isVictory, stat = stat)
                }
            }
            message == "GAME_OVER:DISCONNECTED_BY_USER" -> {
                triggerGameOver(isWin = false, reason = "VERBINDUNG UNTERBROCHEN")
            }
            message == "DEBUG_TEAM_COMPLETE_REQUEST" -> {
                if (isHost) debugCompleteForTeam()
            }
            message.startsWith("CONNECTION_LOST_FROM:") -> {
                val lostId = message.substringAfter("CONNECTION_LOST_FROM:")
                if (isHost) {
                    synchronized(readyPlayers) {
                        readyPlayers.remove(lostId)
                        // Wenn der Host auf diesen Spieler gewartet hat, prüfen wir ob es jetzt weitergehen kann
                        checkAllPlayersReady()
                    }
                }
            }
            message == "CONNECTION_LOST" -> {
                if (_gameState.value is GameState.Playing) {
                    triggerGameOver(isWin = false, reason = "VERBINDUNG VERLOREN")
                }
            }
            message.startsWith("NOTIF_SETUP:") -> {
                val parts = message.substringAfter("NOTIF_SETUP:").split("|")
                if (parts.size >= 2) {
                    handleNotificationSetup(parts[0], parts[1])
                }
            }
            message.startsWith("NOTIF_SETUP_V2:") -> {
                val assignments = message.substringAfter("NOTIF_SETUP_V2:")
                handleNotificationSetupV2(assignments)
            }
        }
    }

    fun initiateHeist() {
        if (isHost) {
            val shuffled = defaultMinigames.shuffled()
            val seqString = shuffled.joinToString(",")
            NetworkManager.sendMessage("START_GAME_TRIGGER:$seqString")
            startGame(shuffled)
        }
    }

    fun startGame(sequence: List<String>? = null) {
        // AI-Generated: [REF-FIX-RANDOM-MINIGAME] - Shuffle sequence for every heist
        activeMinigameSequence.clear()
        if (sequence != null) {
            activeMinigameSequence.addAll(sequence)
        } else {
            activeMinigameSequence.addAll(defaultMinigames.shuffled())
        }

        playedGames.clear()
        playedGames.add(activeMinigameSequence[0])
        _totalErrors.value = 0
        _timerSeconds.value = 0
        _isGameActive.value = false
        readyPlayers.clear()
        startReadyPlayers.clear()
        _gameState.value = GameState.Playing(0, activeMinigameSequence[0])
        
        // AI-Generated: Immersive Android System Notification Overload Game - Setup trigger
        if (isHost && activeMinigameSequence[0] == "NotificationOverload") {
            setupNotificationOverload()
        }

        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _timerSeconds.value++
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun formatTime(seconds: Long): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return "%02d:%02d".format(Locale.getDefault(), mins, secs)
    }

    fun addError() {
        if (_isGameActive.value) {
            _totalErrors.value++
        }
    }

    /**
     * Signalisiert, dass der lokale Spieler bereit ist, das Minispiel zu starten
     * (z.B. Berechtigungen erteilt, Lautstärke ok).
     */
    fun reportReadyToStart() {
        if (isHost) {
            handleMemberStartReady(true, "HOST")
        } else {
            NetworkManager.sendMessage("MEMBER_START_READY")
        }
    }

    private fun handleMemberStartReady(hostReady: Boolean, senderId: String) {
        if (isHost) {
            synchronized(startReadyPlayers) {
                startReadyPlayers.add(senderId)
                if (startReadyPlayers.size >= _players.value.size) {
                    startReadyPlayers.clear()
                    NetworkManager.sendMessage("START_LEVEL_NOW")
                    _isGameActive.value = true
                }
            }
        }
    }

    fun completeCurrentMinigame() {
        val currentState = _gameState.value
        if (currentState is GameState.Playing) {
            // Spieler ist lokal fertig
            _gameState.value = currentState.copy(isCompleted = true)

            if (isHost) {
                handleClientReady(hostReady = true, senderId = "HOST")
            } else {
                NetworkManager.sendMessage("MINIGAME_READY")
            }
        }
    }

    private fun handleClientReady(hostReady: Boolean, senderId: String) {
        if (isHost) {
            synchronized(readyPlayers) {
                readyPlayers.add(senderId)
                checkAllPlayersReady()
                
                // Host State aktualisieren wenn er selbst fertig ist
                if (hostReady) {
                    val currentState = _gameState.value
                    if (currentState is GameState.Playing) {
                        _gameState.value = currentState.copy(isCompleted = true)
                    }
                }
            }
        }
    }

    private fun checkAllPlayersReady() {
        if (isHost) {
            // Wir prüfen gegen die aktuelle Anzahl der Sockets + 1 (Host)
            // Falls ein Client disconnected ist, wird er oben aus readyPlayers entfernt
            if (readyPlayers.size >= _players.value.size) {
                readyPlayers.clear()

                // AI-Generated: Real Device Connection & Sync Patch
                // Kurze Verzögerung einbauen, damit alle Clients Zeit haben, in den 'Waiting'-State zu wechseln
                viewModelScope.launch {
                    delay(400) // 400ms Cooldown für physische Geräte-Synchronisation
                    NetworkManager.sendMessage("COMPLETE_MINIGAME_TRIGGER")
                    proceedToNextMinigame()
                }
            }
        }
    }

    private fun proceedToNextMinigame() {
        val currentState = _gameState.value
        val currentIndex = when (currentState) {
            is GameState.Playing -> currentState.index
            is GameState.WaitingForTeam -> currentState.nextIndex - 1
            else -> return
        }

        val nextIndex = currentIndex + 1
        _isGameActive.value = false
        startReadyPlayers.clear()

        if (nextIndex < activeMinigameSequence.size) {
            val nextGame = activeMinigameSequence[nextIndex]
            playedGames.add(nextGame)

            // AI-Generated: Immersive Android System Notification Overload Game - Setup trigger
            if (isHost && nextGame == "NotificationOverload") {
                setupNotificationOverload()
            }

            _gameState.value = GameState.Playing(nextIndex, nextGame)
        } else {
            finishHeist(isWin = true)
        }
    }

    private fun finishHeist(isWin: Boolean, reason: String? = null) {
        stopTimer()
        if (isHost) {
            val stat = HeistStat(
                timestamp = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date()),
                players = _players.value.joinToString(", "),
                totalDurationSeconds = _timerSeconds.value,
                gameSequence = playedGames.joinToString(" -> "),
                totalErrorsMade = _totalErrors.value,
                isVictory = isWin
            )
            // Broadcast stat to all clients
            val json = serializeHeistStat(stat)
            NetworkManager.sendMessage("HEIST_STAT_SUMMARY:$json")
            saveHeistStatLocally(stat)
            triggerGameOver(isWin = isWin, reason = reason, stat = stat)
        } else if (reason != null) {
            // Client triggered failure (e.g. connection lost)
            triggerGameOver(isWin = isWin, reason = reason)
        }
    }

    private fun saveHeistStatLocally(stat: HeistStat) {
        viewModelScope.launch {
            vaultRepository?.insertHeistStat(stat)
        }
    }

    /**
     * Hilfsfunktion zur einfachen "Serialisierung" der Statistik für das P2P-Netzwerk.
     */
    private fun serializeHeistStat(stat: HeistStat): String {
        // AI-Generated: Manuelle JSON-Serialisierung für minimalen Overhead ohne externe Libs
        return """{"timestamp":"${stat.timestamp}","players":"${stat.players}","duration":${stat.totalDurationSeconds},"sequence":"${stat.gameSequence}","errors":${stat.totalErrorsMade},"win":${stat.isVictory}}"""
    }

    /**
     * Hilfsfunktion zur einfachen "Deserialisierung" der Statistik.
     */
    private fun parseHeistStat(json: String): HeistStat? {
        // AI-Generated: Manuelle JSON-Deserialisierung (Regex-basiert für Einfachheit)
        return try {
            val timestamp = Regex("\"timestamp\":\"(.*?)\"").find(json)?.groupValues?.get(1) ?: ""
            val players = Regex("\"players\":\"(.*?)\"").find(json)?.groupValues?.get(1) ?: ""
            val duration = Regex("\"duration\":(\\d+)").find(json)?.groupValues?.get(1)?.toLong() ?: 0L
            val sequence = Regex("\"sequence\":\"(.*?)\"").find(json)?.groupValues?.get(1) ?: ""
            val errors = Regex("\"errors\":(\\d+)").find(json)?.groupValues?.get(1)?.toInt() ?: 0
            val win = Regex("\"win\":(true|false)").find(json)?.groupValues?.get(1)?.toBoolean() ?: false

            HeistStat(
                timestamp = timestamp,
                players = players,
                totalDurationSeconds = duration,
                gameSequence = sequence,
                totalErrorsMade = errors,
                isVictory = win
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Debug-Funktion: Beendet das aktuelle Minispiel für das gesamte Team synchron.
     */
    fun debugCompleteForTeam() {
        if (isHost) {
            synchronized(readyPlayers) {
                readyPlayers.clear()
                NetworkManager.sendMessage("COMPLETE_MINIGAME_TRIGGER")
                proceedToNextMinigame()
            }
        } else {
            // Client sendet Debug-Request an Host
            NetworkManager.sendMessage("DEBUG_TEAM_COMPLETE_REQUEST")
        }
    }

    fun failCurrentMinigame() {
        finishHeist(isWin = false)
    }

    fun abortGame() {
        stopTimer()
        NetworkManager.sendMessage("GAME_OVER:DISCONNECTED_BY_USER")
        resetToLobby()
    }

    fun triggerGameOver(isWin: Boolean, reason: String? = null, stat: HeistStat? = null) {
        stopTimer()
        _gameState.value = GameState.GameOver(isWin, reason, stat)
    }

    fun openArchive() {
        _gameState.value = GameState.Archive
    }

    fun viewStat(stat: HeistStat) {
        _gameState.value = GameState.GameOver(isWin = stat.isVictory, stat = stat)
    }

    fun deleteStat(stat: HeistStat) {
        viewModelScope.launch {
            vaultRepository?.deleteHeistStat(stat.id)
        }
    }

    fun backToStart() {
        _gameState.value = GameState.StartScreen
    }

    fun resetToLobby() {
        stopTimer()
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
    fun initRepository(repository: VaultRepository) {
        this.vaultRepository = repository
        viewModelScope.launch {
            repository.allHeistStats.collect { _archivedStats.value = it }
        }
    }
    fun saveFinalSession(session: GameSession, results: List<MinigameResult>) {
        viewModelScope.launch { vaultRepository?.saveFullSession(session, results) }
    }
}
