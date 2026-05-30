package co.gorkem.namaz.data.repository

import co.gorkem.namaz.data.local.room.ZikirDao
import co.gorkem.namaz.data.local.room.ZikirEntity
import co.gorkem.namaz.domain.repository.ZikirRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ZikirRepositoryImpl @Inject constructor(
    private val zikirDao: ZikirDao
) : ZikirRepository {

    override fun getAllZikirs(): Flow<List<ZikirEntity>> = zikirDao.getAllZikirs()

    override fun getNamazTesbihati(): Flow<ZikirEntity?> = zikirDao.getNamazTesbihati()

    override suspend fun insertZikir(zikir: ZikirEntity) {
        zikirDao.insertZikir(zikir)
    }

    override suspend fun updateZikir(zikir: ZikirEntity) {
        zikirDao.updateZikir(zikir)
    }

    override suspend fun deleteZikir(zikir: ZikirEntity) {
        zikirDao.deleteZikir(zikir)
    }

    override suspend fun updateCountAndPhase(id: Int, count: Int, phase: Int) {
        zikirDao.updateCountAndPhase(id, count, phase)
    }
}
