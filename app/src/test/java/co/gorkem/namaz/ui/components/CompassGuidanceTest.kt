package co.gorkem.namaz.ui.components

import android.hardware.SensorManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CompassGuidanceTest {

    @Test
    fun healthySensors_showNoGuidance() {
        val guidance = resolveCompassGuidance(
            needsCalibration = false,
            isMagneticDistorted = false,
            sensorAccuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH
        )
        assertEquals(CompassGuidance.NONE, guidance)
    }

    @Test
    fun needsCalibration_only_showsCalibration() {
        val guidance = resolveCompassGuidance(
            needsCalibration = true,
            isMagneticDistorted = false,
            sensorAccuracy = SensorManager.SENSOR_STATUS_ACCURACY_LOW
        )
        assertEquals(CompassGuidance.CALIBRATION, guidance)
    }

    @Test
    fun distortionOnly_showsInterference() {
        val guidance = resolveCompassGuidance(
            needsCalibration = false,
            isMagneticDistorted = true,
            sensorAccuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH
        )
        assertEquals(CompassGuidance.MAGNETIC_INTERFERENCE, guidance)
    }

    @Test
    fun bothConditions_prioritizesInterference() {
        val guidance = resolveCompassGuidance(
            needsCalibration = true,
            isMagneticDistorted = true,
            sensorAccuracy = SensorManager.SENSOR_STATUS_UNRELIABLE
        )
        assertEquals(CompassGuidance.MAGNETIC_INTERFERENCE, guidance)
    }

    @Test
    fun distortion_blocksCalibrationEvenWhenAccuracyRecovered() {
        val guidance = resolveCompassGuidance(
            needsCalibration = true,
            isMagneticDistorted = true,
            sensorAccuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH
        )
        assertEquals(CompassGuidance.MAGNETIC_INTERFERENCE, guidance)
    }

    @Test
    fun accuracyUnreliableWithoutFlags_showsCalibration() {
        val guidance = resolveCompassGuidance(
            needsCalibration = false,
            isMagneticDistorted = false,
            sensorAccuracy = SensorManager.SENSOR_STATUS_UNRELIABLE
        )
        assertEquals(CompassGuidance.CALIBRATION, guidance)
    }

    @Test
    fun accuracyLowWithoutFlags_showsCalibration() {
        val guidance = resolveCompassGuidance(
            needsCalibration = false,
            isMagneticDistorted = false,
            sensorAccuracy = SensorManager.SENSOR_STATUS_ACCURACY_LOW
        )
        assertEquals(CompassGuidance.CALIBRATION, guidance)
    }

    @Test
    fun accuracyMediumWithoutFlags_showsNoGuidance() {
        val guidance = resolveCompassGuidance(
            needsCalibration = false,
            isMagneticDistorted = false,
            sensorAccuracy = SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM
        )
        assertEquals(CompassGuidance.NONE, guidance)
    }

    @Test
    fun accuracyNoContactWithoutFlags_showsNoGuidance() {
        val guidance = resolveCompassGuidance(
            needsCalibration = false,
            isMagneticDistorted = false,
            sensorAccuracy = SensorManager.SENSOR_STATUS_NO_CONTACT
        )
        assertEquals(CompassGuidance.NONE, guidance)
    }

    @Test
    fun accuracyUncalibratedWithoutFlags_showsCalibration() {
        val guidance = resolveCompassGuidance(
            needsCalibration = false,
            isMagneticDistorted = false,
            sensorAccuracy = 1
        )
        assertEquals(CompassGuidance.CALIBRATION, guidance)
    }

    @Test
    fun interference_takesPrecedenceOverEveryAccuracyLevel() {
        for (accuracy in listOf(
            SensorManager.SENSOR_STATUS_NO_CONTACT,
            SensorManager.SENSOR_STATUS_UNRELIABLE,
            SensorManager.SENSOR_STATUS_ACCURACY_LOW,
            1,
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM,
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH
        )) {
            assertEquals(
                CompassGuidance.MAGNETIC_INTERFERENCE,
                resolveCompassGuidance(false, true, accuracy)
            )
        }
    }

    @Test
    fun everySensorStatus_resolvesWithoutThrowing() {
        for (accuracy in -1..6) {
            val guidance = resolveCompassGuidance(
                needsCalibration = true,
                isMagneticDistorted = false,
                sensorAccuracy = accuracy
            )
            assertTrue(guidance in CompassGuidance.values().toList())
        }
    }

    @Test
    fun unknownAccuracyCode_stillResolvesToAKnownGuidance() {
        val guidance = resolveCompassGuidance(
            needsCalibration = false,
            isMagneticDistorted = false,
            sensorAccuracy = 99
        )
        assertEquals(CompassGuidance.NONE, guidance)
    }

    @Test
    fun calibrationHintPointsToFigureEight() {
        assertTrue(
            resolveCompassGuidance(
                needsCalibration = true,
                isMagneticDistorted = false,
                sensorAccuracy = SensorManager.SENSOR_STATUS_ACCURACY_LOW
            ).showsFigureEight
        )
    }

    @Test
    fun interferenceHintPointsToMetal() {
        assertTrue(
            resolveCompassGuidance(
                needsCalibration = false,
                isMagneticDistorted = true,
                sensorAccuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH
            ).showsMetalHint
        )
    }

    @Test
    fun magneticInterference_isVisuallyDistinctFromCalibration() {
        val calibration = CompassGuidance.CALIBRATION
        val interference = CompassGuidance.MAGNETIC_INTERFERENCE
        assertTrue(calibration != interference)
        assertTrue(calibration.tone != interference.tone)
        assertFalse(calibration.showsMetalHint)
        assertFalse(interference.showsFigureEight)
    }

    @Test
    fun none_showsNeitherHint() {
        assertFalse(CompassGuidance.NONE.showsFigureEight)
        assertFalse(CompassGuidance.NONE.showsMetalHint)
    }
}
