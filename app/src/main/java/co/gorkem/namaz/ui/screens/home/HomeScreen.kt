package co.gorkem.namaz.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.usecase.GetNextPrayerUseCase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val prayerTimes by viewModel.prayerTimes.collectAsState()
    val nextPrayer by viewModel.nextPrayer.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

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
            NextPrayerCard(nextPrayer)

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else if (error != null) {
                Text(
                    text = error ?: "Bilinmeyen hata",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                val todayKey = java.time.LocalDate.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")
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
fun PrayerTimesList(prayerTime: co.gorkem.namaz.domain.model.PrayerTime) {
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
