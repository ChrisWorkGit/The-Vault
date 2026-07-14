// PROMPT-REFERENZ: [REF-ISSUE09-CORE-ARCH]
// PROMPT-REFERENZ: [REF-ISSUE02-NET-BASE]
// PROMPT-REFERENZ: [REF-ISSUE17-QR-CONNECT]
// PROMPT-REFERENZ: [REF-ISSUE03-ROOM-SETUP]
// PROMPT-REFERENZ: [REF-ISSUE23-INGAME-MENU]
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
         * @param reason Optionaler Grund für das Spielende (z.B. Abbruch durch Partner).
         */
        data class GameOver(val isWin: Boolean, val reason: String? = null) : GameState
    }

    // AI-Generated: Cyberpunk In-Game Menu & Conditional Debug Overlay
    
    /**
     * Verarbeitet eingehende Netzwerk-Nachrichten.
     */
    private fun handleNetworkMessage(message: String) {
        when {
            message == "GAME_OVER:DISCONNECTED_BY_USER" -> {
                triggerGameOver(isWin = false, reason = "VERBINDUNG VOM PARTNER ABGEBROCHEN")
            }
            message == "CONNECTION_LOST" -> {
                if (_gameState.value is GameState.Playing) {
                    triggerGameOver(isWin = false, reason = "VERBINDUNG VERLOREN")
                }
            }
        }
    }

    /**
     * Bricht das Spiel manuell ab und informiert den Partner.
     */
    fun abortGame() {
        NetworkManager.sendMessage("GAME_OVER:DISCONNECTED_BY_USER")
        resetToLobby()
        NetworkManager.closeConnection()
    }

    /**
     * Debug-Funktion: Triggert sofort ein Fehlschlagen des aktuellen Minispiels.
     */
    fun failCurrentMinigame() {
        triggerGameOver(isWin = false)
    }

    // AI-Generated: Local P2P Socket Foundation
    // AI-Generated: QR-Code P2P Onboarding Layer with Manual Fallback

    private val _networkStatus = MutableStateFlow("Bereit für Verbindung")
    /** Aktueller Status der Netzwerkverbindung als Text. */
    val networkStatus: StateFlow<String> = _networkStatus.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    /** Gibt an, ob der Handshake erfolgreich abgeschlossen wurde. */
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _hostIp = MutableStateFlow<String?>(null)
    /** Die eigene IP-Adresse im WLAN (nur für Hosts). */
    val hostIp: StateFlow<String?> = _hostIp.asStateFlow()

    private val _showScanner = MutableStateFlow(false)
    /** Steuert, ob der QR-Scanner für Clients angezeigt wird. */
    val showScanner: StateFlow<Boolean> = _showScanner.asStateFlow()

    /**
     * Startet den Hosting-Prozess und ermittelt die lokale IP für den QR-Code.
     */
    fun startHosting() {
        val ip = NetworkUtils.getLocalIpv4Address()
        _hostIp.value = ip ?: "IP nicht gefunden"
        
        viewModelScope.launch {
            NetworkManager.startHost(
                onStatusUpdate = { _networkStatus.value = it },
                onHandshakeDone = { success -> 
                    _isConnected.value = success
                    if (success) startGame()
                },
                onMessageReceived = { handleNetworkMessage(it) }
            )
        }
    }

    /**
     * Öffnet den QR-Scanner.
     */
    fun openScanner() {
        _showScanner.value = true
    }

    /**
     * Schließt den Scanner.
     */
    fun closeScanner() {
        _showScanner.value = false
    }

    /**
     * Versucht einer bestehenden Session beizutreten.
     * @param ip Die IP-Adresse des Hosts.
     */
    fun joinGame(ip: String) {
        _showScanner.value = false
        viewModelScope.launch {
            NetworkManager.connectToHost(
                hostIp = ip,
                onStatusUpdate = { _networkStatus.value = it },
                onHandshakeDone = { success -> 
                    _isConnected.value = success
                    if (success) startGame()
                },
                onMessageReceived = { handleNetworkMessage(it) }
            )
        }
    }

    // Liste der Dummy-Minispiele für den Prototyp
    private val minigames = listOf("ShakeDecrypt", "LockPick", "Gyro-Lock", "Laser Barrier", "Voice Scanner", "Final Swipe")

    // Interner State der State Machine
    private val _gameState = MutableStateFlow<GameState>(GameState.Lobby)
    
    /** Öffentlicher State-Stream für die UI. */
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    // Repository für die lokale Persistenz (Initialisierung erfolgt idealerweise via DI/Hilt)
    // AI-Generated: Highly Extensible Room Persistence Layer for Asynchronous Team Metrics
    private var vaultRepository: VaultRepository? = null

    /**
     * Setzt das Repository für den Datenzugriff.
     */
    fun initRepository(repository: VaultRepository) {
        this.vaultRepository = repository
    }

    /**
     * AI-Generated: Highly Extensible Room Persistence Layer for Asynchronous Team Metrics
     * Speichert die finale Spielsession dezentral auf dem Gerät.
     *
     * LOGIK-VORLAGE:
     * 1. Der Host verarbeitet während der Runde die transienten Echtzeit-Sensorwerte der Clients 
     *    (z.B. kontinuierliche Gyro-Daten für Lockpicking) im flüchtigen RAM.
     * 2. Nach Match-Ende aggregiert der Host diese Live-Events zu einem kompakten Team-Performance-Paket.
     * 3. Die Metriken werden in das JSON-Feld 'additionalMetrics' serialisiert.
     * 4. Diese Funktion persistiert das Ergebnis dezentral in der lokalen Room-Datenbank.
     */
    fun saveFinalSession(session: GameSession, results: List<MinigameResult>) {
        viewModelScope.launch {
            vaultRepository?.saveFullSession(session, results)
        }
    }

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
     * @param reason Optionaler Grund für das Spielende.
     */
    fun triggerGameOver(isWin: Boolean, reason: String? = null) {
        _gameState.value = GameState.GameOver(isWin, reason)
    }

    /**
     * Setzt das Spiel zurück in den Lobby-Zustand.
     */
    fun resetToLobby() {
        _gameState.value = GameState.Lobby
        _isConnected.value = false
        _networkStatus.value = "Bereit für Verbindung"
        _hostIp.value = null
        _showScanner.value = false
        NetworkManager.closeConnection()
    }
}
