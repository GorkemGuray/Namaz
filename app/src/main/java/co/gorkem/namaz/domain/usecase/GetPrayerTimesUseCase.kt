package co.gorkem.namaz.domain.usecase

import co.gorkem.namaz.data.remote.NetworkResult
import co.gorkem.namaz.domain.model.LocationInfo
import co.gorkem.namaz.domain.model.PrayerTime
import co.gorkem.namaz.domain.repository.PrayerTimeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPrayerTimesUseCase @Inject constructor(
    private val repository: PrayerTimeRepository
) {
    operator fun invoke(
        location: LocationInfo,
        month: Int,
        year: Int,
        method: Int = 13,
        school: Int = 1
    ): Flow<NetworkResult<List<PrayerTime>>> {
        return repository.getPrayerTimes(location, month, year, method, school)
    }
}
