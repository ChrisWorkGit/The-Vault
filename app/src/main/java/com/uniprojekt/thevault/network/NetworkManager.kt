// PROMPT-REFERENZ: [REF-ISSUE02-NET-BASE]
// PROMPT-REFERENZ: [REF-ISSUE17-QR-CONNECT]
// PROMPT-REFERENZ: [REF-ISSUE23-INGAME-MENU]
// PROMPT-REFERENZ: [REF-ISSUE23-LOBBY-SYSTEM]
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
import java.util.Collections

/**
 * Der NetworkManager kümmert sich um die P2P-Verbindung via Sockets.
 * Unterstützt nun Multi-Client Hosting (bis zu 3 Clients + 1 Host).
 */
object NetworkManager {
    // AI-Generated: Local P2P Socket Foundation
    // AI-Generated: QR-Code P2P Onboarding Layer with Manual Fallback
    // AI-Generated: Cyberpunk In-Game Menu & Conditional Debug Overlay
    // AI-Generated: Multiplayer Lobby & Synchronized In-Game Menu

    private const val TAG = "NetworkManager"
    private const val PORT = 8888

    // Host-spezifisch: Liste aller verbundenen Client-Sockets und deren Writer
    private val clientSockets = Collections.synchronizedList(mutableListOf<Socket>())
    private val clientWriters = Collections.synchronizedList(mutableListOf<PrintWriter>())
    
    // Client-spezifisch: Verbindung zum Host
    private var clientSocket: Socket? = null
    private var clientWriter: PrintWriter? = null
    
    private val listenJobs = Collections.synchronizedList(mutableListOf<Job>())
    private var isHost = false

    /**
     * Startet den Host-Server und wartet in einer Schleife auf bis zu 3 Clients.
     */
    suspend fun startHost(
        onStatusUpdate: (String) -> Unit,
        onHandshakeDone: (Boolean) -> Unit,
        onMessageReceived: (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            isHost = true
            onStatusUpdate("Server-Knoten initiiert. Warte auf Agenten...")
            val serverSocket = ServerSocket(PORT)
            
            // Loop, um mehrere Clients zu akzeptieren
            while (clientSockets.size < 3) {
                val socket = serverSocket.accept()
                clientSockets.add(socket)
                
                val writer = PrintWriter(socket.getOutputStream(), true)
                clientWriters.add(writer)
                
                onStatusUpdate("Agent verbunden: ${socket.inetAddress}")
                
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                
                // Handshake: Warte auf Nachricht vom Client
                val message = reader.readLine()
                if (message == "Hello Vault") {
                    writer.println("Access Granted") // Bestätigung an Client senden
                    
                    // Für jeden Client einen eigenen Listening-Thread starten
                    startListening(reader, onMessageReceived)
                    
                    // Initialer Handshake pro Client erfolgreich
                    onHandshakeDone(true)
                } else {
                    socket.close()
                    clientSockets.remove(socket)
                    clientWriters.remove(writer)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Kritischer Fehler im Host-Node", e)
            onStatusUpdate("Netzwerk-Fehler: ${e.message}")
        }
    }

    /**
     * Verbindet sich als Client mit einem Host.
     */
    suspend fun connectToHost(
        hostIp: String,
        onStatusUpdate: (String) -> Unit,
        onHandshakeDone: (Boolean) -> Unit,
        onMessageReceived: (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            isHost = false
            onStatusUpdate("Infiltriere Host $hostIp...")
            val socket = Socket(hostIp, PORT)
            clientSocket = socket
            clientWriter = PrintWriter(socket.getOutputStream(), true)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            // Handshake
            clientWriter?.println("Hello Vault")
            val response = reader.readLine()
            
            if (response == "Access Granted") {
                onStatusUpdate("Handshake erfolgreich. Zugriff gewährt.")
                onHandshakeDone(true)
                startListening(reader, onMessageReceived)
            } else {
                onStatusUpdate("Zugriff verweigert.")
                onHandshakeDone(false)
                closeConnection()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Verbindungsabbruch", e)
            onStatusUpdate("Fehler: ${e.message}")
            onHandshakeDone(false)
        }
    }

    private fun startListening(reader: BufferedReader, onMessageReceived: (String) -> Unit) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            try {
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    line?.let { onMessageReceived(it) }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Stream unterbrochen", e)
            } finally {
                onMessageReceived("CONNECTION_LOST")
            }
        }
        listenJobs.add(job)
    }

    /**
     * Sendet eine Nachricht. Wenn Host: Broadcast an alle. Wenn Client: Nur an Host.
     */
    fun sendMessage(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (isHost) {
                synchronized(clientWriters) {
                    clientWriters.forEach { it.println(message) }
                }
            } else {
                clientWriter?.println(message)
            }
        }
    }

    /**
     * Beendet alle Verbindungen sauber.
     */
    fun closeConnection() {
        synchronized(listenJobs) {
            listenJobs.forEach { it.cancel() }
            listenJobs.clear()
        }
        try {
            clientSocket?.close()
            synchronized(clientSockets) {
                clientSockets.forEach { it.close() }
                clientSockets.clear()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Bereinigen", e)
        }
        clientSocket = null
        clientWriter = null
        synchronized(clientWriters) {
            clientWriters.clear()
        }
    }
}
