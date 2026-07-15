// PROMPT-REFERENZ: [REF-ISSUE03-ROOM-SETUP]
package com.uniprojekt.thevault.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entität für die Speicherung einer abgeschlossenen Heist-Statistik.
 * Erfüllt das Kriterium "Data Centricity" für das Uni-Projekt.
 */
// AI-Generated: Room Database Statistics & Shareable Highscore Screen
@Entity(tableName = "heist_stats")
data class HeistStat(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: String, // Format: "dd.MM.yyyy HH:mm"
    val players: String, // Komma-separierte Liste
    val totalDurationSeconds: Long,
    val gameSequence: String, // Komma-separierte Liste der Minispiele
    val totalErrorsMade: Int,
    val isVictory: Boolean
)
