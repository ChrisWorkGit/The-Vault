// PROMPT-REFERENZ: [REF-ISSUE03-ROOM-SETUP]
package com.uniprojekt.thevault.data

import com.uniprojekt.thevault.data.dao.VaultDao
import com.uniprojekt.thevault.data.model.GameSession
import com.uniprojekt.thevault.data.model.GameSessionWithResults
import com.uniprojekt.thevault.data.model.MinigameResult
import kotlinx.coroutines.flow.Flow

/**
 * Repository zur Kapselung des Datenzugriffs auf die Room-Datenbank.
 */
// AI-Generated: Highly Extensible Room Persistence Layer for Asynchronous Team Metrics
class VaultRepository(private val vaultDao: VaultDao) {

    /** Stream aller bisherigen Spielsessions inklusive Ergebnisse. */
    val allSessions: Flow<List<GameSessionWithResults>> = vaultDao.getAllSessionsWithResults()

    /**
     * Speichert eine vollständige Session ab. 
     * Verknüpft automatisch die Minigame-Ergebnisse mit der generierten Session-ID.
     */
    suspend fun saveFullSession(session: GameSession, results: List<MinigameResult>) {
        val sessionId = vaultDao.insertSession(session)
        val resultsWithSessionId = results.map { it.copy(sessionId = sessionId) }
        vaultDao.insertMinigameResults(resultsWithSessionId)
    }
}
