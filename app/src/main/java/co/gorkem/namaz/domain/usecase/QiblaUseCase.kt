package co.gorkem.namaz.domain.usecase

import co.gorkem.namaz.domain.model.QiblaDirection
import javax.inject.Inject
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class QiblaUseCase @Inject constructor() {

    private val kaabaLat = 21.4225
    private val kaabaLon = 39.8262

    operator fun invoke(latitude: Double, longitude: Double): QiblaDirection {
        val lat1 = latitude.toRadians()
        val lon1 = longitude.toRadians()
        val lat2 = kaabaLat.toRadians()
        val lon2 = kaabaLon.toRadians()

        val deltaLon = lon2 - lon1

        val y = sin(deltaLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(deltaLon)

        var bearing = atan2(y, x).toDegrees()
        if (bearing < 0) bearing += 360.0

        val direction = QiblaDirection(
            bearing = bearing,
            cardinalDirection = ""
        )

        return direction.copy(cardinalDirection = direction.toCardinal())
    }

    private fun Double.toRadians() = this * PI / 180.0
    private fun Double.toDegrees() = this * 180.0 / PI
}
