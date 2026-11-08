package co.gorkem.namaz.domain.usecase

import co.gorkem.namaz.domain.model.QadaPrayer
import co.gorkem.namaz.domain.repository.QadaRepository
import javax.inject.Inject

class ToggleQadaUseCase @Inject constructor(
    private val repository: QadaRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.toggleQada(id)
    }

    suspend operator fun invoke(qada: QadaPrayer) {
        repository.toggleQada(qada.id)
    }

    suspend operator fun invoke(prayerName: String, isDone: Boolean = true) {
        if (isDone) {
            repository.completeQada(prayerName)
        } else {
            repository.addQada(prayerName)
        }
    }

    suspend fun increment(prayerName: String, count: Int = 1) {
        repository.addQada(prayerName, count)
    }

    suspend fun decrement(prayerName: String) {
        repository.completeQada(prayerName)
    }
}
