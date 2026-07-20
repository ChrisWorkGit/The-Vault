// PROMPT-REFERENZ: [REF-ISSUE37-RENAME-AGENT-FIX]
package com.uniprojekt.thevault.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Repräsentiert das Profil eines Agenten.
 * Die playerId ist der unveränderliche Primärschlüssel.
 * // AI-Generated: Fix agent renaming logic to update existing record instead of creating duplicate
 */
@Entity(tableName = "player_profiles")
data class PlayerProfile(
    @PrimaryKey
    val playerId: String = UUID.randomUUID().toString(),
    val name: String
)
