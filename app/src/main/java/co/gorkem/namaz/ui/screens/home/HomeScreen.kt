package co.gorkem.namaz.ui.screens.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.gorkem.namaz.R
import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.model.PrayerTime
import co.gorkem.namaz.domain.usecase.KerahatInfo
import co.gorkem.namaz.domain.usecase.PrayerDashboardData
import co.gorkem.namaz.domain.usecase.PrayerPeriod
import co.gorkem.namaz.ui.components.PrayerTimeCard
import co.gorkem.namaz.ui.theme.HeroCardShape
import co.gorkem.namaz.ui.theme.prayerHeroGradient
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onLocationClick: () -> Unit
) {
    val prayerTimes by viewModel.prayerTimes.collectAsState()
    val dashboard by viewModel.dashboardData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val locationInfo by viewModel.locationInfo.collectAsState()
    val needsLocationPermission by viewModel.needsLocationPermission.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (isGranted) {
            viewModel.onLocationPermissionGranted()
        } else {
            viewModel.onLocationPermissionDenied()
        }
    }

    if (needsLocationPermission) {
        LocationPermissionScreen(
            onGrantPermission = {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            onSkip = { viewModel.onLocationPermissionDenied() }
        )
        return
    }

    val locationDisplay = buildString {
        if (locationInfo.cityName.isNotEmpty()) append(locationInfo.cityName)
        if (locationInfo.districtName.isNotEmpty()) append(" - ").append(locationInfo.districtName)
    }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = "Namaz Vakitleri",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.9f)
            ),
            actions = {
                IconButton(onClick = { viewModel.useMyLocation() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_my_location),
                        contentDescription = "Konumumu Kullan",
                        modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = error ?: "Bilinmeyen hata",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> {
                val todayKey = LocalDate.now().format(DATE_FORMAT)
                val todayTimes = prayerTimes.find { it.date == todayKey }
                    ?: prayerTimes.firstOrNull()
                val dash = dashboard

                if (dash != null) {
                    DashboardContent(
                        dashboard = dash,
                        locationDisplay = locationDisplay,
                        onLocationClick = onLocationClick,
                        todayTimes = todayTimes
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (todayTimes != null) {
                            val prayers = todayTimes.getAllPrayers()
                            items(prayers, key = { it.first.name }) { (name, time) ->
                                PrayerTimeCard(name = name, time = time)
                            }
                        }
                    }
                }
            }
        }
    }
}

private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy")

@Composable
fun LocationPermissionScreen(
    onGrantPermission: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Konum İzni",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Namaz vakitlerini bulunduğunuz konuma göre doğru hesaplayabilmek için konum iznine ihtiyacımız var.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onGrantPermission) {
            Text("Konum İzni Ver")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onSkip) {
            Text("Varsayılan Konumla Devam Et")
        }
    }
}

@Composable
private fun DashboardContent(
    dashboard: PrayerDashboardData,
    locationDisplay: String,
    onLocationClick: () -> Unit,
    todayTimes: PrayerTime?
) {
    val gradient = prayerHeroGradient(dashboard.currentPeriod.prayerName)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 8.dp, bottom = 24.dp)
    ) {
        item {
            ModernHeroCard(
                currentPeriod = dashboard.currentPeriod,
                locationDisplay = locationDisplay,
                onLocationClick = onLocationClick,
                gradient = gradient
            )
        }

        item {
            DashboardInfoRow(
                dashboard = dashboard
            )
        }

        item {
            Text(
                text = "Günlük Vakitler",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )
        }

        if (todayTimes != null) {
            val prayers = todayTimes.getAllPrayers()
            items(prayers, key = { it.first.name }) { (name, time) ->
                PrayerTimeCard(
                    name = name,
                    time = time,
                    isNext = name == dashboard.nextPrayer.prayerName
                )
            }
        }
    }
}

@Composable
private fun ModernHeroCard(
    currentPeriod: PrayerPeriod,
    locationDisplay: String,
    onLocationClick: () -> Unit,
    gradient: Brush
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(HeroCardShape)
            .background(gradient)
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Location
            HeroLocationChip(
                locationName = locationDisplay,
                onClick = onLocationClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Current period label
            Text(
                text = currentPeriod.label,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "vaktindesiniz",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentPeriod.startTime.format(TIME_FORMAT),
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Text(
                    text = "  →  ",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White.copy(alpha = 0.4f)
                )
                Text(
                    text = currentPeriod.endTime.format(TIME_FORMAT),
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun DashboardInfoRow(dashboard: PrayerDashboardData) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardCard(
                title = "Vaktin Çıkışı",
                value = formatDuration(dashboard.remainingToEndOfPeriodMinutes),
                subtitle = "${dashboard.nextPrayer.prayerName.turkishName} ezanına kalan",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            DashboardCard(
                title = "Sıradaki Vakit",
                value = dashboard.nextPrayer.prayerTime,
                subtitle = dashboard.nextPrayer.prayerName.turkishName,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val kerahat = dashboard.kerahatInfo
            val nextKerahat = dashboard.nextKerahatInfo

            if (kerahat != null) {
                DashboardCard(
                    title = "Kerahat Vakti",
                    value = "Aktif",
                    subtitle = "${kerahat.label} · ${kerahat.remainingToEndMinutes}dk sonra biter",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f)
                )
            } else if (nextKerahat != null) {
                DashboardCard(
                    title = "Kerahat",
                    value = formatDuration(nextKerahat.remainingToStartMinutes),
                    subtitle = "${nextKerahat.label} başlamasına",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            } else {
                DashboardCard(
                    title = "Kerahat",
                    value = "—",
                    subtitle = "Yakın kerahat yok",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.weight(1f)
                )
            }

            DashboardCard(
                title = "Vakit Bitişi",
                value = formatDuration(dashboard.remainingToEndOfPeriodMinutes),
                subtitle = "${dashboard.currentPeriod.prayerName.turkishName} çıkmasına",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun HeroLocationChip(
    locationName: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(Color.Black.copy(alpha = 0.25f))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_my_location),
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = Color.White.copy(alpha = 0.85f)
        )
        Text(
            text = locationName,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

@Composable
private fun DashboardCard(
    title: String,
    value: String,
    subtitle: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = tint
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                maxLines = 1
            )
        }
    }
}

private fun formatDuration(totalMinutes: Long): String {
    if (totalMinutes <= 0) return "0dk"
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return when {
        hours > 0 -> "${hours}s ${minutes}dk"
        else -> "${minutes}dk"
    }
}

private val TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm")
