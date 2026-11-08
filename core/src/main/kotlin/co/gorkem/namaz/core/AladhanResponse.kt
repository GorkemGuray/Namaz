package co.gorkem.namaz.core

import com.google.gson.annotations.SerializedName

data class AladhanResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: List<AladhanDayData>
)

data class AladhanDayData(
    @SerializedName("timings") val timings: AladhanTimings,
    @SerializedName("date") val date: AladhanDate,
    @SerializedName("meta") val meta: AladhanMeta
)

data class AladhanTimings(
    @SerializedName("Fajr") val fajr: String,
    @SerializedName("Sunrise") val sunrise: String,
    @SerializedName("Dhuhr") val dhuhr: String,
    @SerializedName("Asr") val asr: String,
    @SerializedName("Maghrib") val maghrib: String,
    @SerializedName("Isha") val isha: String
)

data class AladhanDate(
    @SerializedName("gregorian") val gregorian: AladhanGregorianDate,
    @SerializedName("hijri") val hijri: AladhanHijriDate
)

data class AladhanGregorianDate(
    @SerializedName("date") val date: String,
    @SerializedName("day") val day: String,
    @SerializedName("month") val month: AladhanMonth,
    @SerializedName("year") val year: String
)

data class AladhanMonth(
    @SerializedName("number") val number: Int,
    @SerializedName("en") val nameEn: String
)

data class AladhanHijriDate(
    @SerializedName("day") val day: String,
    @SerializedName("month") val month: AladhanHijriMonth,
    @SerializedName("year") val year: String
)

data class AladhanHijriMonth(
    @SerializedName("number") val number: Int,
    @SerializedName("en") val nameEn: String,
    @SerializedName("ar") val nameAr: String
)

data class AladhanMeta(
    @SerializedName("timezone") val timezone: String
)
