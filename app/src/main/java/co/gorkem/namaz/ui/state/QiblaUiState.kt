package co.gorkem.namaz.ui.state

import co.gorkem.namaz.domain.model.QiblaDirection

sealed class QiblaUiState {
    data object Loading : QiblaUiState()
    data class CompassActive(
        val qiblaDirection: QiblaDirection,
        val userLocation: co.gorkem.namaz.domain.model.LocationInfo,
        val deviceAzimuth: Double,
        val pitch: Double,
        val roll: Double,
        val alignmentDelta: Double,
        val sensorAccuracy: Int,
        val magneticFieldStrength: Float,
        val isMagneticDistorted: Boolean,
        val needsCalibration: Boolean
    ) : QiblaUiState()
    data class StaticDirection(
        val qiblaDirection: QiblaDirection,
        val userLocation: co.gorkem.namaz.domain.model.LocationInfo
    ) : QiblaUiState()
    data class Error(val message: String) : QiblaUiState()
    data object LocationRequired : QiblaUiState()
}
