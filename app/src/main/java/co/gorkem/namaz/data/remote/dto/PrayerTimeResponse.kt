package co.gorkem.namaz.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CalendarResponse(
    val code: Int,
    val status: String,
    val data: Map<String, DayData>
)

data class DayData(
    val timings: Timings,
    val date: DateInfo,
    val meta: Meta
)

data class Timings(
    @SerializedName("Fajr") val fajr: String,
    @SerializedName("Sunrise") val sunrise: String,
    @SerializedName("Dhuhr") val dhuhr: String,
    @SerializedName("Asr") val asr: String,
    @SerializedName("Sunset") val sunset: String,
    @SerializedName("Maghrib") val maghrib: String,
    @SerializedName("Isha") val isha: String,
    @SerializedName("Imsak") val imsak: String,
    @SerializedName("Midnight") val midnight: String,
    @SerializedName("Firstthird") val firstthird: String,
    @SerializedName("Lastthird") val lastthird: String
)

data class DateInfo(
    val readable: String,
    val timestamp: String,
    val gregorian: GregorianDate
)

data class GregorianDate(
    val date: String,
    val format: String,
    val day: String,
    val weekday: Weekday,
    val month: MonthInfo,
    val year: String,
    val designation: Designation
)

data class Weekday(
    val en: String,
    val ar: String
)

data class MonthInfo(
    val number: Int,
    val en: String,
    val ar: String
)

data class Designation(
    val abbreviated: String,
    val expanded: String
)

data class Meta(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val method: Method
)

data class Method(
    val id: Int,
    val name: String,
    val params: MethodParams
)

data class MethodParams(
    @SerializedName("Fajr") val fajr: Double,
    @SerializedName("Isha") val isha: Double
)
