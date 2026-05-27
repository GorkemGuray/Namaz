package co.gorkem.namaz.service

import com.google.gson.Gson

data class WearPrayerData(
    val cityName: String,
    val prayers: List<WearPrayerItem>,
    val nextPrayer: WearNextPrayer?,
    val kerahat: WearKerahatInfo?,
    val themeId: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        private val gson = Gson()
        fun toJson(data: WearPrayerData): String = gson.toJson(data)
        fun fromJson(json: String): WearPrayerData = gson.fromJson(json, WearPrayerData::class.java)
    }
}

data class WearPrayerItem(
    val name: String,
    val time: String
)

data class WearNextPrayer(
    val name: String,
    val time: String,
    val remainingMinutes: Long,
    val targetTimeMillis: Long = 0L
)

data class WearKerahatInfo(
    val isCurrentlyKerahat: Boolean,
    val prayerName: String,
    val remainingMinutes: Long
)
