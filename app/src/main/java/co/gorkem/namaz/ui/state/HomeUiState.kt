package co.gorkem.namaz.ui.state

import co.gorkem.namaz.domain.model.LocationInfo
import co.gorkem.namaz.domain.model.PrayerTime
import co.gorkem.namaz.domain.usecase.GetNextPrayerUseCase

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(
        val prayerTimes: List<PrayerTime>,
        val todayPrayerTime: PrayerTime,
        val nextPrayer: GetNextPrayerUseCase.NextPrayerInfo,
        val locationInfo: LocationInfo,
        val remainingSeconds: Long
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
    data object PermissionRequired : HomeUiState()
}
