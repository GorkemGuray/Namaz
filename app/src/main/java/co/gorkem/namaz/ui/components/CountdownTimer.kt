package co.gorkem.namaz.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import co.gorkem.namaz.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.gorkem.namaz.domain.usecase.GetNextPrayerUseCase
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun CountdownTimer(
    nextPrayer: GetNextPrayerUseCase.NextPrayerInfo,
    modifier: Modifier = Modifier
) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    var remainingSeconds by remember { mutableLongStateOf(0L) }

    LaunchedEffect(nextPrayer) {
        val now = LocalTime.now()
        val prayerTime = LocalTime.parse(nextPrayer.prayerTime, timeFormatter)
        val today = LocalDate.now()

        val targetDateTime = if (prayerTime.isAfter(now)) {
            LocalDateTime.of(today, prayerTime)
        } else {
            LocalDateTime.of(today.plusDays(1), prayerTime)
        }

        val targetMillis = targetDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        while (true) {
            val remaining = ((targetMillis - System.currentTimeMillis()) / 1000).coerceAtLeast(0)
            remainingSeconds = remaining
            delay(1000L)
        }
    }

    val hours = remainingSeconds / 3600
    val minutes = (remainingSeconds % 3600) / 60
    val seconds = remainingSeconds % 60

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = stringResource(R.string.home_next_prayer),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            AnimatedContent(
                targetState = nextPrayer.prayerName,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }
            ) { name ->
                Text(
                    text = stringResource(name.nameRes),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Text(
                text = nextPrayer.prayerTime,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFeatureSettings = "tnum"
                ),
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
            )
            Text(
                text = formatCountdown(hours, minutes, seconds),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFeatureSettings = "tnum"
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun formatCountdown(hours: Long, minutes: Long, seconds: Long): String {
    return when {
        hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
        minutes > 0 -> String.format("%02d:%02d", minutes, seconds)
        else -> String.format("%02ds", seconds)
    }
}