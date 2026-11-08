package co.gorkem.namaz.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class QiblaSensorManager @Inject constructor() {

    open fun hasMagnetometer(): Boolean = false

    open fun getOrientationEvents(): Flow<QiblaOrientation> = emptyFlow()

    sealed class QiblaOrientation {
        data class Available(
            val azimuth: Double,
            val pitch: Double,
            val roll: Double,
            val sensorAccuracy: Int,
            val magneticFieldStrength: Float,
            val isMagneticDistorted: Boolean,
            val needsCalibration: Boolean
        ) : QiblaOrientation()

        data object MagnetometerUnavailable : QiblaOrientation()
    }
}
