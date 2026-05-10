package co.gorkem.namaz.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import co.gorkem.namaz.domain.usecase.GetNextPrayerUseCase
import kotlinx.coroutines.delay

@Composable
fun CountdownTimer(
    nextPrayer: GetNextPrayerUseCase.NextPrayerInfo,
    modifier: Modifier = Modifier
) {
    var remainingSeconds by remember {
        mutableLongStateOf(nextPrayer.remainingMinutes * 60)
    }

    LaunchedEffect(nextPrayer) {
        remainingSeconds = nextPrayer.remainingMinutes * 60
        while (remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
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
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Sıradaki Vakit",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            AnimatedContent(
                targetState = nextPrayer.prayerName.turkishName,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }
            ) { name ->
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Text(
                text = nextPrayer.prayerTime,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFeatureSettings = "tnum"
                ),
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
            )
            AnimatedContent(
                targetState = remainingSeconds,
                transitionSpec = {
                    (fadeIn() togetherWith fadeOut())
                        .using(SizeTransform(clip = false))
                }
            ) { _ ->
                Text(
                    text = formatCountdown(hours, minutes, seconds),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFeatureSettings = "tnum"
                ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
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
