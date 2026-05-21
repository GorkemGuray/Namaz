package co.gorkem.namaz.domain.calculator

data class CalculationMethod(
    val fajrAngle: Double = -18.0,
    val ishaAngle: Double = -17.0,
    val asrShadowFactor: Double = 1.0,
    val sunriseOffsetMinutes: Int = -7,
    val dhuhrOffsetMinutes: Int = 5,
    val asrOffsetMinutes: Int = 4,
    val maghribOffsetMinutes: Int = 7,
    val imsakOffsetMinutes: Int = 0
) {
    companion object {
        val DIYANET = CalculationMethod()
    }
}

data class CalculatedPrayerTimes(
    val imsak: Int,
    val gunes: Int,
    val ogle: Int,
    val ikindi: Int,
    val aksam: Int,
    val yatsi: Int
)
