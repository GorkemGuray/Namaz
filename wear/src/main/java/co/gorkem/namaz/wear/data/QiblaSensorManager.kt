package co.gorkem.namaz.wear.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.PI
import kotlin.math.sqrt
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.atan2

@Singleton
class QiblaSensorManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    fun hasMagnetometer(): Boolean = magnetometer != null && accelerometer != null

    fun getOrientationEvents(): Flow<QiblaOrientation> = callbackFlow {
        if (!hasMagnetometer()) {
            trySend(QiblaOrientation.MagnetometerUnavailable)
            close()
            return@callbackFlow
        }

        val listener = object : SensorEventListener {
            private val gravity = FloatArray(3)
            private val geomagnetic = FloatArray(3)
            private val rotationMatrix = FloatArray(9)
            private val orientation = FloatArray(3)
            private var smoothSin = 0.0
            private var smoothCos = 0.0
            private var isFirstRun = true
            private val smoothPitch = FloatArray(1)
            private val smoothRoll = FloatArray(1)
            private var hasGravity = false
            private var hasGeomagnetic = false

            @Volatile
            private var accelAccuracy = SensorManager.SENSOR_STATUS_NO_CONTACT

            @Volatile
            private var magnetAccuracy = SensorManager.SENSOR_STATUS_NO_CONTACT

            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        gravity[0] = event.values[0]
                        gravity[1] = event.values[1]
                        gravity[2] = event.values[2]
                        hasGravity = true
                    }
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        geomagnetic[0] = event.values[0]
                        geomagnetic[1] = event.values[1]
                        geomagnetic[2] = event.values[2]
                        hasGeomagnetic = true
                    }
                }

                if (hasGravity && hasGeomagnetic) {
                    if (SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) {
                        SensorManager.getOrientation(rotationMatrix, orientation)
                        
                        val currentSin = sin(orientation[0].toDouble())
                        val currentCos = cos(orientation[0].toDouble())
                        
                        if (isFirstRun) {
                            smoothSin = currentSin
                            smoothCos = currentCos
                            isFirstRun = false
                        } else {
                            smoothSin = smoothSin * 0.90 + currentSin * 0.10
                            smoothCos = smoothCos * 0.90 + currentCos * 0.10
                        }

                        smoothPitch[0] = smoothPitch[0] * 0.90f + orientation[1] * 0.10f
                        smoothRoll[0] = smoothRoll[0] * 0.90f + orientation[2] * 0.10f

                        var azimuthDeg = Math.toDegrees(atan2(smoothSin, smoothCos))
                        if (azimuthDeg < 0) azimuthDeg += 360.0

                        val magnetStrength = sqrt(
                            geomagnetic[0] * geomagnetic[0] +
                            geomagnetic[1] * geomagnetic[1] +
                            geomagnetic[2] * geomagnetic[2]
                        )

                        val overallAccuracy = minOf(accelAccuracy, magnetAccuracy)
                        val needsCalibration = when (overallAccuracy) {
                            SensorManager.SENSOR_STATUS_UNRELIABLE,
                            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> true
                            else -> false
                        }

                        val isDistorted = magnetStrength < 20f || magnetStrength > 70f

                        trySend(
                            QiblaOrientation.Available(
                                azimuth = azimuthDeg,
                                pitch = Math.toDegrees(smoothPitch[0].toDouble()),
                                roll = Math.toDegrees(smoothRoll[0].toDouble()),
                                sensorAccuracy = overallAccuracy,
                                magneticFieldStrength = magnetStrength,
                                isMagneticDistorted = isDistorted,
                                needsCalibration = needsCalibration
                            )
                        )
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                when (sensor?.type) {
                    Sensor.TYPE_ACCELEROMETER -> accelAccuracy = accuracy
                    Sensor.TYPE_MAGNETIC_FIELD -> magnetAccuracy = accuracy
                }
            }
        }

        sensorManager.registerListener(
            listener,
            accelerometer,
            SensorManager.SENSOR_DELAY_UI
        )
        sensorManager.registerListener(
            listener,
            magnetometer,
            SensorManager.SENSOR_DELAY_UI
        )

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

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
