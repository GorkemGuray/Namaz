package co.gorkem.namaz.domain.model

enum class PrayerName(val turkishName: String) {
    IMSAK("İmsak"),
    GUNES("Güneş"),
    OGLE("Öğle"),
    IKINDI("İkindi"),
    AKSAM("Akşam"),
    YATSI("Yatsı");

    companion object {
        fun fromName(name: String): PrayerName {
            return when (name.lowercase()) {
                "imsak" -> IMSAK
                "sunrise" -> GUNES
                "dhuhr" -> OGLE
                "asr" -> IKINDI
                "maghrib" -> AKSAM
                "isha" -> YATSI
                else -> throw IllegalArgumentException("Unknown prayer name: $name")
            }
        }
    }
}
