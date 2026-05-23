package co.gorkem.namaz.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.gorkem.namaz.R
import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.model.PrayerTime
import co.gorkem.namaz.domain.usecase.GetNextPrayerUseCase
import co.gorkem.namaz.domain.usecase.PeriodType
import co.gorkem.namaz.ui.theme.ListItemShape

enum class PrayerRowState {
    Passed,
    Active,
    Upcoming,
    Normal
}

@Composable
fun PrayerTimesList(
    prayerTime: PrayerTime,
    nextPrayer: GetNextPrayerUseCase.NextPrayerInfo?,
    currentPrayerName: PrayerName?,
    currentPeriodType: PeriodType? = null,
    modifier: Modifier = Modifier
) {
    val prayers = prayerTime.getAllPrayers()
    val timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm")
    val now = java.time.LocalTime.now()

    val imsakTimeStr = prayers.find { it.first == PrayerName.IMSAK }?.second
    val imsakTime = imsakTimeStr?.let { java.time.LocalTime.parse(it, timeFormatter) }
    val isBeforeImsak = imsakTime != null && now.isBefore(imsakTime)

    val currentPrayerIndex = currentPrayerName?.let { name ->
        prayers.indexOfFirst { it.first == name }
    } ?: -1

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            prayers.forEachIndexed { index, (name, time) ->
                val state = when {
                    // Before IMSAK: no prayer has passed, IMSAK is upcoming
                    isBeforeImsak -> {
                        if (name == PrayerName.IMSAK) {
                            PrayerRowState.Upcoming
                        } else {
                            PrayerRowState.Normal
                        }
                    }
                    // If current period type is KUSLUK:
                    // Güneş and Imsak are passed. Öğle is upcoming. Nothing is active.
                    currentPeriodType == PeriodType.KUSLUK -> {
                        when (name) {
                            PrayerName.IMSAK, PrayerName.GUNES -> PrayerRowState.Passed
                            PrayerName.OGLE -> PrayerRowState.Upcoming
                            else -> PrayerRowState.Normal
                        }
                    }
                    // Current/Active prayer highlight
                    name == currentPrayerName -> {
                        PrayerRowState.Active
                    }
                    // Next upcoming prayer
                    name == nextPrayer?.prayerName -> {
                        // Safe check: If the next prayer is today's IMSAK but today's YATSI is active (night time),
                        // today's IMSAK has already passed, so it should be PASSED.
                        if (name == PrayerName.IMSAK && currentPrayerName == PrayerName.YATSI) {
                            PrayerRowState.Passed
                        } else {
                            PrayerRowState.Upcoming
                        }
                    }
                    // Prayers before current prayer index have passed
                    currentPrayerIndex != -1 && index < currentPrayerIndex -> {
                        PrayerRowState.Passed
                    }
                    // Default state
                    else -> {
                        PrayerRowState.Normal
                    }
                }

                PrayerListRow(
                    name = name,
                    time = time,
                    state = state,
                    modifier = Modifier.fillMaxWidth()
                )

                if (index < prayers.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun PrayerListRow(
    name: PrayerName,
    time: String,
    state: PrayerRowState,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (state == PrayerRowState.Upcoming) 1.02f else 1f,
        animationSpec = spring(dampingRatio = 0.8f),
        label = "row_scale"
    )

    val containerColor = when (state) {
        PrayerRowState.Upcoming -> MaterialTheme.colorScheme.secondaryContainer
        else -> Color.Transparent
    }

    val nameColor = when (state) {
        PrayerRowState.Active -> MaterialTheme.colorScheme.primary
        PrayerRowState.Upcoming -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    val timeColor = when (state) {
        PrayerRowState.Active -> MaterialTheme.colorScheme.primary
        PrayerRowState.Upcoming -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val iconTint = when (state) {
        PrayerRowState.Active -> MaterialTheme.colorScheme.primary
        PrayerRowState.Upcoming -> MaterialTheme.colorScheme.onSecondaryContainer
        PrayerRowState.Passed -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> prayerIconTint(name)
    }

    val rowAlpha = when (state) {
        PrayerRowState.Passed -> 0.45f
        else -> 1f
    }

    Row(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clip(ListItemShape)
            .background(containerColor)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .alpha(rowAlpha),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    when (state) {
                        PrayerRowState.Upcoming -> MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f)
                        PrayerRowState.Active -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            val iconRes = when (name) {
                PrayerName.IMSAK -> R.drawable.ic_prayer_imsak
                PrayerName.GUNES -> R.drawable.ic_prayer_gunes
                PrayerName.OGLE -> R.drawable.ic_prayer_ogle
                PrayerName.IKINDI -> R.drawable.ic_prayer_ikindi
                PrayerName.AKSAM -> R.drawable.ic_prayer_aksam
                PrayerName.YATSI -> R.drawable.ic_prayer_yatsi
            }
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = stringResource(name.nameRes),
                modifier = Modifier.size(18.dp),
                tint = iconTint
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = stringResource(name.nameRes),
            style = if (state == PrayerRowState.Upcoming) {
                MaterialTheme.typography.titleMedium.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
            } else {
                MaterialTheme.typography.titleMedium
            },
            color = nameColor,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = time,
            style = if (state == PrayerRowState.Upcoming) {
                MaterialTheme.typography.titleLarge.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    fontFeatureSettings = "tnum"
                )
            } else {
                MaterialTheme.typography.titleMedium.copy(
                    fontFeatureSettings = "tnum"
                )
            },
            color = timeColor
        )
    }
}
