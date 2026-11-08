package co.gorkem.namaz.wear.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PrayerTimesResponse(
    val success: Boolean = false,
    val code: Int = 0,
    val message: String = "",
    val data: List<DayPrayerDto> = emptyList()
)

data class DayPrayerDto(
    val date: String = "",
    val times: PrayerTimesDto = PrayerTimesDto()
)

data class PrayerTimesDto(
    val imsak: String = "",
    val gunes: String = "",
    val ogle: String = "",
    val ikindi: String = "",
    val aksam: String = "",
    val yatsi: String = ""
)

data class DistrictSearchResponse(
    val success: Boolean = false,
    val code: Int = 0,
    val message: String = "",
    val data: List<DistrictDto> = emptyList()
)

data class DistrictDto(
    @SerializedName("_id") val id: String = "",
    val name: String = "",
    @SerializedName("state_id") val stateId: String = ""
)
