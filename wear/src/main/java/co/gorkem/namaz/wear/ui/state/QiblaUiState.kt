package co.gorkem.namaz.wear.ui.state

import co.gorkem.namaz.wear.data.QiblaDirection

sealed class QiblaUiState {
    data object Loading : QiblaUiState()
    data class CompassActive(
        val qiblaDirection: QiblaDirection,
        val latitude: Double,
        val longitude: Double,
        val deviceAzimuth: Double,
        val pitch: Double,
        val roll: Double,
        val alignmentDelta: Double,
        val sensorAccuracy: Int,
        val magneticFieldStrength: Float,
        val isMagneticDistorted: Boolean,
        val needsCalibration: Boolean,
        val cityName: String = ""
    ) : QiblaUiState()
    data class StaticDirection(
        val qiblaDirection: QiblaDirection,
        val latitude: Double,
        val longitude: Double,
        val cityName: String = ""
    ) : QiblaUiState()
    data class Error(val message: String) : QiblaUiState()
    data object LocationRequired : QiblaUiState()
}
