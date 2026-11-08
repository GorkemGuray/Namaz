package co.gorkem.namaz.domain.model

import androidx.annotation.StringRes
import co.gorkem.namaz.R
import java.util.Locale

enum class PrayerName(@StringRes val nameRes: Int) {
    IMSAK(R.string.prayer_imsak),
    GUNES(R.string.prayer_gunes),
    OGLE(R.string.prayer_ogle),
    IKINDI(R.string.prayer_ikindi),
    AKSAM(R.string.prayer_aksam),
    YATSI(R.string.prayer_yatsi);

    companion object {
        fun fromName(name: String): PrayerName {
            return when (name.lowercase(Locale.forLanguageTag("tr"))) {
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
