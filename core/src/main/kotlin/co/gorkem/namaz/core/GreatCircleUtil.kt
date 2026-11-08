package co.gorkem.namaz.core

import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

object GreatCircleUtil {
    fun getGreatCirclePath(
        startLat: Double,
        startLon: Double,
        endLat: Double,
        endLon: Double,
        numPoints: Int = 100
    ): List<Pair<Double, Double>> {
        val path = mutableListOf<Pair<Double, Double>>()
        
        val lat1 = startLat.toRadians()
        val lon1 = startLon.toRadians()
        val lat2 = endLat.toRadians()
        val lon2 = endLon.toRadians()

        val d = 2 * asin(
            kotlin.math.sqrt(
                sin((lat1 - lat2) / 2).pow(2.0) +
                        cos(lat1) * cos(lat2) * sin((lon1 - lon2) / 2).pow(2.0)
            )
        )

        if (d == 0.0) {
            return listOf(Pair(startLon, startLat), Pair(endLon, endLat))
        }

        for (i in 0..numPoints) {
            val f = i.toDouble() / numPoints
            val a = sin((1 - f) * d) / sin(d)
            val b = sin(f * d) / sin(d)
            val x = a * cos(lat1) * cos(lon1) + b * cos(lat2) * cos(lon2)
            val y = a * cos(lat1) * sin(lon1) + b * cos(lat2) * sin(lon2)
            val z = a * sin(lat1) + b * sin(lat2)
            
            val lat = atan2(z, kotlin.math.sqrt(x * x + y * y)).toDegrees()
            val lon = atan2(y, x).toDegrees()
            
            path.add(Pair(lon, lat))
        }

        return path
    }

    private fun Double.toRadians() = this * PI / 180.0
    private fun Double.toDegrees() = this * 180.0 / PI
}
