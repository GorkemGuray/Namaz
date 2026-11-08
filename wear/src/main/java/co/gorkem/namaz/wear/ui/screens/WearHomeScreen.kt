package co.gorkem.namaz.wear.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnDefaults
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnItemScope
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.compose.ui.res.stringResource
import co.gorkem.namaz.wear.R
import co.gorkem.namaz.wear.data.WearKerahatInfo
import co.gorkem.namaz.wear.data.WearNextPrayer
import co.gorkem.namaz.wear.data.WearPrayerItem
import co.gorkem.namaz.wear.ui.WearViewModel
import co.gorkem.namaz.wear.prayerNameFromEnum
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.TouchApp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.wear.compose.material3.ListHeaderDefaults

@Composable
fun WearHomeScreen(
    onNavigateToQibla: () -> Unit,
    onNavigateToZikirmatik: () -> Unit,
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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
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
                        LoadingState(
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec)
                        )
                    }
                } else {
                    // Header: Location
                    if (cityName.isNotEmpty()) {
                        item {
                            CompactHeader(
                                cityName = cityName,
                                isPhoneSource = isPhoneSource,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .transformedHeight(this, transformationSpec)
                                    .minimumVerticalContentPadding(ListHeaderDefaults.minimumTopListContentPadding)
                            )
                        }
                    }

                    // Kerahat Alert
                    if (kerahat != null) {
                        item {
                            WearKerahatBanner(
                                kerahat = kerahat!!,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .transformedHeight(this, transformationSpec)
                            )
                        }
                    }

                    // Prayer rows
                    val nextPrayerIndex = prayerItems.indexOfFirst { it.name == nextPrayer?.name }
                    val currentPrayerIndex = if (nextPrayerIndex > 0) nextPrayerIndex - 1 else if (nextPrayerIndex == 0) prayerItems.lastIndex else -1

                    if (nextPrayer != null) {
                        item {
                            NextPrayerHeader(
                                nextPrayerInfo = nextPrayer!!,
                                onCountdownFinished = { viewModel.refreshNextPrayer() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .transformedHeight(this, transformationSpec)
                            )
                        }
                    }

                    if (prayerItems.isNotEmpty()) {
                        val chunked = prayerItems.chunked(2)
                        items(chunked.size) { rowIndex ->
                            val rowItems = chunked[rowIndex]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .transformedHeight(this, transformationSpec)
                                    .padding(horizontal = 16.dp, vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                rowItems.forEachIndexed { colIndex, prayer ->
                                    val index = rowIndex * 2 + colIndex
                                    val isNext = index == nextPrayerIndex
                                    val isCurrent = index == currentPrayerIndex
                                    val isPassed = currentPrayerIndex != -1 && index < currentPrayerIndex && nextPrayerIndex != 0

                                    WearPrayerGridCell(
                                        prayer = prayer,
                                        isNext = isNext,
                                        isCurrent = isCurrent,
                                        isPassed = isPassed,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = onNavigateToQibla,
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec)
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Explore,
                                    contentDescription = "Kıble Bulucu",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    text = "Kıble Bulucu",
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = onNavigateToZikirmatik,
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec)
                                .minimumVerticalContentPadding(ButtonDefaults.minimumVerticalListContentPadding)
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TouchApp,
                                    contentDescription = "Zikirmatik",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    text = "Zikirmatik",
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
private fun TransformingLazyColumnItemScope.WearKerahatBanner(
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

    Box(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(containerColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
                textAlign = TextAlign.Center
            )
            Text(
                text = "-${kerahat.remainingMinutes}dk",
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TransformingLazyColumnItemScope.LoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 2.dp
        )
        Text(
            text = stringResource(R.string.wear_loading),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TransformingLazyColumnItemScope.CompactHeader(
    cityName: String,
    isPhoneSource: Boolean,
    modifier: Modifier = Modifier
) {
    val sourceIconColor = if (isPhoneSource) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
    Row(
        modifier = modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(sourceIconColor)
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = cityName,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun WearCountdownHero(
    nextPrayer: WearNextPrayer,
    modifier: Modifier = Modifier,
    onCountdownFinished: () -> Unit = {}
) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    var remaining by remember { mutableLongStateOf(0L) }
    val currentOnFinished by rememberUpdatedState(onCountdownFinished)

    val targetMillis = remember(nextPrayer) {
        if (nextPrayer.targetTimeMillis > 0L) {
            nextPrayer.targetTimeMillis
        } else {
            val now = LocalTime.now()
            val prayerTime = LocalTime.parse(nextPrayer.time, timeFormatter)
            val today = LocalDate.now()
            val targetDateTime = if (prayerTime.isAfter(now)) {
                LocalDateTime.of(today, prayerTime)
            } else {
                LocalDateTime.of(today.plusDays(1), prayerTime)
            }
            targetDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
    }

    LaunchedEffect(targetMillis) {
        while (isActive) {
            val nowWall = System.currentTimeMillis()
            val remainingSecs = ((targetMillis - nowWall) / 1000).coerceAtLeast(0)
            remaining = remainingSecs
            if (nowWall >= targetMillis + 1200) {
                currentOnFinished()
                break
            }
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
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

@Composable
private fun CompactCountdown(
    nextPrayer: WearNextPrayer,
    modifier: Modifier = Modifier,
    onCountdownFinished: () -> Unit = {}
) {
    WearCountdownHero(
        nextPrayer = nextPrayer,
        modifier = modifier,
        onCountdownFinished = onCountdownFinished
    )
}

@Composable
private fun TransformingLazyColumnItemScope.NextPrayerHeader(
    nextPrayerInfo: WearNextPrayer,
    modifier: Modifier = Modifier,
    onCountdownFinished: () -> Unit = {}
) {
    Row(
        modifier = modifier.padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = prayerNameFromEnum(nextPrayerInfo.name) + " ",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        CompactCountdown(
            nextPrayer = nextPrayerInfo,
            onCountdownFinished = onCountdownFinished
        )
    }
}



@Composable
private fun WearPrayerGridCell(
    prayer: WearPrayerItem,
    isNext: Boolean,
    isCurrent: Boolean,
    isPassed: Boolean,
    modifier: Modifier = Modifier
) {
    val containerColor = when {
        isCurrent -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        isNext -> MaterialTheme.colorScheme.surfaceContainer
        else -> MaterialTheme.colorScheme.surfaceContainerLow
    }

    val nameColor = when {
        isCurrent -> MaterialTheme.colorScheme.primary
        isNext -> MaterialTheme.colorScheme.onSurface
        isPassed -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val timeColor = when {
        isCurrent -> MaterialTheme.colorScheme.primary
        isNext -> MaterialTheme.colorScheme.onSurface
        isPassed -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(containerColor)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = prayerNameFromEnum(prayer.name),
            style = MaterialTheme.typography.labelSmall,
            color = nameColor
        )
        Text(
            text = prayer.time,
            style = MaterialTheme.typography.titleMedium,
            color = timeColor
        )
    }
}
