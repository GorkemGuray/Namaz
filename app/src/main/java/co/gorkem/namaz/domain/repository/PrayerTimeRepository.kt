package co.gorkem.namaz.domain.repository

import co.gorkem.namaz.data.remote.NetworkResult
import co.gorkem.namaz.domain.model.LocationInfo
import co.gorkem.namaz.domain.model.PrayerTime
import kotlinx.coroutines.flow.Flow

interface PrayerTimeRepository {
    fun getPrayerTimes(
        location: LocationInfo,
        month: Int,
        year: Int,
        method: Int = 13,
        school: Int = 1
    ): Flow<NetworkResult<List<PrayerTime>>>
}
