package co.gorkem.namaz.domain.calculator

import co.gorkem.namaz.domain.calculator.SolarCalculations.hourAngle
import co.gorkem.namaz.domain.calculator.SolarCalculations.julianDay
import co.gorkem.namaz.domain.calculator.SolarCalculations.solarNoonUtc
import co.gorkem.namaz.domain.calculator.SolarCalculations.calculatedTimeToLocalTime
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.tan
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerTimeCalculator @Inject constructor() {

    fun calculate(
        date: LocalDate,
        latitude: Double,
        longitude: Double,
        timezone: ZoneId,
        method: CalculationMethod = CalculationMethod.DIYANET
    ): CalculatedPrayerTimes {
        val jd = julianDay(date.year, date.monthValue, date.dayOfMonth)
        val (dec, _) = SolarCalculations.sunPosition(jd)
        val latRad = Math.toRadians(latitude)
        val noonUtc = solarNoonUtc(jd, longitude)

        val sunriseAngleRad = Math.toRadians(-0.833)
        val sunriseHa = hourAngle(latRad, dec, sunriseAngleRad)
        val sunriseUtc = if (sunriseHa != null) noonUtc - sunriseHa else noonUtc - 12.0
        val sunriseLocal = calculatedTimeToLocalTime(sunriseUtc, timezone, date)

        val gunesLocal = adjustMinutes(sunriseLocal, method.sunriseOffsetMinutes)

        val fajrAngleRad = Math.toRadians(method.fajrAngle)
        val fajrHa = hourAngle(latRad, dec, fajrAngleRad)
        val fajrUtc = if (fajrHa != null) noonUtc - fajrHa else noonUtc - 12.0
        val fajrLocal = calculatedTimeToLocalTime(fajrUtc, timezone, date)
        val imsakLocal = adjustMinutes(fajrLocal, method.imsakOffsetMinutes)

        val noonLocal = calculatedTimeToLocalTime(noonUtc, timezone, date)
        val ogleLocal = adjustMinutes(noonLocal, method.dhuhrOffsetMinutes)

        val asrAltitude = atan2(1.0, method.asrShadowFactor + tan(abs(latRad - dec)))
        val asrHa = hourAngle(latRad, dec, asrAltitude)
        val asrUtc = if (asrHa != null) noonUtc + asrHa else noonUtc + 12.0
        val asrLocal = calculatedTimeToLocalTime(asrUtc, timezone, date)
        val ikindiLocal = adjustMinutes(asrLocal, method.asrOffsetMinutes)

        val sunsetHa = hourAngle(latRad, dec, sunriseAngleRad)
        val sunsetUtc = if (sunsetHa != null) noonUtc + sunsetHa else noonUtc + 12.0
        val sunsetLocal = calculatedTimeToLocalTime(sunsetUtc, timezone, date)
        val aksamLocal = adjustMinutes(sunsetLocal, method.maghribOffsetMinutes)

        val ishaAngleRad = Math.toRadians(method.ishaAngle)
        val ishaHa = hourAngle(latRad, dec, ishaAngleRad)
        val ishaUtc = if (ishaHa != null) noonUtc + ishaHa else noonUtc + 12.0
        val ishaLocal = calculatedTimeToLocalTime(ishaUtc, timezone, date)
        val yatsiLocal = ishaLocal

        return CalculatedPrayerTimes(
            imsak = imsakLocal,
            gunes = gunesLocal,
            ogle = ogleLocal,
            ikindi = ikindiLocal,
            aksam = aksamLocal,
            yatsi = yatsiLocal
        )
    }

    private fun adjustMinutes(totalMinutes: Int, offsetMinutes: Int): Int {
        var result = totalMinutes + offsetMinutes
        while (result < 0) result += 1440
        while (result >= 1440) result -= 1440
        return result
    }
}
