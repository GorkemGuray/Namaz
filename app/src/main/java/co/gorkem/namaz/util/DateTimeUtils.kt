package co.gorkem.namaz.util

import android.content.Context
import co.gorkem.namaz.R
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeUtils {
    val DATE_FORMATTER: DateTimeFormatter = co.gorkem.namaz.core.DateTimeUtils.DATE_FORMATTER
    val TIME_FORMATTER: DateTimeFormatter = co.gorkem.namaz.core.DateTimeUtils.TIME_FORMATTER

    fun getShortHijriMonth(context: Context, monthNumber: Int): String {
        return getShortHijriMonth(monthNumber = monthNumber, context = context)
    }

    fun getShortHijriMonth(monthNumber: Int, locale: Locale = Locale.getDefault(), context: Context? = null): String {
        val ctx = context ?: runCatching {
            Class.forName("android.app.ActivityThread")
                .getMethod("currentApplication")
                .invoke(null) as? Context
        }.getOrNull()
        return when (monthNumber) {
            8 -> ctx?.getString(R.string.hijri_month_short_shaban)
                ?: co.gorkem.namaz.core.DateTimeUtils.getShortHijriMonth(monthNumber, locale)
            10 -> ctx?.getString(R.string.hijri_month_short_shawwal)
                ?: co.gorkem.namaz.core.DateTimeUtils.getShortHijriMonth(monthNumber, locale)
            else -> co.gorkem.namaz.core.DateTimeUtils.getShortHijriMonth(monthNumber, locale)
        }
    }
}
