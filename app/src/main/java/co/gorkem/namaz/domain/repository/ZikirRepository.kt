package co.gorkem.namaz.domain.repository

import co.gorkem.namaz.data.local.room.ZikirEntity
import kotlinx.coroutines.flow.Flow

interface ZikirRepository {
    fun getAllZikirs(): Flow<List<ZikirEntity>>
    fun getNamazTesbihati(): Flow<ZikirEntity?>
    suspend fun insertZikir(zikir: ZikirEntity)
    suspend fun updateZikir(zikir: ZikirEntity)
    suspend fun deleteZikir(zikir: ZikirEntity)
    suspend fun updateCountAndPhase(id: Int, count: Int, phase: Int)
}
