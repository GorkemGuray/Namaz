package co.gorkem.namaz.data.local.room

import co.gorkem.namaz.domain.model.PrayerTime

fun PrayerTimeEntity.toDomainModel(): PrayerTime = PrayerTime(
    date = date,
    gregorianDate = gregorianDate,
    hijriDay = hijriDay,
    hijriMonthNumber = hijriMonthNumber,
    hijriYear = hijriYear,
    imsak = imsak,
    gunes = gunes,
    ogle = ogle,
    ikindi = ikindi,
    aksam = aksam,
    yatsi = yatsi,
    timezone = timezone
)

fun PrayerTime.toEntity(
    cityName: String,
    month: Int,
    year: Int,
    method: Int,
    school: Int,
    dataSource: Int
): PrayerTimeEntity = PrayerTimeEntity(
    date = date,
    cityName = cityName,
    gregorianDate = gregorianDate,
    hijriDay = hijriDay,
    hijriMonthNumber = hijriMonthNumber,
    hijriYear = hijriYear,
    imsak = imsak,
    gunes = gunes,
    ogle = ogle,
    ikindi = ikindi,
    aksam = aksam,
    yatsi = yatsi,
    timezone = timezone,
    cacheMonth = month,
    cacheYear = year,
    cacheMethod = method,
    cacheSchool = school,
    cacheDataSource = dataSource
)
