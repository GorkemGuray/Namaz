package co.gorkem.namaz.wear.data

import com.google.gson.Gson

data class WearPrayerData(
    val cityName: String,
    val prayers: List<WearPrayerItem>,
    val nextPrayer: WearNextPrayer?,
    val timestamp: Long = 0L
)

data class WearPrayerItem(
    val name: String,
    val time: String
)

data class WearNextPrayer(
    val name: String,
    val time: String,
    val remainingMinutes: Long
) {
    companion object {
        private val gson = Gson()
        fun fromJson(json: String): WearPrayerData =
            gson.fromJson(json, WearPrayerData::class.java)
    }
}
