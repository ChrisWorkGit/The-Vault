// PROMPT-REFERENZ: [REF-FEATURE-WEAR-OS-COMPANION]
package com.uniprojekt.thevault.wear

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Manager für die Synchronisation von Spieldaten mit der Wear OS App.
 * // AI-Generated: Wear OS Companion App & Wrist Debug Terminal
 */
object WearSyncManager {
    private const val TAG = "WearSyncManager"
    private const val GAME_STATE_PATH = "/game_state"
    private const val WEAR_CAPABILITY = "the_vault_wear_app"

    private val _isWatchConnected = MutableStateFlow(false)
    val isWatchConnected: StateFlow<Boolean> = _isWatchConnected

    /**
     * Initialisiert die Verbindungsüberwachung.
     */
    fun startMonitoring(context: Context) {
        val capabilityClient = Wearable.getCapabilityClient(context)
        
        // Initialer Check
        checkConnection(context)

        // Listener für Änderungen der Capabilities (Watch App gestartet/gestoppt)
        capabilityClient.addListener({ capabilityInfo ->
            _isWatchConnected.value = capabilityInfo.nodes.isNotEmpty()
        }, WEAR_CAPABILITY)
    }

    /**
     * Prüft manuell, ob ein Wear-Gerät mit der App verbunden ist.
     */
    fun checkConnection(context: Context) {
        Wearable.getNodeClient(context).connectedNodes
            .addOnSuccessListener { nodes ->
                _isWatchConnected.value = nodes.isNotEmpty()
                Log.d(TAG, "Connected nodes: ${nodes.size}")
            }
    }

    /**
     * Sendet den aktuellen Spielzustand an alle verbundenen Wear-Geräte.
     * // AI-Generated: Wear OS Data Layer API Sync
     */
    fun syncGameState(
        context: Context,
        timer: String,
        status: String,
        mistakes: String,
        isGameActive: Boolean
    ) {
        val putDataMapReq = PutDataMapRequest.create(GAME_STATE_PATH)
        putDataMapReq.dataMap.apply {
            putString("timer", timer)
            putString("status", status)
            putString("mistakes", mistakes)
            putBoolean("isGameActive", isGameActive)
            putLong("timestamp", System.currentTimeMillis()) // Erwingt Update
        }

        val putDataReq = putDataMapReq.asPutDataRequest()
        putDataReq.setUrgent()

        Wearable.getDataClient(context).putDataItem(putDataReq)
            .addOnSuccessListener {
                Log.d(TAG, "GameState erfolgreich synchronisiert.")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Fehler bei Wear-Synchronisation", e)
            }
    }
}
