package co.gorkem.namaz.domain.usecase

import co.gorkem.namaz.data.local.room.ZikirEntity
import co.gorkem.namaz.domain.repository.ZikirRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetZikirsUseCase @Inject constructor(
    private val repository: ZikirRepository
) {
    operator fun invoke(): Flow<List<ZikirEntity>> {
        return repository.getAllZikirs()
    }
}
