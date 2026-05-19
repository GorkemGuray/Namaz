package co.gorkem.namaz.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.PI

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
            private val smoothAzimuth = FloatArray(1)
            private val smoothPitch = FloatArray(1)
            private val smoothRoll = FloatArray(1)
            private var hasGravity = false
            private var hasGeomagnetic = false

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
                        smoothAzimuth[0] = smoothAzimuth[0] * 0.8f + orientation[0] * 0.2f
                        smoothPitch[0] = smoothPitch[0] * 0.8f + orientation[1] * 0.2f
                        smoothRoll[0] = smoothRoll[0] * 0.8f + orientation[2] * 0.2f

                        var azimuthDeg = Math.toDegrees(smoothAzimuth[0].toDouble())
                        if (azimuthDeg < 0) azimuthDeg += 360.0

                        trySend(
                            QiblaOrientation.Available(
                                azimuth = azimuthDeg,
                                pitch = Math.toDegrees(smoothPitch[0].toDouble()),
                                roll = Math.toDegrees(smoothRoll[0].toDouble())
                            )
                        )
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
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
            val roll: Double
        ) : QiblaOrientation()

        data object MagnetometerUnavailable : QiblaOrientation()
    }
}
