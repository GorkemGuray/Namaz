package co.gorkem.namaz.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection

@Database(
    entities = [PrayerTimeEntity::class, ZikirEntity::class],
    version = NamazDatabase.VERSION,
    exportSchema = true
)
abstract class NamazDatabase : RoomDatabase() {
    abstract fun prayerTimeDao(): PrayerTimeDao
    abstract fun zikirDao(): ZikirDao

    companion object {
        const val VERSION = 5
    }
}

data class MigrationSpec(
    val from: Int,
    val to: Int,
    val statements: List<String> = emptyList()
) {
    fun toMigration(): Migration = object : Migration(from, to) {
        override fun migrate(connection: SQLiteConnection) {
            statements.forEach { statement ->
                connection.prepare(statement).use { it.step() }
            }
        }
    }
}

object NamazDatabaseMigrations {
    const val currentVersion = NamazDatabase.VERSION

    val registry: List<MigrationSpec> = emptyList()

    val migrations: Array<Migration>
        get() = registry.map { it.toMigration() }.toTypedArray()
}

fun resolveMigrationPath(
    fromVersion: Int,
    toVersion: Int,
    registry: List<MigrationSpec>
): List<MigrationSpec> {
    if (fromVersion == toVersion) return emptyList()
    if (toVersion < fromVersion) return emptyList()

    val path = mutableListOf<MigrationSpec>()
    var current = fromVersion
    while (current < toVersion) {
        val step = registry.firstOrNull { it.from == current && it.to == current + 1 } ?: return emptyList()
        path.add(step)
        current = step.to
    }
    return path
}

fun canMigrate(
    fromVersion: Int,
    toVersion: Int,
    registry: List<MigrationSpec>
): Boolean {
    if (fromVersion == toVersion) return true
    return resolveMigrationPath(fromVersion, toVersion, registry).isNotEmpty()
}
