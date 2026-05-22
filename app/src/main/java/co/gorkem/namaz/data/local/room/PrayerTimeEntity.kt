package co.gorkem.namaz.data.local.room

import androidx.room.Entity

@Entity(
    tableName = "prayer_times",
    primaryKeys = ["date", "cityName", "cacheMethod", "cacheSchool", "cacheDataSource"]
)
data class PrayerTimeEntity(
    val date: String,
    val cityName: String,
    val gregorianDate: String,
    val hijriDay: String,
    val hijriMonthNumber: Int,
    val hijriYear: String,
    val imsak: String,
    val gunes: String,
    val ogle: String,
    val ikindi: String,
    val aksam: String,
    val yatsi: String,
    val timezone: String,
    val cacheMonth: Int,
    val cacheYear: Int,
    val cacheMethod: Int,
    val cacheSchool: Int,
    val cacheDataSource: Int,
    val cachedAt: Long = System.currentTimeMillis()
)
