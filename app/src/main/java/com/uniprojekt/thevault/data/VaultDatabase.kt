// PROMPT-REFERENZ: [REF-ISSUE03-ROOM-SETUP]
package com.uniprojekt.thevault.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.uniprojekt.thevault.data.dao.VaultDao
import com.uniprojekt.thevault.data.model.GameSession
import com.uniprojekt.thevault.data.model.MinigameResult

/**
 * Die Room-Datenbank für "The Vault".
 * Verwaltet die lokale Persistenz der Spielhistorie.
 */
// AI-Generated: Highly Extensible Room Persistence Layer for Asynchronous Team Metrics
@Database(entities = [GameSession::class, MinigameResult::class], version = 1, exportSchema = false)
abstract class VaultDatabase : RoomDatabase() {
    abstract fun vaultDao(): VaultDao

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
