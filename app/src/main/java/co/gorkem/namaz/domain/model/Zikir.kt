package co.gorkem.namaz.domain.model

data class Zikir(
    val id: Int = 0,
    val title: String,
    val targetCount: Int,
    val currentCount: Int,
    val isNamazTesbihati: Boolean = false,
    val currentPhase: Int = 0
)
