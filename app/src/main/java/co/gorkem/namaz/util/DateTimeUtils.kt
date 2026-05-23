package co.gorkem.namaz.util

import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeUtils {
    val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun getShortHijriMonth(monthNumber: Int, locale: Locale = Locale.getDefault()): String {
        val isTr = locale.language == "tr"
        return when (monthNumber) {
            1 -> if (isTr) "Muh" else "Muh"
            2 -> if (isTr) "Saf" else "Saf"
            3 -> if (isTr) "Reb I" else "Rab I"
            4 -> if (isTr) "Reb II" else "Rab II"
            5 -> if (isTr) "Cem I" else "Jum I"
            6 -> if (isTr) "Cem II" else "Jum II"
            7 -> if (isTr) "Rec" else "Raj"
            8 -> if (isTr) "Şab" else "Sha"
            9 -> if (isTr) "Ram" else "Ram"
            10 -> if (isTr) "Şev" else "Shaw"
            11 -> if (isTr) "Zil.K" else "Dhu.Q"
            12 -> if (isTr) "Zil.H" else "Dhu.H"
            else -> ""
        }
    }
}
