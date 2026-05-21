package co.gorkem.namaz.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DistrictSearchResponse @JvmOverloads constructor(
    var success: Boolean = false,
    var code: Int = 0,
    var message: String = "",
    @SerializedName("data") var districts: List<DistrictDto> = emptyList()
)

data class PrayerTimesResponse @JvmOverloads constructor(
    var success: Boolean = false,
    var code: Int = 0,
    var message: String = "",
    @SerializedName("data") var days: List<DayPrayerDto> = emptyList()
)

data class DistrictDto @JvmOverloads constructor(
    @SerializedName("_id") var id: String = "",
    @SerializedName("name") var name: String = "",
    @SerializedName("name_en") var nameEn: String = "",
    @SerializedName("state_id") var stateId: String = "",
    @SerializedName("country_id") var countryId: String = ""
)

data class DayPrayerDto @JvmOverloads constructor(
    @SerializedName("_id") var id: String = "",
    @SerializedName("date") var date: String = "",
    @SerializedName("times") var times: PrayerTimesDto = PrayerTimesDto(),
    @SerializedName("hijri_date") var hijriDate: HijriDateDto? = null
)

data class PrayerTimesDto @JvmOverloads constructor(
    @SerializedName("imsak") var imsak: String = "",
    @SerializedName("gunes") var gunes: String = "",
    @SerializedName("ogle") var ogle: String = "",
    @SerializedName("ikindi") var ikindi: String = "",
    @SerializedName("aksam") var aksam: String = "",
    @SerializedName("yatsi") var yatsi: String = ""
)

data class HijriDateDto @JvmOverloads constructor(
    @SerializedName("day") var day: Int = 0,
    @SerializedName("month") var month: Int = 0,
    @SerializedName("month_name") var monthName: String = "",
    @SerializedName("month_name_en") var monthNameEn: String = "",
    @SerializedName("year") var year: Int = 0,
    @SerializedName("full_date") var fullDate: String = ""
)
