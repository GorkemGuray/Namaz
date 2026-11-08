package co.gorkem.namaz.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.gorkem.namaz.R
import co.gorkem.namaz.domain.usecase.RamadanCounters
import co.gorkem.namaz.domain.usecase.RamadanTargetInfo
import kotlinx.coroutines.delay

@Composable
fun RamadanCountdownRow(
    ramadanCounters: RamadanCounters,
    onCountdownFinished: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RamadanCountdownItem(
            title = stringResource(R.string.ramadan_sahur),
            info = ramadanCounters.sahur,
            onCountdownFinished = onCountdownFinished,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .width(1.dp)
                .height(48.dp)
                .background(Color.White.copy(alpha = 0.2f))
        )

        RamadanCountdownItem(
            title = stringResource(R.string.ramadan_iftar),
            info = ramadanCounters.iftar,
            onCountdownFinished = onCountdownFinished,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RamadanCountdownItem(
    title: String,
    info: RamadanTargetInfo,
    onCountdownFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var remainingSeconds by remember { mutableLongStateOf(0L) }
    val currentOnFinished by rememberUpdatedState(onCountdownFinished)

    LaunchedEffect(info.targetTimeInMillis) {
        while (true) {
            val nowWall = System.currentTimeMillis()
            val remaining = ((info.targetTimeInMillis - nowWall) / 1000).coerceAtLeast(0)
            remainingSeconds = remaining
            if (nowWall >= info.targetTimeInMillis + 1200) {
                currentOnFinished()
                break
            }
            delay(1000L)
        }
    }

    val hours = remainingSeconds / 3600
    val minutes = (remainingSeconds % 3600) / 60
    val seconds = remainingSeconds % 60

    val displayText = when {
        hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
        minutes > 0 -> String.format("%02d:%02d", minutes, seconds)
        else -> String.format("%02ds", seconds)
    }

    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = info.prayerTime,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }

        Text(
            text = displayText,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFeatureSettings = "tnum",
                fontWeight = FontWeight.Bold
            ),
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = 100.dp)
        )

        val subLabel = when {
            hours > 0 -> stringResource(R.string.home_hours_left)
            minutes > 0 -> stringResource(R.string.home_minutes_left)
            else -> stringResource(R.string.home_seconds_left)
        }
        Text(
            text = subLabel,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}
