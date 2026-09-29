package co.gorkem.namaz.wear.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.gorkem.namaz.wear.data.WearDataLayerService
import co.gorkem.namaz.wear.data.WearKerahatInfo
import co.gorkem.namaz.wear.data.WearNextPrayer
import co.gorkem.namaz.wear.data.WearPrayerItem
import co.gorkem.namaz.wear.data.WearPrayerRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

enum class LocationSource { GPS, CACHE, DEFAULT }

data class ResolvedLocation(
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val source: LocationSource
)

fun resolveStandaloneLocation(
    gpsLatitude: Double?,
    gpsLongitude: Double?,
    cachedLatitude: Double?,
    cachedLongitude: Double?,
    cachedCityName: String?
): ResolvedLocation {
    if (gpsLatitude != null && gpsLongitude != null) {
        return ResolvedLocation(gpsLatitude, gpsLongitude, "Konumunuz", LocationSource.GPS)
    }

    if (cachedLatitude != null && cachedLongitude != null &&
        cachedLatitude != 0.0 && cachedLongitude != 0.0
    ) {
        val city = cachedCityName?.trim()?.takeIf { it.isNotEmpty() } ?: "Konumunuz"
        return ResolvedLocation(cachedLatitude, cachedLongitude, city, LocationSource.CACHE)
    }

    return ResolvedLocation(
        WearPrayerRepository.DEFAULT_LATITUDE,
        WearPrayerRepository.DEFAULT_LONGITUDE,
        "Konumunuz",
        LocationSource.DEFAULT
    )
}

@HiltViewModel
class WearViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
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

    private val _themeId = MutableStateFlow(0)
    val themeId: StateFlow<Int> = _themeId.asStateFlow()

    init {
        dataLayerService.startListening()
        viewModelScope.launch {
            dataLayerService.prayerData.collect { data ->
                if (data != null) {
                    _prayerItems.value = data.prayers
                    _cityName.value = data.cityName
                    _nextPrayerInfo.value = data.nextPrayer
                    _kerahatInfo.value = data.kerahat
                    _themeId.value = data.themeId
                    _isPhoneSource.value = true
                    _isLoading.value = false
                }
            }
        }
        
        viewModelScope.launch {
            while (true) {
                if (_prayerItems.value.isNotEmpty()) {
                    _nextPrayerInfo.value = co.gorkem.namaz.wear.calculateNextPrayer(_prayerItems.value)
                }
                kotlinx.coroutines.delay(10_000L)
            }
        }
        viewModelScope.launch {
            // Wait 2 seconds to allow DataLayer to fetch existing items from phone
            kotlinx.coroutines.delay(2000L)
            loadFromStandalone()
        }
    }

    @SuppressLint("MissingPermission")
    fun loadFromStandalone() {
        viewModelScope.launch {
            if (_isPhoneSource.value) return@launch
            _isLoading.value = true

            var gpsLatitude: Double? = null
            var gpsLongitude: Double? = null

            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                val cancellationTokenSource = CancellationTokenSource()
                val location: android.location.Location? = kotlinx.coroutines.withTimeoutOrNull(5000L) {
                    suspendCancellableCoroutine { continuation ->
                        fusedLocationClient.getCurrentLocation(
                            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                            cancellationTokenSource.token
                        ).addOnSuccessListener { loc ->
                            continuation.resume(loc)
                        }.addOnFailureListener {
                            continuation.resume(null)
                        }
                    }
                }
                if (location != null) {
                    gpsLatitude = location.latitude
                    gpsLongitude = location.longitude
                } else {
                    cancellationTokenSource.cancel()
                }
            } catch (e: SecurityException) {
            } catch (e: Exception) {
            }

            val cached = dataLayerService.prayerData.value
            val resolved = resolveStandaloneLocation(
                gpsLatitude = gpsLatitude,
                gpsLongitude = gpsLongitude,
                cachedLatitude = cached?.latitude,
                cachedLongitude = cached?.longitude,
                cachedCityName = cached?.cityName
            )

            if (_isPhoneSource.value) return@launch

            prayerRepository.getPrayerTimesFromApi(
                latitude = resolved.latitude,
                longitude = resolved.longitude
            ).collect { result ->
                result.onSuccess { prayers ->
                    _prayerItems.value = prayers
                    _cityName.value = resolved.cityName
                    _isLoading.value = false
                    _nextPrayerInfo.value = co.gorkem.namaz.wear.calculateNextPrayer(prayers)
                }.onFailure {
                    _isLoading.value = false
                }
            }
        }
    }


}
