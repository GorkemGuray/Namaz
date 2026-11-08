package co.gorkem.namaz.data.repository

import co.gorkem.namaz.data.local.room.QadaPrayerDao
import co.gorkem.namaz.data.local.room.QadaPrayerEntity
import co.gorkem.namaz.data.local.room.toDomain
import co.gorkem.namaz.data.local.room.toEntity
import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.model.QadaPrayer
import co.gorkem.namaz.domain.repository.QadaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QadaRepositoryImpl @Inject constructor(
    private val qadaPrayerDao: QadaPrayerDao
) : QadaRepository {

    override fun getAll(): Flow<List<QadaPrayer>> = qadaPrayerDao.getAll().map { list ->
        list.map { it.toDomain() }
    }

    override fun getByPrayer(prayerName: String): Flow<List<QadaPrayer>> =
        qadaPrayerDao.getByPrayer(prayerName).map { list ->
            list.map { it.toDomain() }
        }

    override fun getCountByPrayer(prayerName: String): Flow<Int> =
        qadaPrayerDao.countByPrayer(prayerName)

    override fun getQadaCounts(): Flow<Map<PrayerName, Int>> {
        val prayerFlows = PrayerName.values().map { prayer ->
            qadaPrayerDao.countByPrayer(prayer.name).map { count -> prayer to count }
        }
        return combine(prayerFlows) { pairs ->
            pairs.toMap()
        }
    }

    override suspend fun getById(id: Long): QadaPrayer? =
        qadaPrayerDao.getById(id)?.toDomain()

    override suspend fun insert(qada: QadaPrayer): Long =
        qadaPrayerDao.insert(qada.toEntity())

    override suspend fun update(qada: QadaPrayer) =
        qadaPrayerDao.update(qada.toEntity())

    override suspend fun delete(qada: QadaPrayer) =
        qadaPrayerDao.delete(qada.toEntity())

    override suspend fun toggleQada(id: Long) {
        val existing = qadaPrayerDao.getById(id) ?: return
        qadaPrayerDao.update(existing.copy(isDone = !existing.isDone))
    }

    override suspend fun addQada(prayerName: String, count: Int) {
        val now = System.currentTimeMillis()
        repeat(count.coerceAtLeast(1)) {
            qadaPrayerDao.insert(
                QadaPrayerEntity(
                    prayerName = prayerName,
                    dateMillis = now,
                    isDone = false
                )
            )
        }
    }

    override suspend fun completeQada(prayerName: String) {
        val oldestUndone = qadaPrayerDao.getOldestUndone(prayerName) ?: return
        qadaPrayerDao.update(oldestUndone.copy(isDone = true))
    }
}
