package co.gorkem.namaz.domain.model

import java.io.Serializable

data class LocationInfo(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val cityName: String = "",
    val districtName: String = "",
    val districtId: String = ""
) : Serializable {

    val formattedName: String
        get() = buildString {
            val city = cityName.trim()
            val district = districtName.trim()
            if (city.isNotEmpty()) {
                append(city)
                if (district.isNotEmpty() && !district.equals(city, ignoreCase = true)) {
                    append(" - ").append(district)
                }
            } else if (district.isNotEmpty()) {
                append(district)
            }
        }

    companion object {
        val DEFAULT = LocationInfo(
            latitude = 41.0082,
            longitude = 28.9784,
            timezone = "Europe/Istanbul",
            cityName = "İstanbul",
            districtName = "İstanbul",
            districtId = "9541"
        )
    }
}
