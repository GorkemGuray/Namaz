package co.gorkem.namaz.domain.usecase

import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.model.QadaPrayer
import co.gorkem.namaz.domain.repository.QadaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class QadaUseCasesTest {

    private class FakeQadaRepository : QadaRepository {
        private val listFlow = MutableStateFlow<List<QadaPrayer>>(emptyList())
        private var idCounter = 1L

        override fun getAll(): Flow<List<QadaPrayer>> = listFlow

        override fun getByPrayer(prayerName: String): Flow<List<QadaPrayer>> =
            listFlow.map { list -> list.filter { it.prayerName == prayerName } }

        override fun getCountByPrayer(prayerName: String): Flow<Int> =
            listFlow.map { list -> list.count { it.prayerName == prayerName && !it.isDone } }

        override fun getQadaCounts(): Flow<Map<PrayerName, Int>> =
            listFlow.map { list ->
                PrayerName.values().associateWith { prayer ->
                    list.count { it.prayerName == prayer.name && !it.isDone }
                }
            }

        override suspend fun getById(id: Long): QadaPrayer? =
            listFlow.value.find { it.id == id }

        override suspend fun insert(qada: QadaPrayer): Long {
            val assignedId = if (qada.id == 0L) idCounter++ else qada.id
            val newEntity = qada.copy(id = assignedId)
            listFlow.value = listFlow.value + newEntity
            return assignedId
        }

        override suspend fun update(qada: QadaPrayer) {
            listFlow.value = listFlow.value.map { if (it.id == qada.id) qada else it }
        }

        override suspend fun delete(qada: QadaPrayer) {
            listFlow.value = listFlow.value.filterNot { it.id == qada.id }
        }

        override suspend fun toggleQada(id: Long) {
            val current = getById(id) ?: return
            update(current.copy(isDone = !current.isDone))
        }

        override suspend fun addQada(prayerName: String, count: Int) {
            val now = System.currentTimeMillis()
            repeat(count.coerceAtLeast(1)) {
                insert(
                    QadaPrayer(
                        id = idCounter++,
                        prayerName = prayerName,
                        dateMillis = now,
                        isDone = false
                    )
                )
            }
        }

        override suspend fun completeQada(prayerName: String) {
            val oldest = listFlow.value
                .filter { it.prayerName == prayerName && !it.isDone }
                .minByOrNull { it.dateMillis } ?: return
            update(oldest.copy(isDone = true))
        }
    }

    private lateinit var repository: FakeQadaRepository
    private lateinit var getQadaCountsUseCase: GetQadaCountsUseCase
    private lateinit var toggleQadaUseCase: ToggleQadaUseCase

    @Before
    fun setUp() {
        repository = FakeQadaRepository()
        getQadaCountsUseCase = GetQadaCountsUseCase(repository)
        toggleQadaUseCase = ToggleQadaUseCase(repository)
    }

    @Test
    fun initialCounts_allSixPrayersAreZero() = runTest {
        val counts = getQadaCountsUseCase().first()
        assertEquals(6, counts.size)
        PrayerName.values().forEach { prayer ->
            assertEquals(0, counts[prayer])
        }
    }

    @Test
    fun incrementQada_increasesDebtCount() = runTest {
        toggleQadaUseCase.increment(PrayerName.IMSAK.name)
        toggleQadaUseCase.increment(PrayerName.IMSAK.name)
        toggleQadaUseCase.increment(PrayerName.OGLE.name)

        val counts = getQadaCountsUseCase().first()
        assertEquals(2, counts[PrayerName.IMSAK])
        assertEquals(1, counts[PrayerName.OGLE])
        assertEquals(0, counts[PrayerName.IKINDI])
    }

    @Test
    fun decrementQada_completesOneDebtAndDecreasesCount() = runTest {
        toggleQadaUseCase.increment(PrayerName.YATSI.name)
        toggleQadaUseCase.increment(PrayerName.YATSI.name)

        var counts = getQadaCountsUseCase().first()
        assertEquals(2, counts[PrayerName.YATSI])

        toggleQadaUseCase.decrement(PrayerName.YATSI.name)
        counts = getQadaCountsUseCase().first()
        assertEquals(1, counts[PrayerName.YATSI])

        toggleQadaUseCase.decrement(PrayerName.YATSI.name)
        counts = getQadaCountsUseCase().first()
        assertEquals(0, counts[PrayerName.YATSI])

        toggleQadaUseCase.decrement(PrayerName.YATSI.name)
        counts = getQadaCountsUseCase().first()
        assertEquals(0, counts[PrayerName.YATSI])
    }

    @Test
    fun toggleById_flipsIsDoneStatus() = runTest {
        val id = repository.insert(
            QadaPrayer(
                prayerName = PrayerName.AKSAM.name,
                isDone = false
            )
        )

        assertEquals(1, getQadaCountsUseCase().first()[PrayerName.AKSAM])

        toggleQadaUseCase(id)
        val updated = repository.getById(id)
        assertTrue(updated?.isDone == true)
        assertEquals(0, getQadaCountsUseCase().first()[PrayerName.AKSAM])

        toggleQadaUseCase(id)
        val toggledBack = repository.getById(id)
        assertFalse(toggledBack?.isDone == true)
        assertEquals(1, getQadaCountsUseCase().first()[PrayerName.AKSAM])
    }

    @Test
    fun toggleByPrayerName_marksDoneOrAddsDebt() = runTest {
        toggleQadaUseCase(PrayerName.GUNES.name, isDone = false)
        assertEquals(1, getQadaCountsUseCase().first()[PrayerName.GUNES])

        toggleQadaUseCase(PrayerName.GUNES.name, isDone = true)
        assertEquals(0, getQadaCountsUseCase().first()[PrayerName.GUNES])
    }
}
