package co.gorkem.namaz.domain.model

data class LocationSearchResult(
    val displayName: String,
    val cityName: String,
    val districtName: String,
    val countryName: String,
    val latitude: Double,
    val longitude: Double,
    val districtId: String = ""
)
