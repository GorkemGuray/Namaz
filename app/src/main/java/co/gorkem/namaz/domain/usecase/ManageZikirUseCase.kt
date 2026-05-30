package co.gorkem.namaz.domain.usecase

import co.gorkem.namaz.data.local.room.ZikirEntity
import co.gorkem.namaz.domain.repository.ZikirRepository
import javax.inject.Inject

class ManageZikirUseCase @Inject constructor(
    private val repository: ZikirRepository
) {
    suspend fun addZikir(title: String, targetCount: Int, isNamazTesbihati: Boolean = false) {
        repository.insertZikir(
            ZikirEntity(
                title = title,
                targetCount = targetCount,
                currentCount = 0,
                isNamazTesbihati = isNamazTesbihati,
                currentPhase = 0
            )
        )
    }

    suspend fun deleteZikir(zikir: ZikirEntity) {
        repository.deleteZikir(zikir)
    }

    suspend fun updateCount(id: Int, count: Int, phase: Int) {
        repository.updateCountAndPhase(id, count, phase)
    }

    suspend fun resetZikir(id: Int) {
        repository.updateCountAndPhase(id, 0, 0)
    }
}
