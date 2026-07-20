// PROMPT-REFERENZ: [REF-ISSUE37-RENAME-AGENT-FIX]
package com.uniprojekt.thevault.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.uniprojekt.thevault.data.dao.HeistStatDao
import com.uniprojekt.thevault.data.dao.PlayerProfileDao
import com.uniprojekt.thevault.data.dao.VaultDao
import com.uniprojekt.thevault.data.model.GameSession
import com.uniprojekt.thevault.data.model.HeistStat
import com.uniprojekt.thevault.data.model.MinigameResult
import com.uniprojekt.thevault.data.model.PlayerProfile

/**
 * Die Room-Datenbank für "The Vault".
 * Verwaltet die lokale Persistenz der Spielhistorie und Profile.
 */
// AI-Generated: Fix agent renaming logic to update existing record instead of creating duplicate
@Database(entities = [GameSession::class, MinigameResult::class, HeistStat::class, PlayerProfile::class], version = 3, exportSchema = false)
abstract class VaultDatabase : RoomDatabase() {
    abstract fun vaultDao(): VaultDao
    abstract fun heistStatDao(): HeistStatDao
    abstract fun playerProfileDao(): PlayerProfileDao

    companion object {
        @Volatile
        private var INSTANCE: VaultDatabase? = null

        fun getDatabase(context: Context): VaultDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VaultDatabase::class.java,
                    "vault_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
