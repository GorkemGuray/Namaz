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
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.CardDefaults
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
    val listState = rememberScalingLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                top = 32.dp,
                bottom = 16.dp
            )
        ) {
            if (isLoading && prayerItems.isEmpty()) {
                item {
                    LoadingState()
                }
            } else {
                // City chip
                if (cityName.isNotEmpty()) {
                    item {
                        CityChip(cityName = cityName)
                    }
                }

                // Next prayer countdown hero
                if (nextPrayer != null) {
                    item {
                        WearCountdownHero(nextPrayer = nextPrayer!!)
                    }
                }

                // Kerahat banner
                if (kerahat != null) {
                    item {
                        WearKerahatBanner(kerahat = kerahat!!)
                    }
                }

                // Source indicator
                item {
                    SourceIndicator(isPhoneSource = isPhoneSource)
                }

                // Prayer rows
                items(
                    items = prayerItems,
                    key = { it.name }
                ) { prayer ->
                    val isNext = nextPrayer?.name == prayer.name
                    WearPrayerRow(
                        prayer = prayer,
                        isNext = isNext
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
private fun CityChip(
    cityName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = cityName,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun WearCountdownHero(
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
        hrs > 0 -> String.format("%02d:%02d:%02d", hrs, mins, secs)
        mins > 0 -> String.format("%02d:%02d", mins, secs)
        else -> String.format("%02ds", secs)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = prayerNameFromEnum(nextPrayer.name),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )

            Text(
                text = nextPrayer.time,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )

            AnimatedContent(
                targetState = countdownText,
                transitionSpec = {
                    scaleIn(
                        initialScale = 0.9f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    ) + fadeIn() togetherWith
                    scaleOut(
                        targetScale = 1.1f,
                        animationSpec = spring()
                    ) + fadeOut()
                },
                label = "wear_countdown"
            ) { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun SourceIndicator(
    isPhoneSource: Boolean,
    modifier: Modifier = Modifier
) {
    val label = if (isPhoneSource) stringResource(R.string.wear_synced_from_phone) else stringResource(R.string.wear_standalone_mode)
    val color = if (isPhoneSource) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun WearPrayerRow(
    prayer: WearPrayerItem,
    isNext: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isNext) 1.03f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "wear_row_scale"
    )

    val containerColor = if (isNext) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val nameColor = if (isNext) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val timeColor = if (isNext) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .scale(scale),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = prayerNameFromEnum(prayer.name),
                style = if (isNext) {
                    MaterialTheme.typography.titleMedium
                } else {
                    MaterialTheme.typography.bodyMedium
                },
                color = nameColor
            )
            Text(
                text = prayer.time,
                style = if (isNext) {
                    MaterialTheme.typography.titleMedium
                } else {
                    MaterialTheme.typography.bodyMedium
                },
                color = timeColor
            )
        }
    }
}
