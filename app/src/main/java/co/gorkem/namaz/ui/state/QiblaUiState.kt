package co.gorkem.namaz.ui.state

import co.gorkem.namaz.domain.model.QiblaDirection

sealed class QiblaUiState {
    data object Loading : QiblaUiState()
    data class CompassActive(
        val qiblaDirection: QiblaDirection,
        val deviceAzimuth: Double,
        val pitch: Double,
        val roll: Double,
        val alignmentDelta: Double
    ) : QiblaUiState()
    data class StaticDirection(
        val qiblaDirection: QiblaDirection
    ) : QiblaUiState()
    data class Error(val message: String) : QiblaUiState()
    data object LocationRequired : QiblaUiState()
}
