// PROMPT-REFERENZ: [REF-ISSUE03-ROOM-SETUP]
// PROMPT-REFERENZ: [REF-ISSUE28-HIGHSCORE-SCREEN]
package com.uniprojekt.thevault.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.net.Uri
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.uniprojekt.thevault.data.model.HeistStat
import com.uniprojekt.thevault.ui.theme.*
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

/**
 * HighscoreScreen visualisiert die Ergebnisse eines Heists im Cyberpunk-Stil.
 * Ermöglicht das Teilen von Erfolgen und das Einsehen alter Stats.
 */
// AI-Generated: Room Database Statistics & Shareable Highscore Screen
@Composable
fun HighscoreScreen(
    stat: HeistStat?,
    isArchiveMode: Boolean = false,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    var captureRect by remember { mutableStateOf<Rect?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .crtOverlay()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Zurück", tint = NeonGreen)
                }
                
                Text(
                    text = if (isArchiveMode) "HEIST DETAILS" else "HEIST SUMMARY",
                    color = NeonGreen,
                    style = cyberpunkGlowStyle(NeonGreen),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )

                if (stat != null) {
                    IconButton(onClick = { 
                        captureRect?.let { shareScreenshot(context, view, it) } 
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Teilen", tint = NeonGreen)
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (stat != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coords ->
                            val pos = coords.positionInWindow()
                            captureRect = Rect(
                                pos.x.roundToInt(),
                                pos.y.roundToInt(),
                                (pos.x + coords.size.width).roundToInt(),
                                (pos.y + coords.size.height).roundToInt()
                            )
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // AI-Generated: Custom Screenshot Header Logo
                    Text(
                        text = "THE VAULT",
                        color = NeonGreen,
                        style = cyberpunkGlowStyle(NeonGreen),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 16.dp).background(Color.Black.copy(alpha = 0.8f))
                    )
                    
                    StatCard(stat)
                }
            } else if (isArchiveMode) {
                Text("Keine Daten im Archiv gefunden.", color = TextGreen)
            }
        }
    }
}

@Composable
fun StatCard(stat: HeistStat) {
    val statusColor = if (stat.isVictory) NeonGreen else Color(0xFFFF3333)
    val statusText = if (stat.isVictory) "VAULT BREACH SUCCESSFUL" else "SYSTEM COMPROMISED"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkGreen)
            .padding(16.dp)
            .background(Color.Black.copy(alpha = 0.8f))
    ) {
        Text(
            text = statusText,
            color = statusColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Divider(modifier = Modifier.padding(vertical = 12.dp), color = DarkGreen)

        StatRow("ZEIT:", formatDuration(stat.totalDurationSeconds), statusColor)
        StatRow("AGENTEN:", stat.players, TextGreen)
        
        // AI-Generated: Vertical Sequence Display
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            Text(text = "SEQUENZ:", color = DarkGreen, fontWeight = FontWeight.Bold, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
            Text(
                text = stat.gameSequence,
                color = TextGreen,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp, start = 8.dp)
            )
        }
        
        StatRow("FEHLER:", stat.totalErrorsMade.toString(), if (stat.totalErrorsMade > 0) Color.Yellow else TextGreen)
        StatRow("DATUM:", stat.timestamp, TextGreen)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "--- END OF ENCRYPTED DATA ---",
            color = DarkGreen,
            fontSize = 10.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )
    }
}

@Composable
fun StatRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = DarkGreen, fontWeight = FontWeight.Bold, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
        Text(text = value, color = valueColor, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
    }
}

private fun formatDuration(seconds: Long): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}

/**
 * Erzeugt einen Screenshot der aktuellen View und öffnet den Share-Intent.
 */
private fun shareScreenshot(context: Context, view: View, captureRect: Rect) {
    // AI-Generated: Custom Cropped Screenshot Rendering
    val fullBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(fullBitmap)
    view.draw(canvas)

    try {
        // Crop the bitmap to the captureRect (Logo + StatCard)
        val croppedBitmap = Bitmap.createBitmap(
            fullBitmap,
            captureRect.left.coerceAtLeast(0),
            captureRect.top.coerceAtLeast(0),
            captureRect.width().coerceAtMost(fullBitmap.width - captureRect.left),
            captureRect.height().coerceAtMost(fullBitmap.height - captureRect.top)
        )

        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "heist_summary.png")
        val stream = FileOutputStream(file)
        croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()

        val contentUri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(contentUri, context.contentResolver.getType(contentUri))
            putExtra(Intent.EXTRA_STREAM, contentUri)
            type = "image/png"
        }
        context.startActivity(Intent.createChooser(shareIntent, "Ergebnis teilen"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * Archiv-Screen zur Anzeige aller vergangenen Heists.
 */
@Composable
fun ArchiveScreen(
    stats: List<HeistStat>,
    onSelectStat: (HeistStat) -> Unit,
    onDeleteStat: (HeistStat) -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .crtOverlay()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Zurück", tint = NeonGreen)
                }
                Text(
                    text = "BREACH LOGS",
                    color = NeonGreen,
                    fontSize = 24.sp,
                    style = cyberpunkGlowStyle(NeonGreen),
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(stats) { stat ->
                    ArchiveEntry(
                        stat = stat, 
                        onClick = { onSelectStat(stat) },
                        onDelete = { onDeleteStat(stat) }
                    )
                }
            }
        }
    }
}

@Composable
fun ArchiveEntry(stat: HeistStat, onClick: () -> Unit, onDelete: () -> Unit) {
    val color = if (stat.isVictory) NeonGreen else NeonRed
    
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, color.copy(alpha = 0.5f))
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable { onClick() }
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stat.timestamp, color = DarkGreen, fontSize = 12.sp)
                Text(
                    text = if (stat.isVictory) "SUCCESS" else "FAILED",
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
            Text(text = "Dauer: ${formatDuration(stat.totalDurationSeconds)} | Fehler: ${stat.totalErrorsMade}", color = TextGreen, fontSize = 14.sp)
            Text(text = "Agenten: ${stat.players}", color = TextGreen, fontSize = 14.sp, maxLines = 1)
        }

        // Leuchtend rotes X zum Löschen (unten rechts platziert)
        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(4.dp)
                .size(32.dp)
        ) {
            Text(
                text = "X",
                color = NeonRed,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                style = cyberpunkGlowStyle(NeonRed, radius = 12f),
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
        }
    }
}
