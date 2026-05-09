package co.gorkem.namaz.data.remote.api

import co.gorkem.namaz.data.remote.dto.NominatimPlace
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApiService {

    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("addressdetails") addressDetails: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("accept-language") language: String = "tr"
    ): List<NominatimPlace>
}
