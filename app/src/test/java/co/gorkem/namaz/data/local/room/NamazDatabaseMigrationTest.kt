package co.gorkem.namaz.data.local.room

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NamazDatabaseMigrationTest {

    private val spec55to66 = MigrationSpec(from = 5, to = 6)
    private val spec66to77 = MigrationSpec(from = 6, to = 7)

    @Test
    fun sameVersion_needsNoMigration() {
        val path = resolveMigrationPath(fromVersion = 5, toVersion = 5, registry = emptyList())
        assertTrue(path.isEmpty())
    }

    @Test
    fun registryHasNoGap_returnsNoPath() {
        val path = resolveMigrationPath(fromVersion = 5, toVersion = 6, registry = emptyList())
        assertTrue(path.isEmpty())
    }

    @Test
    fun singleStep_returnsThatMigration() {
        val path = resolveMigrationPath(5, 6, listOf(spec55to66))
        assertEquals(listOf(spec55to66), path)
    }

    @Test
    fun consecutiveSteps_chainsInOrder() {
        val path = resolveMigrationPath(5, 7, listOf(spec55to66, spec66to77))
        assertEquals(listOf(spec55to66, spec66to77), path)
    }

    @Test
    fun consecutiveSteps_chainsRegardlessOfRegistryOrder() {
        val path = resolveMigrationPath(5, 7, listOf(spec66to77, spec55to66))
        assertEquals(listOf(spec55to66, spec66to77), path)
    }

    @Test
    fun brokenChain_returnsNoPath() {
        val path = resolveMigrationPath(5, 7, listOf(spec55to66))
        assertTrue(path.isEmpty())
    }

    @Test
    fun downgrade_returnsNoPath() {
        val path = resolveMigrationPath(7, 5, listOf(spec55to66, spec66to77))
        assertTrue(path.isEmpty())
    }

    @Test
    fun targetNotReachable_returnsNoPath() {
        val path = resolveMigrationPath(5, 8, listOf(spec55to66, spec66to77))
        assertTrue(path.isEmpty())
    }

    @Test
    fun missingIntermediateVersion_returnsNoPath() {
        val path = resolveMigrationPath(5, 7, listOf(MigrationSpec(from = 5, to = 7)))
        assertTrue(path.isEmpty())
    }

    @Test
    fun canMigrate_reportsPathAvailability() {
        assertTrue(canMigrate(5, 6, listOf(spec55to66)))
        assertFalse(canMigrate(5, 6, emptyList()))
    }

    @Test
    fun canMigrate_sameVersionAlwaysTrue() {
        assertTrue(canMigrate(5, 5, emptyList()))
    }

    @Test
    fun currentRegistry_isConsistentWithDatabaseVersion() {
        assertEquals(NamazDatabase.VERSION, NamazDatabaseMigrations.currentVersion)
    }

    @Test
    fun currentRegistry_hasNoDuplicateSourceVersions() {
        val sources = NamazDatabaseMigrations.registry.map { it.from }
        assertEquals(sources.size, sources.toSet().size)
    }

    @Test
    fun currentRegistry_coversEveryVersionFromZikirIntroduction() {
        val registry = NamazDatabaseMigrations.registry
        val firstUserDataVersion = NamazDatabaseMigrations.currentVersion
        for (version in firstUserDataVersion until NamazDatabaseMigrations.currentVersion) {
            assertTrue(
                "v$version -> v${NamazDatabaseMigrations.currentVersion} için migration yok",
                canMigrate(version, NamazDatabaseMigrations.currentVersion, registry)
            )
        }
    }

    @Test
    fun currentRegistry_registryVersionStaysBelowCurrentUntilMigrationExists() {
        NamazDatabaseMigrations.registry.forEach { spec ->
            assertTrue(spec.to <= NamazDatabaseMigrations.currentVersion)
        }
    }
}
