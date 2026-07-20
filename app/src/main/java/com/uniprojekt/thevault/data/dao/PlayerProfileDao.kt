// PROMPT-REFERENZ: [REF-ISSUE37-RENAME-AGENT-FIX]
package com.uniprojekt.thevault.data.dao

import androidx.room.*
import com.uniprojekt.thevault.data.model.PlayerProfile
import kotlinx.coroutines.flow.Flow

/**
 * DAO für die Verwaltung von Spielerprofilen.
 * // AI-Generated: Fix agent renaming logic to update existing record instead of creating duplicate
 */
@Dao
interface PlayerProfileDao {
    @Query("SELECT * FROM player_profiles LIMIT 1")
    fun getLocalProfile(): Flow<PlayerProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: PlayerProfile)

    @Update
    suspend fun updateProfile(profile: PlayerProfile)

    @Query("SELECT * FROM player_profiles WHERE playerId = :id")
    suspend fun getProfileById(id: String): PlayerProfile?
}
