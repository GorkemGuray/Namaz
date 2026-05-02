package co.gorkem.namaz.data.remote.api

import co.gorkem.namaz.data.remote.dto.CalendarResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AladhanApiService {

    @GET("v1/calendar")
    suspend fun getPrayerTimes(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int = 13,
        @Query("month") month: Int,
        @Query("year") year: Int,
        @Query("annual") annual: String = "false"
    ): CalendarResponse
}
