package co.gorkem.namaz.domain.model

data class LocationInfo(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val cityName: String = "",
    val districtName: String = ""
) {
    companion object {
        val DEFAULT = LocationInfo(
            latitude = 41.0082,
            longitude = 28.9784,
            timezone = "Europe/Istanbul",
            cityName = "İstanbul",
            districtName = "Fatih"
        )
    }
}
