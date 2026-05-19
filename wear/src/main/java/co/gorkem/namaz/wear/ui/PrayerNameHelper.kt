package co.gorkem.namaz.wear.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import co.gorkem.namaz.wear.R

@Composable
fun prayerNameFromEnum(name: String): String {
    return when (name) {
        "IMSAK" -> stringResource(R.string.prayer_imsak)
        "GUNES" -> stringResource(R.string.prayer_gunes)
        "OGLE" -> stringResource(R.string.prayer_ogle)
        "IKINDI" -> stringResource(R.string.prayer_ikindi)
        "AKSAM" -> stringResource(R.string.prayer_aksam)
        "YATSI" -> stringResource(R.string.prayer_yatsi)
        else -> name
    }
}
