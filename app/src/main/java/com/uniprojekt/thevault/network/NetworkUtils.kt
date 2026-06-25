// PROMPT-REFERENZ: [REF-ISSUE02-NET-BASE]
// PROMPT-REFERENZ: [REF-ISSUE17-QR-CONNECT]
package com.uniprojekt.thevault.network

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.net.Inet4Address
import java.net.NetworkInterface

/**
 * Hilfsklasse für Netzwerkoperationen und QR-Code-Generierung.
 */
object NetworkUtils {
    // AI-Generated: QR-Code P2P Onboarding Layer with Manual Fallback

    /**
     * Ermittelt die lokale IPv4-Adresse des Geräts im WLAN.
     * @return Die IP-Adresse als String oder null, wenn keine gefunden wurde.
     */
    fun getLocalIpv4Address(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    // Wir suchen eine IPv4-Adresse, die nicht Loopback ist
                    if (!address.isLoopbackAddress && address is Inet4Address) {
                        return address.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Generiert eine QR-Code Bitmap aus einem gegebenen Text im Cyberpunk-Stil.
     * @param text Der zu kodierende Text (z.B. IP-Adresse).
     * @param size Die Größe des Quadrats in Pixeln.
     * @param moduleColor Farbe der QR-Module (Standard Neon-Grün).
     * @param backgroundColor Hintergrundfarbe (Standard Schwarz).
     * @return Eine Bitmap des QR-Codes.
     */
    fun generateQrCode(
        text: String, 
        size: Int = 512, 
        moduleColor: Int = 0xFF00FF66.toInt(), 
        backgroundColor: Int = 0xFF040805.toInt()
    ): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) moduleColor else backgroundColor)
            }
        }
        return bitmap
    }
}
