package co.gorkem.namaz.wear.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.gorkem.namaz.wear.data.QiblaDirection
import co.gorkem.namaz.wear.data.QiblaSensorManager
import co.gorkem.namaz.wear.data.QiblaUseCase
import co.gorkem.namaz.wear.data.WearDataLayerService
import co.gorkem.namaz.wear.ui.state.QiblaUiState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.math.abs

@HiltViewModel
class WearQiblaViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val qiblaUseCase: QiblaUseCase,
    private val sensorManager: QiblaSensorManager,
    private val dataLayerService: WearDataLayerService
) : ViewModel() {

    private val _uiState = MutableStateFlow<QiblaUiState>(QiblaUiState.Loading)
    val uiState: StateFlow<QiblaUiState> = _uiState.asStateFlow()

    private var qiblaDirection: QiblaDirection? = null

    init {
        loadQibla()
    }

    @SuppressLint("MissingPermission")
    fun loadQibla() {
        viewModelScope.launch {
            _uiState.value = QiblaUiState.Loading

            // 1. DataLayer'dan (telefondan) koordinat almayı dene
            var lat = 0.0
            var lon = 0.0
            var cityName = ""
            val phoneData = dataLayerService.prayerData.value
            if (phoneData != null) {
                lat = phoneData.latitude
                lon = phoneData.longitude
                cityName = phoneData.cityName
            }

            // 2. Telefondan gelmediyse, saat üzerindeki GPS'i dene
            if (lat == 0.0 && lon == 0.0) {
                cityName = "Konumunuz"
                try {
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                    val cancellationTokenSource = CancellationTokenSource()
                    val location: android.location.Location? = withTimeoutOrNull(4000L) {
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
                        lat = location.latitude
                        lon = location.longitude
                    } else {
                        cancellationTokenSource.cancel()
                    }
                } catch (e: SecurityException) {
                    // İzin yok
                } catch (e: Exception) {
                    // Hata
                }
            }

            // 3. Hala lokasyon yoksa varsayılan İstanbul
            if (lat == 0.0 && lon == 0.0) {
                lat = 41.0082
                lon = 28.9784
                cityName = "İstanbul"
            }

            qiblaDirection = qiblaUseCase(lat, lon)

            if (sensorManager.hasMagnetometer()) {
                startCompassMode(lat, lon, cityName)
            } else {
                _uiState.value = QiblaUiState.StaticDirection(
                    qiblaDirection = qiblaDirection!!,
                    latitude = lat,
                    longitude = lon,
                    cityName = cityName
                )
            }
        }
    }

    private fun startCompassMode(latitude: Double, longitude: Double, cityName: String) {
        sensorManager.getOrientationEvents()
            .catch { e ->
                _uiState.value = QiblaUiState.Error(
                    message = e.localizedMessage ?: "Bilinmeyen bir hata oluştu"
                )
            }
            .onEach { orientation ->
                when (orientation) {
                    is QiblaSensorManager.QiblaOrientation.MagnetometerUnavailable -> {
                        _uiState.value = QiblaUiState.StaticDirection(
                            qiblaDirection = qiblaDirection!!,
                            latitude = latitude,
                            longitude = longitude,
                            cityName = cityName
                        )
                    }
                    is QiblaSensorManager.QiblaOrientation.Available -> {
                        val qibla = qiblaDirection ?: return@onEach
                        val delta = calculateAlignmentDelta(orientation.azimuth, qibla.bearing)
                        _uiState.value = QiblaUiState.CompassActive(
                            qiblaDirection = qibla,
                            latitude = latitude,
                            longitude = longitude,
                            deviceAzimuth = orientation.azimuth,
                            pitch = orientation.pitch,
                            roll = orientation.roll,
                            alignmentDelta = delta,
                            sensorAccuracy = orientation.sensorAccuracy,
                            magneticFieldStrength = orientation.magneticFieldStrength,
                            isMagneticDistorted = orientation.isMagneticDistorted,
                            needsCalibration = orientation.needsCalibration,
                            cityName = cityName
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun calculateAlignmentDelta(deviceAzimuth: Double, qiblaBearing: Double): Double {
        var delta = abs(deviceAzimuth - qiblaBearing)
        if (delta > 180.0) delta = 360.0 - delta
        return delta
    }

    fun retry() {
        loadQibla()
    }
}
