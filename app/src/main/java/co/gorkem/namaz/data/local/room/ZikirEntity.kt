package co.gorkem.namaz.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "zikirs")
data class ZikirEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val targetCount: Int,
    val currentCount: Int,
    val isNamazTesbihati: Boolean = false,
    val currentPhase: Int = 0 // 0: Sübhanallah, 1: Elhamdülillah, 2: Allahuekber
)
