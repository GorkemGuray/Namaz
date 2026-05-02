package co.gorkem.namaz.ui.screens.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.model.PrayerTime
import co.gorkem.namaz.domain.usecase.GetNextPrayerUseCase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val prayerTimes by viewModel.prayerTimes.collectAsState()
    val nextPrayer by viewModel.nextPrayer.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val locationInfo by viewModel.locationInfo.collectAsState()
    val needsLocationPermission by viewModel.needsLocationPermission.collectAsState()

    val context = LocalContext.current
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
            onSkip = {
                viewModel.onLocationPermissionDenied()
            }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Namaz Vakitleri") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LocationInfoCard(locationInfo)

            Spacer(modifier = Modifier.height(12.dp))

            NextPrayerCard(nextPrayer)

            Spacer(modifier = Modifier.height(24.dp))

            when {
                isLoading -> CircularProgressIndicator()
                error != null -> Text(
                    text = error ?: "Bilinmeyen hata",
                    color = MaterialTheme.colorScheme.error
                )
                else -> {
                    val todayKey = LocalDate.now().format(
                        DateTimeFormatter.ofPattern("dd-MM-yyyy")
                    )
                    val todayTimes = prayerTimes.find { it.date == todayKey }

                    if (todayTimes != null) {
                        PrayerTimesList(todayTimes)
                    } else if (prayerTimes.isNotEmpty()) {
                        PrayerTimesList(prayerTimes.first())
                    }
                }
            }
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
fun LocationInfoCard(locationInfo: co.gorkem.namaz.domain.model.LocationInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = if (locationInfo.cityName.isNotEmpty())
                "${locationInfo.cityName}, ${locationInfo.districtName}"
            else
                "Konum alınamadı",
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun NextPrayerCard(nextPrayer: GetNextPrayerUseCase.NextPrayerInfo?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sıradaki Vakit",
                style = MaterialTheme.typography.labelLarge
            )
            if (nextPrayer != null) {
                Text(
                    text = nextPrayer.prayerName.turkishName,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = nextPrayer.prayerTime,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "${nextPrayer.remainingMinutes} dakika kaldı",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Text(
                    text = "Yükleniyor...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun PrayerTimesList(prayerTime: PrayerTime) {
    val prayers = prayerTime.getAllPrayers()

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(prayers) { (name, time) ->
            PrayerTimeItem(name = name, time = time)
        }
    }
}

@Composable
fun PrayerTimeItem(name: PrayerName, time: String) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = name.turkishName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = time,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}
