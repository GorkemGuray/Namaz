package co.gorkem.namaz.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NominatimPlace(
    @SerializedName("place_id") val placeId: Long,
    @SerializedName("display_name") val displayName: String,
    @SerializedName("lat") val lat: String,
    @SerializedName("lon") val lon: String,
    @SerializedName("address") val address: NominatimAddress? = null
)

data class NominatimAddress(
    @SerializedName("city") val city: String? = null,
    @SerializedName("town") val town: String? = null,
    @SerializedName("village") val village: String? = null,
    @SerializedName("county") val county: String? = null,
    @SerializedName("state") val state: String? = null,
    @SerializedName("state_district") val stateDistrict: String? = null,
    @SerializedName("country") val country: String? = null,
    @SerializedName("country_code") val countryCode: String? = null
) {
    val cityName: String
        get() = city ?: town ?: village ?: county ?: ""

    val districtName: String
        get() = stateDistrict ?: county ?: state ?: ""
}
