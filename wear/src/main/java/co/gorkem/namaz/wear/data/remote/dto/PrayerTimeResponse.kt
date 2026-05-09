package co.gorkem.namaz.wear.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CalendarResponse(
    val code: Int,
    val status: String,
    val data: List<DayData>
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
    @SerializedName("Maghrib") val maghrib: String,
    @SerializedName("Isha") val isha: String,
    @SerializedName("Imsak") val imsak: String
)

data class DateInfo(
    val readable: String,
    val gregorian: GregorianDate
)

data class GregorianDate(
    val date: String
)

data class Meta(
    val timezone: String,
    val method: Method
)

data class Method(
    val id: Int,
    val name: String
)
