package co.gorkem.namaz.domain.usecase

import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.repository.QadaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQadaCountsUseCase @Inject constructor(
    private val repository: QadaRepository
) {
    operator fun invoke(): Flow<Map<PrayerName, Int>> {
        return repository.getQadaCounts()
    }

    fun getCount(prayerName: String): Flow<Int> {
        return repository.getCountByPrayer(prayerName)
    }

    fun getCount(prayer: PrayerName): Flow<Int> {
        return repository.getCountByPrayer(prayer.name)
    }
}
