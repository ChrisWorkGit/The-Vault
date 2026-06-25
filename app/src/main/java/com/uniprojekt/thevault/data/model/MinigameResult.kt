// PROMPT-REFERENZ: [REF-ISSUE03-ROOM-SETUP]
package com.uniprojekt.thevault.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entität für das Ergebnis eines einzelnen kooperativen Minispiels innerhalb einer Session.
 */
// AI-Generated: Highly Extensible Room Persistence Layer for Asynchronous Team Metrics
@Entity(
    tableName = "minigame_results",
    foreignKeys = [
        ForeignKey(
            entity = GameSession::class,
            parentColumns = ["sessionId"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId"])]
)
data class MinigameResult(
    @PrimaryKey(autoGenerate = true) val resultId: Long = 0,
    val sessionId: Long,
    val minigameTag: String, // z.B. "COOP_LOCKPICK"
    val isSuccess: Boolean,
    val timeSpentSeconds: Int,
    /**
     * AI-Generated: Highly Extensible Room Persistence Layer for Asynchronous Team Metrics
     * Ein essenzielles TEXT/JSON-Feld für maßgeschneiderte Team-Performance-Werte.
     * 
     * HINWEIS: Da kooperative Minispiele (wie Lockpicking via Gyro) hochgradig unterschiedliche 
     * Sensordaten produzieren, kapselt dieses Feld die transienten Echtzeit-Events, die vom Host 
     * während der Runde im RAM aggregiert wurden, in einem kompakten String (JSON).
     */
    val additionalMetrics: String
)
