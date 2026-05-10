package co.gorkem.namaz.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Brightness5
import androidx.compose.material.icons.rounded.Brightness7
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import co.gorkem.namaz.domain.model.PrayerName

fun prayerIcon(name: PrayerName): ImageVector = when (name) {
    PrayerName.IMSAK -> Icons.Rounded.WbTwilight
    PrayerName.GUNES -> Icons.Rounded.Brightness5
    PrayerName.OGLE -> Icons.Rounded.Brightness7
    PrayerName.IKINDI -> Icons.Rounded.LightMode
    PrayerName.AKSAM -> Icons.Rounded.DarkMode
    PrayerName.YATSI -> Icons.Rounded.Bedtime
}

fun prayerIconTint(name: PrayerName): Color = when (name) {
    PrayerName.IMSAK -> Color(0xFF6A8AB0)
    PrayerName.GUNES -> Color(0xFFE0A040)
    PrayerName.OGLE -> Color(0xFFD4B830)
    PrayerName.IKINDI -> Color(0xFFD08050)
    PrayerName.AKSAM -> Color(0xFFB05060)
    PrayerName.YATSI -> Color(0xFF7060A0)
}

@Composable
fun PrayerIcon(
    name: PrayerName,
    modifier: Modifier = Modifier,
    tint: Color = prayerIconTint(name)
) {
    Icon(
        imageVector = prayerIcon(name),
        contentDescription = name.turkishName,
        modifier = modifier,
        tint = tint
    )
}
