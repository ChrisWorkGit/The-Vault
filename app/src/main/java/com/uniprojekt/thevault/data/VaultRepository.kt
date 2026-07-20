// PROMPT-REFERENZ: [REF-ISSUE37-RENAME-AGENT-FIX]
package com.uniprojekt.thevault.data

import com.uniprojekt.thevault.data.dao.HeistStatDao
import com.uniprojekt.thevault.data.dao.PlayerProfileDao
import com.uniprojekt.thevault.data.dao.VaultDao
import com.uniprojekt.thevault.data.model.GameSession
import com.uniprojekt.thevault.data.model.GameSessionWithResults
import com.uniprojekt.thevault.data.model.HeistStat
import com.uniprojekt.thevault.data.model.MinigameResult
import com.uniprojekt.thevault.data.model.PlayerProfile
import kotlinx.coroutines.flow.Flow

/**
 * Repository zur Kapselung des Datenzugriffs auf die Room-Datenbank.
 */
// AI-Generated: Fix agent renaming logic to update existing record instead of creating duplicate
class VaultRepository(
    private val vaultDao: VaultDao,
    private val heistStatDao: HeistStatDao,
    private val playerProfileDao: PlayerProfileDao
) {

    /** Stream des lokalen Spielerprofils. */
    val localProfile: Flow<PlayerProfile?> = playerProfileDao.getLocalProfile()

    /**
     * Speichert oder aktualisiert das lokale Spielerprofil.
     * Nutzt die eindeutige ID zur Identifizierung, um Duplikate beim Umbenennen zu vermeiden.
     */
    suspend fun saveOrUpdateProfile(profile: PlayerProfile) {
        playerProfileDao.insertOrUpdate(profile)
    }

    suspend fun getProfileById(id: String): PlayerProfile? {
        return playerProfileDao.getProfileById(id)
    }

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
