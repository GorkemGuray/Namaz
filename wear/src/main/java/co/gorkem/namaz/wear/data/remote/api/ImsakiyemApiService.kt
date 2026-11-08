package co.gorkem.namaz.wear.data.remote.api

import co.gorkem.namaz.wear.data.remote.dto.DistrictSearchResponse
import co.gorkem.namaz.wear.data.remote.dto.PrayerTimesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ImsakiyemApiService {

    @GET("api/locations/search/districts")
    suspend fun searchDistricts(
        @Query("q") query: String,
        @Query("countryId") countryId: Int = 2
    ): DistrictSearchResponse

    @GET("api/prayer-times/{districtId}/monthly")
    suspend fun getMonthlyPrayerTimes(
        @Path("districtId") districtId: String
    ): PrayerTimesResponse
}
