package co.gorkem.namaz.wear.data.remote.api

import co.gorkem.namaz.wear.data.remote.dto.PrayerTimesResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ImsakiyemApiService {

    @GET("api/prayer-times/{districtId}/monthly")
    suspend fun getMonthlyPrayerTimes(
        @Path("districtId") districtId: String
    ): PrayerTimesResponse
}
