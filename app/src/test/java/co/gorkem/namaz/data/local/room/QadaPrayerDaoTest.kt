package co.gorkem.namaz.data.local.room

import co.gorkem.namaz.data.repository.QadaRepositoryImpl
import co.gorkem.namaz.domain.model.PrayerName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class QadaPrayerDaoTest {

    private class FakeQadaPrayerDao : QadaPrayerDao {
        private val storage = MutableStateFlow<List<QadaPrayerEntity>>(emptyList())
        private var autoId = 1L

        override suspend fun insert(entity: QadaPrayerEntity): Long {
            val assignedId = if (entity.id == 0L) autoId++ else entity.id
            val stored = entity.copy(id = assignedId)
            storage.value = storage.value + stored
            return assignedId
        }

        override suspend fun insertAll(entities: List<QadaPrayerEntity>) {
            entities.forEach { insert(it) }
        }

        override suspend fun update(entity: QadaPrayerEntity) {
            storage.value = storage.value.map { if (it.id == entity.id) entity else it }
        }

        override suspend fun delete(entity: QadaPrayerEntity) {
            storage.value = storage.value.filterNot { it.id == entity.id }
        }

        override suspend fun getById(id: Long): QadaPrayerEntity? {
            return storage.value.find { it.id == id }
        }

        override fun getAll(): Flow<List<QadaPrayerEntity>> {
            return storage.map { list -> list.sortedWith(compareByDescending<QadaPrayerEntity> { it.dateMillis }.thenByDescending { it.id }) }
        }

        override fun getByPrayer(prayerName: String): Flow<List<QadaPrayerEntity>> {
            return storage.map { list ->
                list.filter { it.prayerName == prayerName }
                    .sortedWith(compareByDescending<QadaPrayerEntity> { it.dateMillis }.thenByDescending { it.id })
            }
        }

        override fun countByPrayer(prayerName: String): Flow<Int> {
            return storage.map { list -> list.count { it.prayerName == prayerName && !it.isDone } }
        }

        override suspend fun getCountByPrayer(prayerName: String): Int {
            return storage.value.count { it.prayerName == prayerName && !it.isDone }
        }

        override suspend fun getOldestUndone(prayerName: String): QadaPrayerEntity? {
            return storage.value
                .filter { it.prayerName == prayerName && !it.isDone }
                .minWithOrNull(compareBy<QadaPrayerEntity> { it.dateMillis }.thenBy { it.id })
        }

        override suspend fun getLatestDone(prayerName: String): QadaPrayerEntity? {
            return storage.value
                .filter { it.prayerName == prayerName && it.isDone }
                .maxWithOrNull(compareBy<QadaPrayerEntity> { it.dateMillis }.thenBy { it.id })
        }

        override suspend fun deleteAllByPrayer(prayerName: String) {
            storage.value = storage.value.filterNot { it.prayerName == prayerName }
        }
    }

    private lateinit var dao: FakeQadaPrayerDao
    private lateinit var repository: QadaRepositoryImpl

    @Before
    fun setUp() {
        dao = FakeQadaPrayerDao()
        repository = QadaRepositoryImpl(dao)
    }

    @Test
    fun crudOperations_workCorrectly() = runTest {
        val id = dao.insert(
            QadaPrayerEntity(
                prayerName = "IMSAK",
                dateMillis = 1000L,
                isDone = false
            )
        )
        assertTrue(id > 0)

        val retrieved = dao.getById(id)
        assertNotNull(retrieved)
        assertEquals("IMSAK", retrieved?.prayerName)
        assertEquals(false, retrieved?.isDone)

        dao.update(retrieved!!.copy(isDone = true))
        val updated = dao.getById(id)
        assertEquals(true, updated?.isDone)

        dao.delete(updated!!)
        assertNull(dao.getById(id))
    }

    @Test
    fun countByPrayer_onlyCountsUndonePrayers() = runTest {
        dao.insert(QadaPrayerEntity(prayerName = "OGLE", dateMillis = 1000L, isDone = false))
        dao.insert(QadaPrayerEntity(prayerName = "OGLE", dateMillis = 2000L, isDone = false))
        dao.insert(QadaPrayerEntity(prayerName = "OGLE", dateMillis = 3000L, isDone = true))

        assertEquals(2, dao.getCountByPrayer("OGLE"))
        assertEquals(2, dao.countByPrayer("OGLE").first())
    }

    @Test
    fun repositoryAddAndComplete_updatesCountsAndPersistence() = runTest {
        repository.addQada("IKINDI", count = 3)
        assertEquals(3, repository.getCountByPrayer("IKINDI").first())

        repository.completeQada("IKINDI")
        assertEquals(2, repository.getCountByPrayer("IKINDI").first())

        val allCounts = repository.getQadaCounts().first()
        assertEquals(2, allCounts[PrayerName.IKINDI])
        assertEquals(0, allCounts[PrayerName.YATSI])
    }

    @Test
    fun getOldestUndone_picksEarliestCreatedPrayer() = runTest {
        dao.insert(QadaPrayerEntity(prayerName = "AKSAM", dateMillis = 5000L, isDone = false))
        dao.insert(QadaPrayerEntity(prayerName = "AKSAM", dateMillis = 2000L, isDone = false))
        dao.insert(QadaPrayerEntity(prayerName = "AKSAM", dateMillis = 8000L, isDone = false))

        val oldest = dao.getOldestUndone("AKSAM")
        assertNotNull(oldest)
        assertEquals(2000L, oldest?.dateMillis)
    }
}
