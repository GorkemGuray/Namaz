package co.gorkem.namaz.data.remote.api

import co.gorkem.namaz.data.remote.dto.BaseResponse
import co.gorkem.namaz.data.remote.dto.DayPrayerDto
import co.gorkem.namaz.data.remote.dto.DistrictDto
import co.gorkem.namaz.data.remote.dto.StateDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ImsakiyemApiService {

    @GET("api/locations/states")
    suspend fun getStates(
        @Query("countryId") countryId: Int = 2
    ): BaseResponse<List<StateDto>>

    @GET("api/locations/districts")
    suspend fun getDistricts(
        @Query("stateId") stateId: String
    ): BaseResponse<List<DistrictDto>>

    @GET("api/locations/search/districts")
    suspend fun searchDistricts(
        @Query("q") query: String,
        @Query("countryId") countryId: Int = 2
    ): BaseResponse<List<DistrictDto>>

    @GET("api/prayer-times/{districtId}/monthly")
    suspend fun getMonthlyPrayerTimes(
        @Path("districtId") districtId: String,
        @Query("startDate") startDate: String? = null
    ): BaseResponse<List<DayPrayerDto>>
}
