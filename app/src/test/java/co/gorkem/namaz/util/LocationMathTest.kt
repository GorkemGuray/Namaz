package co.gorkem.namaz.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LocationMathTest {

    @Test
    fun calculateDistance_sameCoordinates_returnsZero() {
        val distance = LocationMath.calculateDistance(41.0082, 28.9784, 41.0082, 28.9784)
        assertEquals(0.0, distance, 0.001)
    }

    @Test
    fun calculateDistance_istanbulToKocaeli_returnsCorrectDistance() {
        // Istanbul (41.0082, 28.9784) to Kocaeli/Izmit (40.7654, 29.9408) is ~90km
        val distance = LocationMath.calculateDistance(41.0082, 28.9784, 40.7654, 29.9408)
        // Expected distance around 85,000 to 95,000 meters
        assertTrue(distance > 80000.0)
        assertTrue(distance < 95000.0)
    }

    @Test
    fun isDistanceSignificant_sameCoordinates_returnsFalse() {
        val significant = LocationMath.isDistanceSignificant(41.0082, 28.9784, 41.0082, 28.9784, 10000.0)
        assertFalse(significant)
    }

    @Test
    fun isDistanceSignificant_largeDistance_returnsTrue() {
        // Istanbul (41.0082, 28.9784) to Kocaeli (40.7654, 29.9408) is ~90km > 10km threshold
        val significant = LocationMath.isDistanceSignificant(41.0082, 28.9784, 40.7654, 29.9408, 10000.0)
        assertTrue(significant)
    }

    @Test
    fun isDistanceSignificant_shortDistance_returnsFalse() {
        // Istanbul Sultanahmet to Eminönü is about 1-2km (e.g. 41.0082, 28.9784 to 41.0182, 28.9784)
        val significant = LocationMath.isDistanceSignificant(41.0082, 28.9784, 41.0182, 28.9784, 10000.0)
        assertFalse(significant)
    }

    @Test
    fun isDistanceSignificant_uninitializedZeroCoordinates_returnsTrue() {
        // One location is (0.0, 0.0)
        assertTrue(LocationMath.isDistanceSignificant(0.0, 0.0, 41.0082, 28.9784, 10000.0))
        assertTrue(LocationMath.isDistanceSignificant(41.0082, 28.9784, 0.0, 0.0, 10000.0))
        assertTrue(LocationMath.isDistanceSignificant(0.0, 0.0, 0.0, 0.0, 10000.0))
    }
}
