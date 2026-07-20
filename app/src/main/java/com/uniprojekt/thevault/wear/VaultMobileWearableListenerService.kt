// PROMPT-REFERENZ: [REF-FEATURE-WEAR-OS-COMPANION]
package com.uniprojekt.thevault.wear

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

/**
 * Service auf dem Smartphone, der Nachrichten von der Smartwatch empfängt.
 * // AI-Generated: Wear OS Companion App & Wrist Debug Terminal
 */
class VaultMobileWearableListenerService : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == "/debug_command") {
            val command = String(messageEvent.data)
            Log.d("VaultWear", "Debug-Kommando empfangen: $command")
            
            // Hier leiten wir das Kommando an die App-Logik weiter.
            // In einer echten App würde man einen EventBus, LocalBroadcast oder ein Singleton nutzen.
            handleDebugCommand(command)
        }
    }

    private fun handleDebugCommand(command: String) {
        // Wir nutzen hier eine statische Referenz oder einen Callback, 
        // um das GameViewModel zu informieren. Da wir keinen Dependency Injection Container 
        // hier einfach manipulieren wollen, nutzen wir ein einfaches Signal-Objekt.
        WearDebugSignalHandler.onCommandReceived(command)
    }
}

/**
 * Einfacher Handler, um Debug-Signale von Wear an das ViewModel zu delegieren.
 */
object WearDebugSignalHandler {
    private var listener: ((String) -> Unit)? = null

    fun setListener(l: (String) -> Unit) {
        listener = l
    }

    fun onCommandReceived(command: String) {
        listener?.invoke(command)
    }
}
