package co.gorkem.namaz.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: T,
    val meta: MetaDto?
)

data class MetaDto(
    @SerializedName("totalCount") val totalCount: Int? = null
)

data class StateDto(
    @SerializedName("_id") val id: String,
    val name: String,
    @SerializedName("name_en") val nameEn: String,
    @SerializedName("country_id") val countryId: String
)

data class DistrictDto(
    @SerializedName("_id") val id: String,
    val name: String,
    @SerializedName("name_en") val nameEn: String,
    @SerializedName("state_id") val stateId: String,
    @SerializedName("country_id") val countryId: String
)

data class DayPrayerDto(
    @SerializedName("_id") val id: String,
    val date: String,
    val times: PrayerTimesDto,
    @SerializedName("hijri_date") val hijriDate: HijriDateDto?
)

data class PrayerTimesDto(
    val imsak: String,
    val gunes: String,
    val ogle: String,
    val ikindi: String,
    val aksam: String,
    val yatsi: String
)

data class HijriDateDto(
    val day: Int,
    val month: Int,
    @SerializedName("month_name") val monthName: String,
    @SerializedName("month_name_en") val monthNameEn: String,
    val year: Int,
    @SerializedName("full_date") val fullDate: String
)

typealias DistrictsResponse = BaseResponse<List<DistrictDto>>
typealias SearchDistrictsResponse = BaseResponse<List<DistrictDto>>
typealias PrayerTimesResponse = BaseResponse<List<DayPrayerDto>>
