// PROMPT-REFERENZ: [REF-ISSUE02-NET-BASE]
// PROMPT-REFERENZ: [REF-ISSUE17-QR-CONNECT]
// PROMPT-REFERENZ: [REF-ISSUE23-INGAME-MENU]
package com.uniprojekt.thevault.network

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

/**
 * Der NetworkManager kümmert sich um die P2P-Verbindung via Sockets.
 * Er erlaubt es einem Gerät als Host (Server) zu agieren und anderen als Client beizutreten.
 */
object NetworkManager {
    // AI-Generated: Local P2P Socket Foundation
    // AI-Generated: QR-Code P2P Onboarding Layer with Manual Fallback
    // AI-Generated: Cyberpunk In-Game Menu & Conditional Debug Overlay

    private const val TAG = "NetworkManager"
    private const val PORT = 8888

    private var socket: Socket? = null
    private var writer: PrintWriter? = null
    private var listenJob: Job? = null

    /**
     * Startet den Host-Server und wartet auf eine Verbindung.
     * @param onStatusUpdate Callback für Statusänderungen.
     * @param onHandshakeDone Callback, wenn der Handshake erfolgreich war.
     * @param onMessageReceived Callback für eingehende Nachrichten nach dem Handshake.
     */
    suspend fun startHost(
        onStatusUpdate: (String) -> Unit,
        onHandshakeDone: (Boolean) -> Unit,
        onMessageReceived: (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            onStatusUpdate("Host gestartet. Warte auf Client...")
            val serverSocket = ServerSocket(PORT)
            
            // Blockiert im IO-Thread, bis ein Client sich verbindet
            val clientSocket = serverSocket.accept()
            socket = clientSocket
            onStatusUpdate("Client verbunden: ${clientSocket.inetAddress}")

            val reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
            writer = PrintWriter(clientSocket.getOutputStream(), true)

            // Handshake: Erwarte Nachricht vom Client
            val message = reader.readLine()
            Log.d(TAG, "Server empfing: $message")

            if (message == "Hello Vault") {
                // Bestätigung senden
                writer?.println("Access Granted")
                onStatusUpdate("Handshake erfolgreich: Access Granted")
                onHandshakeDone(true)
                
                // Starte Listening-Loop für In-Game-Kommunikation
                startListening(reader, onMessageReceived)
            } else {
                writer?.println("Access Denied")
                onStatusUpdate("Handshake fehlgeschlagen: Falsche Nachricht")
                onHandshakeDone(false)
                closeConnection()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Hosting", e)
            onStatusUpdate("Fehler beim Hosting: ${e.message}")
            onHandshakeDone(false)
        }
    }

    /**
     * Verbindet sich als Client mit einem Host.
     * @param hostIp Die IP-Adresse des Hosts.
     * @param onStatusUpdate Callback für Statusänderungen.
     * @param onHandshakeDone Callback, wenn der Handshake erfolgreich war.
     * @param onMessageReceived Callback für eingehende Nachrichten nach dem Handshake.
     */
    suspend fun connectToHost(
        hostIp: String,
        onStatusUpdate: (String) -> Unit,
        onHandshakeDone: (Boolean) -> Unit,
        onMessageReceived: (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            onStatusUpdate("Verbinde zu $hostIp...")
            val clientSocket = Socket(hostIp, PORT)
            socket = clientSocket
            
            writer = PrintWriter(clientSocket.getOutputStream(), true)
            val reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))

            // Handshake: Nachricht an Server senden
            onStatusUpdate("Sende Handshake...")
            writer?.println("Hello Vault")

            // Antwort vom Server lesen
            val response = reader.readLine()
            Log.d(TAG, "Client empfing: $response")

            if (response == "Access Granted") {
                onStatusUpdate("Verbunden: Access Granted")
                onHandshakeDone(true)
                
                // Starte Listening-Loop für In-Game-Kommunikation
                startListening(reader, onMessageReceived)
            } else {
                onStatusUpdate("Verbindung abgelehnt: $response")
                onHandshakeDone(false)
                closeConnection()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Fehler bei Verbindung", e)
            onStatusUpdate("Fehler bei Verbindung: ${e.message}")
            onHandshakeDone(false)
        }
    }

    /**
     * Startet einen Coroutine-Job, der kontinuierlich auf Nachrichten vom Partner lauscht.
     */
    private fun startListening(reader: BufferedReader, onMessageReceived: (String) -> Unit) {
        listenJob?.cancel()
        listenJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    line?.let { 
                        Log.d(TAG, "Nachricht empfangen: $it")
                        onMessageReceived(it) 
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Fehler beim Lesen vom Socket", e)
            } finally {
                onMessageReceived("CONNECTION_LOST")
            }
        }
    }

    /**
     * Sendet eine Nachricht an das Partner-Gerät.
     * @param message Die zu sendende Nachricht.
     */
    fun sendMessage(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            writer?.println(message)
        }
    }

    /**
     * Schließt die aktuelle Verbindung und beendet den Listening-Job.
     */
    fun closeConnection() {
        listenJob?.cancel()
        try {
            socket?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Schließen des Sockets", e)
        }
        socket = null
        writer = null
    }
}
