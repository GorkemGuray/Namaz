package co.gorkem.namaz.wear.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnDefaults
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.CardDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.compose.ui.res.stringResource
import co.gorkem.namaz.wear.R
import co.gorkem.namaz.wear.data.WearKerahatInfo
import co.gorkem.namaz.wear.data.WearNextPrayer
import co.gorkem.namaz.wear.data.WearPrayerItem
import co.gorkem.namaz.wear.ui.WearViewModel
import co.gorkem.namaz.wear.ui.prayerNameFromEnum
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun WearHomeScreen(
    viewModel: WearViewModel = hiltViewModel()
) {
    val prayerItems by viewModel.prayerItems.collectAsState()
    val cityName by viewModel.cityName.collectAsState()
    val nextPrayer by viewModel.nextPrayerInfo.collectAsState()
    val kerahat by viewModel.kerahatInfo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isPhoneSource by viewModel.isPhoneSource.collectAsState()
    val listState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()

    ScreenScaffold(
        scrollState = listState,
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) { contentPadding ->
        TransformingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = contentPadding,
            flingBehavior = TransformingLazyColumnDefaults.snapFlingBehavior(listState),
            rotaryScrollableBehavior = RotaryScrollableDefaults.snapBehavior(listState)
        ) {
            if (isLoading && prayerItems.isEmpty()) {
                item {
                    LoadingState()
                }
            } else {
                // Compact Header (City + Source)
                if (cityName.isNotEmpty()) {
                    item {
                        CompactHeader(cityName = cityName, isPhoneSource = isPhoneSource)
                    }
                }

                // Kerahat banner
                if (kerahat != null) {
                    item {
                        WearKerahatBanner(kerahat = kerahat!!)
                    }
                }

                // Prayer rows
                val nextPrayerIndex = prayerItems.indexOfFirst { it.name == nextPrayer?.name }
                val currentPrayerIndex = if (nextPrayerIndex > 0) nextPrayerIndex - 1 else if (nextPrayerIndex == 0) prayerItems.lastIndex else -1

                items(prayerItems.size) { index ->
                    val prayer = prayerItems[index]
                    val isNext = index == nextPrayerIndex
                    val isCurrent = index == currentPrayerIndex
                    val isPassed = currentPrayerIndex != -1 && index < currentPrayerIndex && nextPrayerIndex != 0

                    WearPrayerRow(
                        prayer = prayer,
                        isNext = isNext,
                        isCurrent = isCurrent,
                        isPassed = isPassed,
                        nextPrayerInfo = if (isNext) nextPrayer else null,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WearKerahatBanner(
    kerahat: WearKerahatInfo,
    modifier: Modifier = Modifier
) {
    val containerColor = if (kerahat.isCurrentlyKerahat) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.tertiaryContainer
    }
    val contentColor = if (kerahat.isCurrentlyKerahat) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        MaterialTheme.colorScheme.onTertiaryContainer
    }
    val label = if (kerahat.isCurrentlyKerahat) {
        stringResource(R.string.wear_kerahat_active)
    } else {
        stringResource(R.string.wear_kerahat_approaching)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
                textAlign = TextAlign.Center
            )
            Text(
                text = "${kerahat.remainingMinutes} ${stringResource(R.string.wear_kerahat_remaining)}",
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(36.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp
        )
        Text(
            text = stringResource(R.string.wear_loading),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun CompactHeader(
    cityName: String,
    isPhoneSource: Boolean,
    modifier: Modifier = Modifier
) {
    val sourceIconColor = if (isPhoneSource) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
    Row(
        modifier = modifier.fillMaxWidth().padding(bottom = 2.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(sourceIconColor)
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = cityName,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CompactCountdown(
    nextPrayer: WearNextPrayer,
    modifier: Modifier = Modifier
) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    var remaining by remember { mutableLongStateOf(0L) }

    LaunchedEffect(nextPrayer) {
        val now = LocalTime.now()
        val prayerTime = LocalTime.parse(nextPrayer.time, timeFormatter)
        val today = LocalDate.now()

        val targetDateTime = if (prayerTime.isAfter(now)) {
            LocalDateTime.of(today, prayerTime)
        } else {
            LocalDateTime.of(today.plusDays(1), prayerTime)
        }

        val targetMillis = targetDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        while (true) {
            val remainingSecs = ((targetMillis - System.currentTimeMillis()) / 1000).coerceAtLeast(0)
            remaining = remainingSecs
            delay(1000L)
        }
    }

    val hrs = remaining / 3600
    val mins = (remaining % 3600) / 60
    val secs = remaining % 60

    val countdownText = when {
        hrs > 0 -> String.format("-%02d:%02d:%02d", hrs, mins, secs)
        else -> String.format("-%02d:%02d", mins, secs)
    }

    Text(
        text = countdownText,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

@Composable
private fun WearPrayerRow(
    prayer: WearPrayerItem,
    isNext: Boolean,
    isCurrent: Boolean,
    isPassed: Boolean,
    nextPrayerInfo: WearNextPrayer?,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isNext) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.0f)
    }

    val nameColor = when {
        isCurrent -> MaterialTheme.colorScheme.primary
        isNext -> MaterialTheme.colorScheme.onPrimaryContainer
        isPassed -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.onSurface
    }

    val timeColor = when {
        isCurrent -> MaterialTheme.colorScheme.primary
        isNext -> MaterialTheme.colorScheme.onPrimaryContainer
        isPassed -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier
            .padding(vertical = 1.dp)
            .clip(MaterialTheme.shapes.small)
            .background(containerColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = prayerNameFromEnum(prayer.name),
            style = MaterialTheme.typography.labelMedium,
            color = nameColor
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (isNext && nextPrayerInfo != null) {
                CompactCountdown(nextPrayer = nextPrayerInfo)
            }
            Text(
                text = prayer.time,
                style = MaterialTheme.typography.labelMedium,
                color = timeColor
            )
        }
    }
}
