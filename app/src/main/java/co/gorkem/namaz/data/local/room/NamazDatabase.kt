package co.gorkem.namaz.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PrayerTimeEntity::class],
    version = 2,
    exportSchema = false
)
abstract class NamazDatabase : RoomDatabase() {
    abstract fun prayerTimeDao(): PrayerTimeDao
}
