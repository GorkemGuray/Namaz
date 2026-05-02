package co.gorkem.namaz.domain.model

data class PrayerTime(
    val date: String,
    val gregorianDate: String,
    val imsak: String,
    val gunes: String,
    val ogle: String,
    val ikindi: String,
    val aksam: String,
    val yatsi: String,
    val timezone: String
) {
    fun getTimeForName(name: PrayerName): String {
        return when (name) {
            PrayerName.IMSAK -> imsak
            PrayerName.GUNES -> gunes
            PrayerName.OGLE -> ogle
            PrayerName.IKINDI -> ikindi
            PrayerName.AKSAM -> aksam
            PrayerName.YATSI -> yatsi
        }
    }

    fun getAllPrayers(): List<Pair<PrayerName, String>> {
        return listOf(
            PrayerName.IMSAK to imsak,
            PrayerName.GUNES to gunes,
            PrayerName.OGLE to ogle,
            PrayerName.IKINDI to ikindi,
            PrayerName.AKSAM to aksam,
            PrayerName.YATSI to yatsi
        )
    }
}
