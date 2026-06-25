// PROMPT-REFERENZ: [REF-ISSUE03-ROOM-SETUP]
package com.uniprojekt.thevault.data.dao

import androidx.room.*
import com.uniprojekt.thevault.data.model.GameSession
import com.uniprojekt.thevault.data.model.GameSessionWithResults
import com.uniprojekt.thevault.data.model.MinigameResult
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object für den Zugriff auf die Tresor-Historie.
 */
// AI-Generated: Highly Extensible Room Persistence Layer for Asynchronous Team Metrics
@Dao
interface VaultDao {
    @Insert
    suspend fun insertSession(session: GameSession): Long

    @Insert
    suspend fun insertMinigameResults(results: List<MinigameResult>)

    @Transaction
    @Query("SELECT * FROM game_sessions ORDER BY timestamp DESC")
    fun getAllSessionsWithResults(): Flow<List<GameSessionWithResults>>

    @Transaction
    @Query("SELECT * FROM game_sessions WHERE sessionId = :sessionId")
    suspend fun getSessionWithResults(sessionId: Long): GameSessionWithResults?
}
