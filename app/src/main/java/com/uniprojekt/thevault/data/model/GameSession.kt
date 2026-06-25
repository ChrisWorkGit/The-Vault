// PROMPT-REFERENZ: [REF-ISSUE03-ROOM-SETUP]
package com.uniprojekt.thevault.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entität für eine globale Spielrunde (Heist).
 * Speichert die übergeordneten Metadaten einer abgeschlossenen Session.
 */
// AI-Generated: Highly Extensible Room Persistence Layer for Asynchronous Team Metrics
@Entity(tableName = "game_sessions")
data class GameSession(
    @PrimaryKey(autoGenerate = true) val sessionId: Long = 0,
    val timestamp: Long,
    val isWin: Boolean,
    val totalDurationSeconds: Int
)
