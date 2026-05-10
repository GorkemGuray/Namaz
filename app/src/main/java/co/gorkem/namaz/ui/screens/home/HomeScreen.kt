package co.gorkem.namaz.ui.screens.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.gorkem.namaz.R
import co.gorkem.namaz.domain.usecase.PrayerDashboardData
import co.gorkem.namaz.ui.components.CountdownTimer
import co.gorkem.namaz.ui.components.PrayerHeroCard
import co.gorkem.namaz.ui.components.PrayerTimesList
import co.gorkem.namaz.ui.theme.PillShape
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
    val nextPrayer by viewModel.nextPrayer.collectAsState()

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
                        .padding(horizontal = 24.dp),
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

                if (dash != null && todayTimes != null) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            PrayerHeroCard(
                                dashboard = dash,
                                locationName = locationDisplay,
                                hijriDate = todayTimes.hijriDate,
                                onLocationClick = onLocationClick
                            )
                        }

                        item {
                            KerahatBanner(dashboard = dash)
                        }

                        item {
                            Text(
                                text = "Bugünün Vakitleri",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 2.dp, bottom = 0.dp)
                            )
                        }

                        item {
                            PrayerTimesList(
                                prayerTime = todayTimes,
                                nextPrayer = dash.nextPrayer,
                                currentPrayerName = dash.currentPeriod.prayerName
                            )
                        }
                    }
                } else if (todayTimes != null) {
                    val np = nextPrayer
                    if (np != null) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            CountdownTimer(nextPrayer = np)
                        }

                        item {
                            Text(
                                text = "Bugünün Vakitleri",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        item {
                            PrayerTimesList(
                                prayerTime = todayTimes,
                                nextPrayer = np,
                                currentPrayerName = null
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
private fun KerahatBanner(dashboard: PrayerDashboardData) {
    val kerahat = dashboard.kerahatInfo
    val nextKerahat = dashboard.nextKerahatInfo

    AnimatedVisibility(
        visible = kerahat != null || (nextKerahat != null && nextKerahat.remainingToStartMinutes <= 30),
        enter = slideInVertically { it / 2 } + fadeIn(),
        exit = slideOutVertically { it / 2 } + fadeOut()
    ) {
        when {
            kerahat != null -> {
                KerahatChip(
                    text = kerahat.label + " aktif",
                    subtext = kerahat.remainingToEndMinutes.toString() + " dk sonra biter",
                    isActive = true
                )
            }
            nextKerahat != null && nextKerahat.remainingToStartMinutes <= 30 -> {
                KerahatChip(
                    text = nextKerahat.label + " yaklaşıyor",
                    subtext = nextKerahat.remainingToStartMinutes.toString() + " dk sonra başlar",
                    isActive = false
                )
            }
        }
    }
}

@Composable
private fun KerahatChip(
    text: String,
    subtext: String,
    isActive: Boolean
) {
    val backgroundColor = if (isActive) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.tertiaryContainer
    }

    val contentColor = if (isActive) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        MaterialTheme.colorScheme.onTertiaryContainer
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(PillShape)
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.WarningAmber,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = contentColor
        )
        Column {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor
            )
            Text(
                text = subtext,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.8f)
            )
        }
    }
}

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
        androidx.compose.material3.Button(onClick = onGrantPermission) {
            Text("Konum İzni Ver")
        }
        Spacer(modifier = Modifier.height(12.dp))
        androidx.compose.material3.TextButton(onClick = onSkip) {
            Text("Varsayılan Konumla Devam Et")
        }
    }
}

private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy")
