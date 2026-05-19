package co.gorkem.namaz.wear

import android.content.Context
import co.gorkem.namaz.wear.R

fun prayerNameFromEnum(context: Context, name: String): String {
    return when (name) {
        "IMSAK" -> context.getString(R.string.prayer_imsak)
        "GUNES" -> context.getString(R.string.prayer_gunes)
        "OGLE" -> context.getString(R.string.prayer_ogle)
        "IKINDI" -> context.getString(R.string.prayer_ikindi)
        "AKSAM" -> context.getString(R.string.prayer_aksam)
        "YATSI" -> context.getString(R.string.prayer_yatsi)
        else -> name
    }
}
