package co.gorkem.namaz.ui.screens.qibla

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.gorkem.namaz.R
import co.gorkem.namaz.domain.model.LocationInfo
import co.gorkem.namaz.domain.usecase.QiblaUseCase
import co.gorkem.namaz.service.LocationTracker
import co.gorkem.namaz.service.QiblaSensorManager
import co.gorkem.namaz.ui.state.QiblaUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class QiblaViewModel @Inject constructor(
    private val qiblaUseCase: QiblaUseCase,
    private val sensorManager: QiblaSensorManager,
    private val locationTracker: LocationTracker,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<QiblaUiState>(QiblaUiState.Loading)
    val uiState: StateFlow<QiblaUiState> = _uiState.asStateFlow()

    private var qiblaDirection: co.gorkem.namaz.domain.model.QiblaDirection? = null

    init {
        loadQibla()
    }

    private fun loadQibla() {
        viewModelScope.launch {
            _uiState.value = QiblaUiState.Loading

            val location = if (locationTracker.hasLocationPermission()) {
                locationTracker.getCurrentLocation()
            } else {
                null
            }

            if (location == null) {
                _uiState.value = QiblaUiState.LocationRequired
                return@launch
            }

            val info = LocationInfo(
                latitude = location.latitude,
                longitude = location.longitude,
                timezone = "UTC"
            )

            qiblaDirection = qiblaUseCase(info.latitude, info.longitude)

            if (sensorManager.hasMagnetometer()) {
                startCompassMode()
            } else {
                _uiState.value = QiblaUiState.StaticDirection(
                    qiblaDirection = qiblaDirection!!
                )
            }
        }
    }

    private fun startCompassMode() {
        sensorManager.getOrientationEvents()
            .catch { e ->
                _uiState.value = QiblaUiState.Error(
                    message = context.getString(R.string.qibla_error_prefix) + " ${e.localizedMessage ?: context.getString(R.string.home_unknown_error)}"
                )
            }
            .onEach { orientation ->
                when (orientation) {
                    is QiblaSensorManager.QiblaOrientation.MagnetometerUnavailable -> {
                        _uiState.value = QiblaUiState.StaticDirection(
                            qiblaDirection = qiblaDirection!!
                        )
                    }
                    is QiblaSensorManager.QiblaOrientation.Available -> {
                        val qibla = qiblaDirection ?: return@onEach
                        val delta = calculateAlignmentDelta(orientation.azimuth, qibla.bearing)
                        _uiState.value = QiblaUiState.CompassActive(
                            qiblaDirection = qibla,
                            deviceAzimuth = orientation.azimuth,
                            pitch = orientation.pitch,
                            roll = orientation.roll,
                            alignmentDelta = delta
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

    override fun onCleared() {
        super.onCleared()
    }
}
