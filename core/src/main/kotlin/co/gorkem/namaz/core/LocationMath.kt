package co.gorkem.namaz.core

import kotlin.math.*

object LocationMath {
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    fun isDistanceSignificant(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
        thresholdMeters: Double = 10000.0
    ): Boolean {
        if ((lat1 == 0.0 && lon1 == 0.0) || (lat2 == 0.0 && lon2 == 0.0)) {
            return true
        }
        return calculateDistance(lat1, lon1, lat2, lon2) > thresholdMeters
    }
}
