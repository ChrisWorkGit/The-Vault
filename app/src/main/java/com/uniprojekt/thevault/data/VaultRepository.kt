// PROMPT-REFERENZ: [REF-ISSUE03-ROOM-SETUP]
package com.uniprojekt.thevault.data

import com.uniprojekt.thevault.data.dao.HeistStatDao
import com.uniprojekt.thevault.data.dao.VaultDao
import com.uniprojekt.thevault.data.model.GameSession
import com.uniprojekt.thevault.data.model.GameSessionWithResults
import com.uniprojekt.thevault.data.model.HeistStat
import com.uniprojekt.thevault.data.model.MinigameResult
import kotlinx.coroutines.flow.Flow

/**
 * Repository zur Kapselung des Datenzugriffs auf die Room-Datenbank.
 */
// AI-Generated: Room Database Statistics & Shareable Highscore Screen
class VaultRepository(
    private val vaultDao: VaultDao,
    private val heistStatDao: HeistStatDao
) {

    /** Stream aller bisherigen Spielsessions inklusive Ergebnisse. */
    val allSessions: Flow<List<GameSessionWithResults>> = vaultDao.getAllSessionsWithResults()

    /** Stream aller Heist-Statistiken. */
    val allHeistStats: Flow<List<HeistStat>> = heistStatDao.getAllStatsOrderedByDate()

    /** Stream der besten Heist-Statistiken (Siege sortiert nach Zeit). */
    val bestHeistStats: Flow<List<HeistStat>> = heistStatDao.getBestStatsOrderedByTime()

    /**
     * Speichert eine vollständige Session ab. 
     * Verknüpft automatisch die Minigame-Ergebnisse mit der generierten Session-ID.
     */
    suspend fun saveFullSession(session: GameSession, results: List<MinigameResult>) {
        val sessionId = vaultDao.insertSession(session)
        val resultsWithSessionId = results.map { it.copy(sessionId = sessionId) }
        vaultDao.insertMinigameResults(resultsWithSessionId)
    }

    /**
     * Speichert eine Heist-Statistik ab.
     */
    suspend fun insertHeistStat(stat: HeistStat) {
        heistStatDao.insertStat(stat)
    }

    /**
     * Löscht eine Heist-Statistik.
     */
    suspend fun deleteHeistStat(id: Long) {
        heistStatDao.deleteStatById(id)
    }
}
