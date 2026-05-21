package co.gorkem.namaz.wear.data.remote.dto

data class PrayerTimesResponse(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: List<DayPrayerDto>
)

data class DayPrayerDto(
    val date: String,
    val times: PrayerTimesDto
)

data class PrayerTimesDto(
    val imsak: String,
    val gunes: String,
    val ogle: String,
    val ikindi: String,
    val aksam: String,
    val yatsi: String
)
