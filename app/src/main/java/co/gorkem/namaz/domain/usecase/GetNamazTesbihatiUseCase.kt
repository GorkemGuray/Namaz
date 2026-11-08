package co.gorkem.namaz.domain.usecase

import co.gorkem.namaz.domain.model.Zikir
import co.gorkem.namaz.domain.repository.ZikirRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNamazTesbihatiUseCase @Inject constructor(
    private val repository: ZikirRepository
) {
    operator fun invoke(): Flow<Zikir?> {
        return repository.getNamazTesbihati()
    }
}
