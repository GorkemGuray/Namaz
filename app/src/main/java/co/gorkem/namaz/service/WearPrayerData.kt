package co.gorkem.namaz.service

import com.google.gson.Gson

data class WearPrayerData(
    val cityName: String,
    val prayers: List<WearPrayerItem>,
    val nextPrayer: WearNextPrayer?,
    val timestamp: Long = System.currentTimeMillis()
)

data class WearPrayerItem(
    val name: String,
    val time: String,
    val turkishName: String
)

data class WearNextPrayer(
    val name: String,
    val turkishName: String,
    val time: String,
    val remainingMinutes: Long
) {
    companion object {
        private val gson = Gson()

        fun toJson(data: WearPrayerData): String = gson.toJson(data)

        fun fromJson(json: String): WearPrayerData = gson.fromJson(json, WearPrayerData::class.java)
    }
}
