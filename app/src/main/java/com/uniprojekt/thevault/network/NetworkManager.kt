// PROMPT-REFERENZ: [REF-ISSUE02-NET-BASE]
// PROMPT-REFERENZ: [REF-ISSUE17-QR-CONNECT]
// PROMPT-REFERENZ: [REF-ISSUE23-INGAME-MENU]
// PROMPT-REFERENZ: [REF-ISSUE23-LOBBY-SYSTEM]
// PROMPT-REFERENZ: [REF-ISSUE30-REAL-DEVICE-FIX]
// PROMPT-REFERENZ: [REF-ISSUE-SYNC-ANALYSIS-AND-FIX]
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

    // Host-spezifisch: Mapping von IP zu PrintWriter für gezielte Kommunikation
    private val clientWritersMap = Collections.synchronizedMap(mutableMapOf<String, PrintWriter>())
    private val clientSockets = Collections.synchronizedList(mutableListOf<Socket>())
    private val clientWriters = Collections.synchronizedList(mutableListOf<PrintWriter>())
    
    // Client-spezifisch: Verbindung zum Host
    private var clientSocket: Socket? = null
    private var clientWriter: PrintWriter? = null
    
    private val listenJobs = Collections.synchronizedList(mutableListOf<Job>())
    private var isHost = false
    private var messageCallback: ((String, String?) -> Unit)? = null

    /**
     * Startet den Host-Server und wartet in einer Schleife auf bis zu 3 Clients.
     */
    suspend fun startHost(
        onStatusUpdate: (String) -> Unit,
        onHandshakeDone: (Boolean) -> Unit,
        onMessageReceived: (String, String?) -> Unit // Geändert: IP als Absender-ID
    ) = withContext(Dispatchers.IO) {
        try {
            isHost = true
            messageCallback = onMessageReceived
            onStatusUpdate("Server-Knoten initiiert. Warte auf Agenten...")
            val serverSocket = ServerSocket(PORT)
            Log.d(TAG, "HOST: ServerSocket auf Port $PORT geoeffnet")
            // Loop, um mehrere Clients zu akzeptieren
            while (clientSockets.size < 3) {
                val socket = serverSocket.accept()
                val clientIp = socket.inetAddress.hostAddress ?: "unknown_${clientSockets.size}"
                Log.d(TAG, "HOST: Verbindung akzeptiert von $clientIp")
                clientSockets.add(socket)
                
                val writer = PrintWriter(socket.getOutputStream(), true)
                clientWriters.add(writer)
                clientWritersMap[clientIp] = writer
                
                onStatusUpdate("Agent verbunden: $clientIp")
                
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                
                // Handshake: Warte auf Nachricht vom Client
                val message = reader.readLine()
                Log.d(TAG, "HOST: Handshake von $clientIp: '$message'")
                if (message == "Hello Vault") {
                    writer.println("Access Granted") // Bestätigung an Client senden
                    
                    // Für jeden Client einen eigenen Listening-Thread starten
                    startListening(reader, clientIp, onMessageReceived)
                    
                    // Initialer Handshake pro Client erfolgreich
                    onHandshakeDone(true)
                } else {
                    Log.d(TAG, "HOST: ungueltiger Handshake von $clientIp -> verworfen")
                    socket.close()
                    clientSockets.remove(socket)
                    clientWriters.remove(writer)
                    clientWritersMap.remove(clientIp)
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
        onMessageReceived: (String, String?) -> Unit // Geändert: IP als Absender-ID
    ) = withContext(Dispatchers.IO) {
        try {
            isHost = false
            messageCallback = onMessageReceived
            onStatusUpdate("Infiltriere Host $hostIp...")
            val socket = Socket(hostIp, PORT)
            clientSocket = socket
            clientWriter = PrintWriter(socket.getOutputStream(), true)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            // Handshake
            Log.d(TAG, "CLIENT: verbunden mit $hostIp:$PORT, sende 'Hello Vault'")
            clientWriter?.println("Hello Vault")
            val response = reader.readLine()
            Log.d(TAG, "CLIENT: Host-Antwort: '$response'")
            if (response == "Access Granted") {
                onStatusUpdate("Handshake erfolgreich. Zugriff gewährt.")
                onHandshakeDone(true)
                startListening(reader, "HOST", onMessageReceived)
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

    private fun startListening(reader: BufferedReader, senderId: String, onMessageReceived: (String, String?) -> Unit) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            try {
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    Log.d(TAG, "RX von '$senderId': '${line?.take(120)}'")
                    line?.let { onMessageReceived(it, senderId) }
                }
                Log.d(TAG, "Stream von '$senderId' regulaer beendet")
            } catch (e: Exception) {
                Log.e(TAG, "Stream unterbrochen von $senderId", e)
            } finally {
                Log.d(TAG, "Listener '$senderId' beendet -> CONNECTION_LOST_FROM:$senderId")
                // AI-Generated: Real Device Connection & Sync Patch
                // Informiere das ViewModel über den spezifischen Abbruch dieser Verbindung
                onMessageReceived("CONNECTION_LOST_FROM:$senderId", senderId)
            }
        }
        listenJobs.add(job)
    }

    /**
     * Sendet eine Nachricht. Wenn Host: Broadcast an alle. Wenn Client: Nur an Host.
     * @param useLoopback Wenn true, wird die Nachricht auch lokal verarbeitet (Single Source of Truth).
     */
    fun sendMessage(message: String, useLoopback: Boolean = false) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "TX isHost=$isHost loopback=$useLoopback clients=${clientWriters.size} msg='${message.take(120)}'")
            // AI-Generated: [REF-ISSUE-SYNC-ANALYSIS-AND-FIX] - Loopback Handling
            if (useLoopback) {
                messageCallback?.invoke(message, "LOCAL_LOOPBACK")
            }

            if (isHost) {
                synchronized(clientWriters) {
                    clientWriters.forEach { it.println(message) }
                }
            } else {
                if (clientWriter == null) Log.d(TAG, "TX WARN: clientWriter==null, Nachricht verworfen")
                clientWriter?.println(message)
            }
        }
    }

    /**
     * Beendet alle Verbindungen sauber.
     */
    fun closeConnection() {
        Log.d(TAG, "closeConnection() isHost=$isHost clients=${clientSockets.size}")
        messageCallback = null
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
        synchronized(clientWritersMap) {
            clientWritersMap.clear()
        }
    }
}