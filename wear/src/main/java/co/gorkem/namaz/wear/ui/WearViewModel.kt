package co.gorkem.namaz.wear.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.gorkem.namaz.wear.data.WearDataLayerService
import co.gorkem.namaz.wear.data.WearKerahatInfo
import co.gorkem.namaz.wear.data.WearNextPrayer
import co.gorkem.namaz.wear.data.WearPrayerItem
import co.gorkem.namaz.wear.data.WearPrayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WearViewModel @Inject constructor(
    private val dataLayerService: WearDataLayerService,
    private val prayerRepository: WearPrayerRepository
) : ViewModel() {

    private val _prayerItems = MutableStateFlow<List<WearPrayerItem>>(emptyList())
    val prayerItems: StateFlow<List<WearPrayerItem>> = _prayerItems.asStateFlow()

    private val _cityName = MutableStateFlow("")
    val cityName: StateFlow<String> = _cityName.asStateFlow()

    private val _nextPrayerInfo = MutableStateFlow<WearNextPrayer?>(null)
    val nextPrayerInfo = _nextPrayerInfo.asStateFlow()

    private val _kerahatInfo = MutableStateFlow<WearKerahatInfo?>(null)
    val kerahatInfo: StateFlow<WearKerahatInfo?> = _kerahatInfo.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isPhoneSource = MutableStateFlow(false)
    val isPhoneSource: StateFlow<Boolean> = _isPhoneSource.asStateFlow()

    init {
        dataLayerService.startListening()
        viewModelScope.launch {
            dataLayerService.prayerData.collect { data ->
                if (data != null) {
                    _prayerItems.value = data.prayers
                    _cityName.value = data.cityName
                    _nextPrayerInfo.value = data.nextPrayer
                    _kerahatInfo.value = data.kerahat
                    _isPhoneSource.value = true
                    _isLoading.value = false
                }
            }
        }
        loadFromStandalone()
    }

    fun loadFromStandalone() {
        viewModelScope.launch {
            if (_isPhoneSource.value) return@launch
            _isLoading.value = true
            prayerRepository.getPrayerTimesFromApi().collect { result ->
                result.onSuccess { prayers ->
                    _prayerItems.value = prayers
                    _cityName.value = "İstanbul"
                    _isLoading.value = false
                }.onFailure {
                    _isLoading.value = false
                }
            }
        }
    }
}
