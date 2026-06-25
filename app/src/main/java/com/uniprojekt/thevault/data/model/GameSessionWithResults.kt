// PROMPT-REFERENZ: [REF-ISSUE03-ROOM-SETUP]
package com.uniprojekt.thevault.data.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Relation-Klasse für die Abfrage einer vollständigen Spielrunde inklusive aller Minigame-Ergebnisse.
 */
// AI-Generated: Highly Extensible Room Persistence Layer for Asynchronous Team Metrics
data class GameSessionWithResults(
    @Embedded val session: GameSession,
    @Relation(
        parentColumn = "sessionId",
        entityColumn = "sessionId"
    )
    val results: List<MinigameResult>
)
