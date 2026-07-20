// PROMPT-REFERENZ: [REF-FEATURE-WEAR-OS-COMPANION]
package com.uniprojekt.thevault.wear

import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Service, der Daten-Updates vom Smartphone empfängt.
 * // AI-Generated: Wear OS Companion App & Wrist Debug Terminal
 */
class VaultWearableListenerService : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d("VaultWear", "Daten-Update empfangen: ${dataEvents.count} Events")
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == "/game_state") {
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                
                val timer = dataMap.getString("timer", "00:00")
                val status = dataMap.getString("status", "INACTIVE")
                val mistakes = dataMap.getString("mistakes", "0/3")
                val isGameActive = dataMap.getBoolean("isGameActive", false)

                Log.d("VaultWear", "Status: $status, Active: $isGameActive")

                // Update den globalen State
                WearGameStateProvider.updateState(
                    timer = timer,
                    status = status,
                    mistakes = mistakes,
                    isGameActive = isGameActive
                )
            }
        }
    }
}

/**
 * Einfacher Provider für den Spielzustand auf der Smartwatch.
 */
object WearGameStateProvider {
    val timer = MutableStateFlow("00:00")
    val status = MutableStateFlow("INACTIVE")
    val mistakes = MutableStateFlow("0/3")
    val isGameActive = MutableStateFlow(false)

    fun updateState(timer: String, status: String, mistakes: String, isGameActive: Boolean) {
        this.timer.value = timer
        this.status.value = status
        this.mistakes.value = mistakes
        this.isGameActive.value = isGameActive
    }
}
