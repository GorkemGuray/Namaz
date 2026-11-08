package co.gorkem.namaz.domain.repository

import co.gorkem.namaz.domain.model.Zikir
import kotlinx.coroutines.flow.Flow

interface ZikirRepository {
    fun getAllZikirs(): Flow<List<Zikir>>
    fun getNamazTesbihati(): Flow<Zikir?>
    suspend fun insertZikir(zikir: Zikir)
    suspend fun updateZikir(zikir: Zikir)
    suspend fun deleteZikir(zikir: Zikir)
    suspend fun updateCountAndPhase(id: Int, count: Int, phase: Int)
}
