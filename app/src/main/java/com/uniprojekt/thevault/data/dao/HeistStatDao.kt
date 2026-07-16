// PROMPT-REFERENZ: [REF-ISSUE03-ROOM-SETUP]
package com.uniprojekt.thevault.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.uniprojekt.thevault.data.model.HeistStat
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object für den Zugriff auf Heist-Statistiken.
 */
// AI-Generated: Room Database Statistics & Shareable Highscore Screen
@Dao
interface HeistStatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStat(stat: HeistStat)

    @Query("SELECT * FROM heist_stats ORDER BY id DESC")
    fun getAllStatsOrderedByDate(): Flow<List<HeistStat>>

    @Query("SELECT * FROM heist_stats WHERE isVictory = 1 ORDER BY totalDurationSeconds ASC")
    fun getBestStatsOrderedByTime(): Flow<List<HeistStat>>

    @Query("DELETE FROM heist_stats WHERE id = :id")
    suspend fun deleteStatById(id: Long)
}
