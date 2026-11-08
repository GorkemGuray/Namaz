package co.gorkem.namaz.domain.repository

import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.model.QadaPrayer
import kotlinx.coroutines.flow.Flow

interface QadaRepository {
    fun getAll(): Flow<List<QadaPrayer>>
    fun getByPrayer(prayerName: String): Flow<List<QadaPrayer>>
    fun getCountByPrayer(prayerName: String): Flow<Int>
    fun getQadaCounts(): Flow<Map<PrayerName, Int>>
    suspend fun getById(id: Long): QadaPrayer?
    suspend fun insert(qada: QadaPrayer): Long
    suspend fun update(qada: QadaPrayer)
    suspend fun delete(qada: QadaPrayer)
    suspend fun toggleQada(id: Long)
    suspend fun addQada(prayerName: String, count: Int = 1)
    suspend fun completeQada(prayerName: String)
}
