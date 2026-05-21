package co.gorkem.namaz.domain.calculator

import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

object SolarCalculations {

    fun julianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = (y / 100).toDouble()
        val b = 2 - a + (a / 4).toInt()
        return (365.25 * (y + 4716)).toInt() + (30.6001 * (m + 1)).toInt() + day + b - 1524.5
    }

    fun sunPosition(jd: Double): SunPosition {
        val d = jd - 2451545.0
        val g = Math.toRadians((357.529 + 0.98560028 * d) % 360.0)
        val q = Math.toRadians((280.459 + 0.98564736 * d) % 360.0)

        val l = q + Math.toRadians(1.915) * sin(g) + Math.toRadians(0.020) * sin(2.0 * g)
        val e = Math.toRadians(23.439 - 0.00000036 * d)

        val declination = asin(sin(e) * sin(l))

        val rightAscension = atan2(cos(e) * sin(l), cos(l))
        var eotDegrees = Math.toDegrees(q - rightAscension)
        eotDegrees = ((eotDegrees + 180) % 360) - 180
        val eotHours = eotDegrees * 4.0 / 60.0

        return SunPosition(
            declinationRad = declination,
            equationOfTimeHours = eotHours
        )
    }

    fun hourAngle(
        latitudeRad: Double,
        declinationRad: Double,
        targetAltitudeRad: Double
    ): Double? {
        val numerator = sin(targetAltitudeRad) - sin(latitudeRad) * sin(declinationRad)
        val denominator = cos(latitudeRad) * cos(declinationRad)
        val cosValue = numerator / denominator
        if (cosValue < -1.0 || cosValue > 1.0) return null
        return Math.toDegrees(acos(cosValue)) / 15.0
    }

    fun solarNoonUtc(jd: Double, longitudeDeg: Double): Double {
        val (_, eot) = sunPosition(jd)
        return 12.0 - longitudeDeg / 15.0 - eot
    }

    fun calculatedTimeToLocalTime(
        hoursUtc: Double,
        zoneId: ZoneId,
        date: LocalDate
    ): Int {
        val offsetSeconds = zoneId.rules.getOffset(
            java.time.LocalDateTime.of(date, java.time.LocalTime.NOON)
        ).totalSeconds
        val utcOffsetHours = offsetSeconds / 3600.0
        val localHours = hoursUtc + utcOffsetHours
        var hours = localHours.toInt()
        var minutes = ((localHours - hours) * 60.0).toInt()
        if (minutes < 0) { minutes += 60; hours -= 1 }
        while (hours < 0) hours += 24
        while (hours >= 24) hours -= 24
        return hours * 60 + minutes
    }

    data class SunPosition(
        val declinationRad: Double,
        val equationOfTimeHours: Double
    )
}
