// PROMPT-REFERENZ: [REF-ISSUE02-NET-BASE]
// PROMPT-REFERENZ: [REF-ISSUE17-QR-CONNECT]
package com.uniprojekt.thevault.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
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

    private const val TAG = "NetworkManager"
    private const val PORT = 8888

    /**
     * Startet den Host-Server und wartet auf eine Verbindung.
     * @param onStatusUpdate Callback für Statusänderungen.
     * @param onHandshakeDone Callback, wenn der Handshake erfolgreich war.
     */
    suspend fun startHost(
        onStatusUpdate: (String) -> Unit,
        onHandshakeDone: (Boolean) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            onStatusUpdate("Host gestartet. Warte auf Client...")
            val serverSocket = ServerSocket(PORT)
            
            // Blockiert im IO-Thread, bis ein Client sich verbindet
            val clientSocket = serverSocket.accept()
            onStatusUpdate("Client verbunden: ${clientSocket.inetAddress}")

            val reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
            val writer = PrintWriter(clientSocket.getOutputStream(), true)

            // Handshake: Erwarte Nachricht vom Client
            val message = reader.readLine()
            Log.d(TAG, "Server empfing: $message")

            if (message == "Hello Vault") {
                // Bestätigung senden
                writer.println("Access Granted")
                onStatusUpdate("Handshake erfolgreich: Access Granted")
                onHandshakeDone(true)
            } else {
                writer.println("Access Denied")
                onStatusUpdate("Handshake fehlgeschlagen: Falsche Nachricht")
                onHandshakeDone(false)
            }

            // In diesem einfachen Beispiel lassen wir den Socket offen oder schließen ihn je nach Bedarf.
            // serverSocket.close() 
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
     */
    suspend fun connectToHost(
        hostIp: String,
        onStatusUpdate: (String) -> Unit,
        onHandshakeDone: (Boolean) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            onStatusUpdate("Verbinde zu $hostIp...")
            val socket = Socket(hostIp, PORT)
            
            val writer = PrintWriter(socket.getOutputStream(), true)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            // Handshake: Nachricht an Server senden
            onStatusUpdate("Sende Handshake...")
            writer.println("Hello Vault")

            // Antwort vom Server lesen
            val response = reader.readLine()
            Log.d(TAG, "Client empfing: $response")

            if (response == "Access Granted") {
                onStatusUpdate("Verbunden: Access Granted")
                onHandshakeDone(true)
            } else {
                onStatusUpdate("Verbindung abgelehnt: $response")
                onHandshakeDone(false)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Fehler bei Verbindung", e)
            onStatusUpdate("Fehler bei Verbindung: ${e.message}")
            onHandshakeDone(false)
        }
    }
}
