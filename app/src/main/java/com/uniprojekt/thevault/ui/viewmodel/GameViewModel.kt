// PROMPT-REFERENZ: [REF-FIX-RANDOM-MINIGAME]
// PROMPT-REFERENZ: [REF-ISSUE30-REAL-DEVICE-FIX]
// PROMPT-REFERENZ: [REF-ISSUE27-NOTIFICATION-OVERLOAD]
// PROMPT-REFERENZ: [REF-ISSUE37-RENAME-AGENT-FIX]
// PROMPT-REFERENZ: [REF-ISSUE-SYNC-ANALYSIS-AND-FIX]
// PROMPT-REFERENZ: [REF-FEATURE-WEAR-OS-COMPANION]
package com.uniprojekt.thevault.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniprojekt.thevault.data.VaultRepository
import com.uniprojekt.thevault.data.model.GameSession
import com.uniprojekt.thevault.data.model.HeistStat
import com.uniprojekt.thevault.data.model.MinigameResult
import com.uniprojekt.thevault.data.model.PlayerProfile
import com.uniprojekt.thevault.network.NetworkManager
import com.uniprojekt.thevault.network.NetworkUtils
import com.uniprojekt.thevault.wear.WearDebugSignalHandler
import com.uniprojekt.thevault.wear.WearSyncManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
        data class InLobby(val players: List<PlayerProfile>, val isHost: Boolean) : GameState
        data class Playing(val index: Int, val name: String, val isCompleted: Boolean = false) : GameState
        data class WaitingForTeam(val nextIndex: Int, val currentMinigameName: String? = null) : GameState
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

    // AI-Generated: Fix agent renaming logic to update existing record instead of creating duplicate
    private val _localPlayer = MutableStateFlow(PlayerProfile(name = "AGENT_${(1000..9999).random()}"))
    val localPlayer: StateFlow<PlayerProfile> = _localPlayer.asStateFlow()

    private val _players = MutableStateFlow<List<PlayerProfile>>(emptyList())
    val players: StateFlow<List<PlayerProfile>> = _players.asStateFlow()

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

    val isWatchConnected = WearSyncManager.isWatchConnected

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

    private var isConnecting = false

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
            val targetPlayerProfile = allPlayers[0]
            val goldenKey = (1000..9999).random().toString()
            NetworkManager.sendMessage("NOTIF_SETUP:${targetPlayerProfile.name}|$goldenKey", useLoopback = true)
            return
        }

        // Erzeuge für jeden Spieler einen Key, den er finden muss
        val keys = List(n) { (1000..9999).random().toString() }
        
        // Wir bauen eine Zuweisung:
        // Spieler[i] muss keys[i] finden.
        // Spieler[(i+1)%n] bekommt die Info über keys[i].
        // Format: NAME:MY_KEY:AGENT_TO_HELP:KEY_FOR_THEM
        val assignments = allPlayers.mapIndexed { i, profile ->
            val myKey = keys[i]
            val nextIndex = (i + 1) % n
            
            val agentToHelp = allPlayers[nextIndex].name
            val keyToTellThem = keys[nextIndex]
            "${profile.name}:$myKey:$agentToHelp:$keyToTellThem"
        }.joinToString("|")

        // AI-Generated: [REF-ISSUE-SYNC-ANALYSIS-AND-FIX] - Loopback für konsistentes Setup
        NetworkManager.sendMessage("NOTIF_SETUP_V2:$assignments", useLoopback = true)
    }

    /**
     * Bestimmt lokal die Rolle (Target vs Analyst) und den anzuzeigenden Inhalt.
     */
    private fun handleNotificationSetup(targetPlayerName: String, goldenKey: String) {
        val myName = _localPlayer.value.name
        val allPlayers = _players.value
        val isTarget = myName == targetPlayerName

        _notificationRole.value = if (isTarget) "TARGET" else "ANALYST"

        if (isTarget) {
            // Der Target Node muss den Golden Key in seinen Notifications finden
            _notificationContent.value = goldenKey
        } else {
            // Analysten erhalten Teil-Informationen zur Kommunikation
            val analysts = allPlayers.filter { it.name != targetPlayerName }
            val myAnalystIndex = analysts.indexOfFirst { it.name == myName }

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
        val myName = _localPlayer.value.name
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

    /**
     * Aktualisiert den Namen des lokalen Spielers.
     * Nutzt die eindeutige ID, damit kein neuer Datensatz angelegt wird.
     * // AI-Generated: Fix agent renaming logic to update existing record instead of creating duplicate
     */
    fun updatePlayerName(newName: String) {
        val updatedProfile = _localPlayer.value.copy(name = newName)
        _localPlayer.value = updatedProfile
        
        // Persistierung in Room anhand der ID
        viewModelScope.launch {
            vaultRepository?.saveOrUpdateProfile(updatedProfile)
        }

        if (isHost) {
            updateHostPlayerList()
        } else {
            NetworkManager.sendMessage("UPDATE_PLAYER_NAME:${updatedProfile.playerId}|$newName")
        }
    }

    private fun updateHostPlayerList() {
        // Host ist immer an Position 0
        val currentPlayers = _players.value.toMutableList()
        val myProfile = _localPlayer.value
        
        if (currentPlayers.isEmpty()) {
            currentPlayers.add(myProfile)
        } else {
            // Finde mich selbst anhand der ID und aktualisiere
            val index = currentPlayers.indexOfFirst { it.playerId == myProfile.playerId }
            if (index != -1) {
                currentPlayers[index] = myProfile
            } else {
                currentPlayers[0] = myProfile
            }
        }
        _players.value = currentPlayers
        broadcastPlayerList()
        updateLobbyState()
    }

    private fun broadcastPlayerList() {
        // Format: id1:name1,id2:name2
        val listString = _players.value.joinToString(",") { "${it.playerId}:${it.name}" }
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
        _players.value = listOf(_localPlayer.value)
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
        if (_isConnected.value || isConnecting) return
        isConnecting = true
        isHost = false
        _showScanner.value = false
        viewModelScope.launch {
            NetworkManager.connectToHost(
                hostIp = ip,
                onStatusUpdate = { _networkStatus.value = it },
                onHandshakeDone = { success ->
                    isConnecting = false
                    _isConnected.value = success
                    if (success) {
                        val profile = _localPlayer.value
                        _gameState.value = GameState.InLobby(listOf(profile), false)
                        NetworkManager.sendMessage("UPDATE_PLAYER_NAME:${profile.playerId}|${profile.name}")
                    }
                },
                onMessageReceived = { msg, sender -> handleNetworkMessage(msg, sender) }
            )
        }
    }

    private fun handleNetworkMessage(message: String, senderId: String?) {
        when {
            message.startsWith("UPDATE_PLAYER_NAME:") -> {
                if (isHost) {
                    val data = message.substringAfter("UPDATE_PLAYER_NAME:")
                    val parts = data.split("|")
                    if (parts.size == 2) {
                        val id = parts[0]
                        val name = parts[1]
                        
                        val currentList = _players.value.toMutableList()
                        val existingIndex = currentList.indexOfFirst { it.playerId == id }
                        
                        // AI-Generated: Fix agent renaming logic to update existing record instead of creating duplicate
                        if (existingIndex != -1) {
                            currentList[existingIndex] = currentList[existingIndex].copy(name = name)
                        } else {
                            currentList.add(PlayerProfile(playerId = id, name = name))
                        }
                        
                        _players.value = currentList
                        broadcastPlayerList()
                        updateLobbyState()
                    }
                }
            }
            message.startsWith("PLAYER_LIST_UPDATE:") -> {
                if (!isHost) {
                    val listString = message.substringAfter("PLAYER_LIST_UPDATE:")
                    val list = listString.split(",").mapNotNull {
                        val parts = it.split(":")
                        if (parts.size == 2) PlayerProfile(playerId = parts[0], name = parts[1]) else null
                    }
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
                    handleMemberStartReady(senderId)
                }
            }
            message == "START_LEVEL_NOW" -> {
                _isGameActive.value = true
            }
            message == "MINIGAME_READY" -> {
                if (isHost && senderId != null) {
                    // AI-Generated: [REF-ISSUE-SYNC-ANALYSIS-AND-FIX]
                    // Host registriert Bereitschaft (von sich selbst via Loopback oder von Clients)
                    handleClientReady(senderId = senderId)
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
            // AI-Generated: [REF-ISSUE-SYNC-ANALYSIS-AND-FIX] - Loopback für Spielstart
            NetworkManager.sendMessage("START_GAME_TRIGGER:$seqString", useLoopback = true)
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
        // AI-Generated: [REF-ISSUE-SYNC-ANALYSIS-AND-FIX] - Loopback für konsistente Host-Logik
        NetworkManager.sendMessage("MEMBER_START_READY", useLoopback = true)
    }

    private fun handleMemberStartReady(senderId: String) {
        if (isHost) {
            synchronized(startReadyPlayers) {
                startReadyPlayers.add(senderId)
                if (startReadyPlayers.size >= _players.value.size) {
                    startReadyPlayers.clear()
                    // Alle sind bereit -> Level-Start Kommando an alle (inkl. Loopback)
                    NetworkManager.sendMessage("START_LEVEL_NOW", useLoopback = true)
                }
            }
        }
    }

    fun completeCurrentMinigame() {
        val currentState = _gameState.value
        if (currentState is GameState.Playing) {
            // AI-Generated: [REF-ISSUE-SYNC-ANALYSIS-AND-FIX]
            // Spieler geht lokal sofort in den Warten-Modus für bessere UX
            _gameState.value = GameState.WaitingForTeam(currentState.index + 1, currentState.name)
            
            // Signal an den Host (via Loopback falls man selbst Host ist)
            NetworkManager.sendMessage("MINIGAME_READY", useLoopback = true)
        }
    }

    private fun handleClientReady(senderId: String) {
        if (isHost) {
            synchronized(readyPlayers) {
                readyPlayers.add(senderId)
                checkAllPlayersReady()
            }
        }
    }

    private fun checkAllPlayersReady() {
        if (isHost) {
            // Wir prüfen gegen die aktuelle Anzahl der Sockets + 1 (Host)
            if (readyPlayers.size >= _players.value.size) {
                readyPlayers.clear()

                // AI-Generated: [REF-ISSUE-SYNC-ANALYSIS-AND-FIX]
                // Alle fertig -> Trigger an alle (inkl. Loopback für den Host selbst)
                viewModelScope.launch {
                    delay(400) // Cooldown für physische Geräte-Synchronisation
                    NetworkManager.sendMessage("COMPLETE_MINIGAME_TRIGGER", useLoopback = true)
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
        // AI-Generated: [REF-ISSUE-SYNC-ANALYSIS-AND-FIX] - Reset der Bereit-Listen für das nächste Minispiel
        _isGameActive.value = false
        startReadyPlayers.clear()
        readyPlayers.clear()
        
        // AI-Generated: Reset Notification Data for next minigame
        _notificationRole.value = null
        _notificationContent.value = null

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
        _isGameActive.value = false
        if (isHost) {
            val stat = HeistStat(
                timestamp = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date()),
                players = _players.value.joinToString(", ") { it.name },
                totalDurationSeconds = _timerSeconds.value,
                gameSequence = playedGames.joinToString(" -> "),
                totalErrorsMade = _totalErrors.value,
                isVictory = isWin
            )
            // AI-Generated: [REF-ISSUE-SYNC-ANALYSIS-AND-FIX] - Loopback für Heist-Abschluss
            val json = serializeHeistStat(stat)
            NetworkManager.sendMessage("HEIST_STAT_SUMMARY:$json", useLoopback = true)
        } else if (reason != null) {
            // Client-seitiger Fehler (z.B. Verbindung verloren)
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
        // AI-Generated: [REF-ISSUE-SYNC-ANALYSIS-AND-FIX] - Signal an Partner, selbst sofort Reset
        NetworkManager.sendMessage("GAME_OVER:DISCONNECTED_BY_USER", useLoopback = false)
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
        // AI-Generated: [REF-ISSUE-SYNC-ANALYSIS-AND-FIX] - Reset aller spielbezogenen States
        _isGameActive.value = false
        readyPlayers.clear()
        startReadyPlayers.clear()
        activeMinigameSequence.clear()

        NetworkManager.closeConnection()
        _gameState.value = GameState.StartScreen
        _isConnected.value = false
        _networkStatus.value = "Bereit für Verbindung"
        _hostIp.value = null
        _showScanner.value = false
        _players.value = emptyList()
        _notificationRole.value = null
        _notificationContent.value = null
    }

    fun openScanner() = viewModelScope.launch { _showScanner.value = true }
    fun closeScanner() = viewModelScope.launch { _showScanner.value = false }

    /**
     * Initialisiert die Synchronisation mit der Wear OS App.
     * // AI-Generated: Wear OS Companion App & Wrist Debug Terminal
     */
    fun startWearSync(context: Context) {
        WearSyncManager.startMonitoring(context)
        viewModelScope.launch {
            combine(_timerSeconds, _gameState, _totalErrors, _isGameActive) { timer, state, errors, active ->
                val statusText = when (state) {
                    is GameState.Playing -> "ACTIVE: ${state.name}"
                    is GameState.Lobby, is GameState.InLobby -> "IN LOBBY"
                    is GameState.GameOver -> if (state.isWin) "SUCCESS" else "FAILED"
                    is GameState.WaitingForTeam -> "WAITING"
                    else -> "IDLE"
                }
                val mistakesText = "$errors/3"
                val timeText = formatTime(timer)
                
                WearSyncManager.syncGameState(context.applicationContext, timeText, statusText, mistakesText, active)
            }.collect {}
        }

        WearDebugSignalHandler.setListener { command ->
            when (command) {
                "BYPASS_NODE" -> debugCompleteForTeam()
                "ALARM_TEST" -> addError()
            }
        }
    }

    fun initRepository(repository: VaultRepository) {
        this.vaultRepository = repository
        viewModelScope.launch {
            repository.allHeistStats.collect { _archivedStats.value = it }
        }
        // AI-Generated: Fix agent renaming logic - load or create local profile
        viewModelScope.launch {
            repository.localProfile.collect { profile ->
                if (profile != null) {
                    _localPlayer.value = profile
                    if (isHost) updateHostPlayerList()
                } else {
                    // Erstes Mal App gestartet
                    repository.saveOrUpdateProfile(_localPlayer.value)
                }
            }
        }
    }
    fun saveFinalSession(session: GameSession, results: List<MinigameResult>) {
        viewModelScope.launch { vaultRepository?.saveFullSession(session, results) }
    }
}
