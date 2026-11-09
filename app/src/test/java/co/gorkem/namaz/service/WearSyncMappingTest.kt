package co.gorkem.namaz.service

import co.gorkem.namaz.domain.model.LocationInfo
import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.model.PrayerTime
import org.junit.Assert.assertEquals
import org.junit.Test

class WearSyncMappingTest {

    private fun createPrayerTime(
        date: String = "2026-11-09",
        imsak: String = "05:50",
        gunes: String = "07:15",
        ogle: String = "12:55",
        ikindi: String = "15:40",
        aksam: String = "18:10",
        yatsi: String = "19:30"
    ): PrayerTime = PrayerTime(
        date = date,
        gregorianDate = "",
        hijriDay = "",
        hijriMonthNumber = 0,
        hijriYear = "",
        imsak = imsak,
        gunes = gunes,
        ogle = ogle,
        ikindi = ikindi,
        aksam = aksam,
        yatsi = yatsi,
        timezone = "Europe/Istanbul"
    )

    private val fakeStringResolver: (Int) -> String = { resId ->
        when (resId) {
            PrayerName.IMSAK.nameRes -> "İmsak"
            PrayerName.GUNES.nameRes -> "Güneş"
            PrayerName.OGLE.nameRes -> "Öğle"
            PrayerName.IKINDI.nameRes -> "İkindi"
            PrayerName.AKSAM.nameRes -> "Akşam"
            PrayerName.YATSI.nameRes -> "Yatsı"
            else -> "Res_$resId"
        }
    }

    @Test
    fun cityAndDistrict_mapsFormattedNameToWearDataCityName() {
        val location = LocationInfo(
            latitude = 41.0742,
            longitude = 28.2481,
            timezone = "Europe/Istanbul",
            cityName = "İstanbul",
            districtName = "Silivri",
            districtId = "9548"
        )
        val today = createPrayerTime()

        val wearData = WearSyncManager.createWearPrayerData(
            todayData = today,
            location = location,
            themeId = 0,
            dataSource = 0,
            stringResolver = fakeStringResolver,
            nextPrayerInfo = null,
            kerahatInfo = null
        )

        // Saate iletilen cityName telefondaki formattedName ile birebir aynı olmalı
        assertEquals("İstanbul - Silivri", wearData.cityName)
        assertEquals(location.formattedName, wearData.cityName)

        // JSON serileştirme ve saat tarafı parse sonrası doğrulaması
        val json = WearPrayerData.toJson(wearData)
        val parsed = WearPrayerData.fromJson(json)
        assertEquals("İstanbul - Silivri", parsed?.cityName)
    }

    @Test
    fun cityWithoutDistrict_usesCityNameDirectly() {
        val location = LocationInfo(
            latitude = 39.9334,
            longitude = 32.8597,
            timezone = "Europe/Istanbul",
            cityName = "Ankara",
            districtName = "",
            districtId = "9206"
        )
        val today = createPrayerTime()

        val wearData = WearSyncManager.createWearPrayerData(
            todayData = today,
            location = location,
            themeId = 0,
            dataSource = 0,
            stringResolver = fakeStringResolver,
            nextPrayerInfo = null,
            kerahatInfo = null
        )

        assertEquals("Ankara", wearData.cityName)
        assertEquals(location.formattedName, wearData.cityName)
    }

    @Test
    fun districtMatchesCity_doesNotDuplicate() {
        val location = LocationInfo(
            latitude = 41.0082,
            longitude = 28.9784,
            timezone = "Europe/Istanbul",
            cityName = "İstanbul",
            districtName = "İstanbul",
            districtId = "9541"
        )
        val today = createPrayerTime()

        val wearData = WearSyncManager.createWearPrayerData(
            todayData = today,
            location = location,
            themeId = 0,
            dataSource = 0,
            stringResolver = fakeStringResolver,
            nextPrayerInfo = null,
            kerahatInfo = null
        )

        assertEquals("İstanbul", wearData.cityName)
        assertEquals(location.formattedName, wearData.cityName)
    }

    @Test
    fun onlyDistrictProvided_usesDistrictName() {
        val location = LocationInfo(
            latitude = 41.0742,
            longitude = 28.2481,
            timezone = "Europe/Istanbul",
            cityName = "",
            districtName = "Silivri",
            districtId = "9548"
        )
        val today = createPrayerTime()

        val wearData = WearSyncManager.createWearPrayerData(
            todayData = today,
            location = location,
            themeId = 0,
            dataSource = 0,
            stringResolver = fakeStringResolver,
            nextPrayerInfo = null,
            kerahatInfo = null
        )

        assertEquals("Silivri", wearData.cityName)
        assertEquals(location.formattedName, wearData.cityName)
    }
}
