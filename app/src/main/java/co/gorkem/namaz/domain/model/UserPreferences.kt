package co.gorkem.namaz.domain.model

data class RecentCity(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val districtId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class UserPreferences(
    val notificationsEnabled: Boolean = true,
    val notificationPermissionRequested: Boolean = false,
    val notificationMinutesBefore: Int = 15,
    val prayerVoiceEnabled: Boolean = false,
    val calculationMethod: Int = 13,
    val school: Int = 0,
    val useLocalCalculation: Boolean = false,
    val dataSource: Int = 0,
    val language: String = "tr",
    val appTheme: Int = 0,
    val manualCityName: String = "",
    val manualDistrictName: String = "",
    val manualDistrictId: String = "",
    val manualLatitude: Double = 0.0,
    val manualLongitude: Double = 0.0,
    val recentCities: List<RecentCity> = emptyList(),
    val lastAutoLatitude: Double = 0.0,
    val lastAutoLongitude: Double = 0.0,
    val lastAutoCityName: String = "",
    val lastAutoDistrictName: String = "",
    val lastAutoDistrictId: String = "",
    val lastAutoTimezone: String = "Europe/Istanbul",
    val manualTimezone: String = "Europe/Istanbul",
    val lastUpdateCheckMs: Long = 0L
)

val UserPreferences.activeDataSource: Int
    get() = if (useLocalCalculation) 2 else dataSource

fun UserPreferences.getLocationInfo(): LocationInfo {
    return if (manualCityName.isNotEmpty()) {
        LocationInfo(
            latitude = manualLatitude,
            longitude = manualLongitude,
            timezone = manualTimezone.ifEmpty { "Europe/Istanbul" },
            cityName = manualCityName,
            districtName = manualDistrictName,
            districtId = manualDistrictId
        )
    } else if (lastAutoCityName.isNotEmpty()) {
        LocationInfo(
            latitude = lastAutoLatitude,
            longitude = lastAutoLongitude,
            timezone = lastAutoTimezone.ifEmpty { "Europe/Istanbul" },
            cityName = lastAutoCityName,
            districtName = lastAutoDistrictName,
            districtId = lastAutoDistrictId
        )
    } else {
        LocationInfo.DEFAULT
    }
}
