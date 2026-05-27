package co.gorkem.namaz.wear.data

data class QiblaDirection(
    val bearing: Double,
    val cardinalDirection: String
) {
    fun toCardinal(): String {
        return when (bearing) {
            in 337.5..360.0, in 0.0..22.5 -> "K"
            in 22.5..67.5 -> "KD"
            in 67.5..112.5 -> "D"
            in 112.5..157.5 -> "GD"
            in 157.5..202.5 -> "G"
            in 202.5..247.5 -> "GB"
            in 247.5..292.5 -> "B"
            in 292.5..337.5 -> "KB"
            else -> "?"
        }
    }
}
