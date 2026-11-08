package co.gorkem.namaz.data.repository

import co.gorkem.namaz.data.local.room.ZikirDao
import co.gorkem.namaz.data.local.room.toDomain
import co.gorkem.namaz.data.local.room.toEntity
import co.gorkem.namaz.domain.model.Zikir
import co.gorkem.namaz.domain.repository.ZikirRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ZikirRepositoryImpl @Inject constructor(
    private val zikirDao: ZikirDao
) : ZikirRepository {

    override fun getAllZikirs(): Flow<List<Zikir>> = zikirDao.getAllZikirs().map { list ->
        list.map { it.toDomain() }
    }

    override fun getNamazTesbihati(): Flow<Zikir?> = zikirDao.getNamazTesbihati().map {
        it?.toDomain()
    }

    override suspend fun insertZikir(zikir: Zikir) {
        zikirDao.insertZikir(zikir.toEntity())
    }

    override suspend fun updateZikir(zikir: Zikir) {
        zikirDao.updateZikir(zikir.toEntity())
    }

    override suspend fun deleteZikir(zikir: Zikir) {
        zikirDao.deleteZikir(zikir.toEntity())
    }

    override suspend fun updateCountAndPhase(id: Int, count: Int, phase: Int) {
        zikirDao.updateCountAndPhase(id, count, phase)
    }
}
