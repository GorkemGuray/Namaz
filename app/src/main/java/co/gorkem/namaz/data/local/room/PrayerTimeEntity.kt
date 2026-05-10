package co.gorkem.namaz.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_times")
data class PrayerTimeEntity(
    @PrimaryKey
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
    val cachedAt: Long = System.currentTimeMillis()
)
