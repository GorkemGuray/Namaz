package co.gorkem.namaz.ui.screens.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import co.gorkem.namaz.R
import co.gorkem.namaz.domain.model.LocationInfo
import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.model.PrayerTime
import co.gorkem.namaz.ui.components.PrayerHeroCard
import co.gorkem.namaz.ui.components.PrayerTimeCard
import co.gorkem.namaz.ui.theme.prayerAmbientColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit,
    onNavigateToCitySelection: () -> Unit
) {
    val prayerTimes by viewModel.prayerTimes.collectAsState()
    val nextPrayer by viewModel.nextPrayer.collectAsState()
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

    // Prayer-aware ambient background tint
    val ambientColor = prayerAmbientColor(nextPrayer?.prayerName)
    val animatedBackground by animateColorAsState(
        targetValue = ambientColor,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow),
        label = "bg_tint"
    )

    val locationDisplay = buildString {
        if (locationInfo.cityName.isNotEmpty()) append(locationInfo.cityName)
        if (locationInfo.districtName.isNotEmpty()) append(" - ").append(locationInfo.districtName)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
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
                    TextButton(onClick = onNavigateToCitySelection) {
                        Text(
                            "Şehir",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    TextButton(onClick = onNavigateToSettings) {
                        Text(
                            "Ayarlar",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Section
            if (nextPrayer != null) {
                PrayerHeroCard(
                    nextPrayer = nextPrayer!!,
                    locationName = locationDisplay,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Content area
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(top = 32.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                error != null -> {
                    Text(
                        text = error ?: "Bilinmeyen hata",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 32.dp)
                    )
                }
                else -> {
                    val todayKey = LocalDate.now().format(DATE_FORMAT)
                    val todayTimes = prayerTimes.find { it.date == todayKey }

                    Text(
                        text = "Günlük Vakitler",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )

                    if (todayTimes != null) {
                        PrayerTimesList(
                            prayerTime = todayTimes,
                            nextPrayerName = nextPrayer?.prayerName
                        )
                    } else if (prayerTimes.isNotEmpty()) {
                        PrayerTimesList(
                            prayerTime = prayerTimes.first(),
                            nextPrayerName = nextPrayer?.prayerName
                        )
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
fun PrayerTimesList(
    prayerTime: PrayerTime,
    nextPrayerName: PrayerName?
) {
    val prayers = prayerTime.getAllPrayers()

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(prayers) { (name, time) ->
            PrayerTimeCard(
                name = name,
                time = time,
                isNext = name == nextPrayerName
            )
        }
    }
}
