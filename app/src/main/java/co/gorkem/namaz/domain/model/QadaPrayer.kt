package co.gorkem.namaz.domain.model

data class QadaPrayer(
    val id: Long = 0,
    val prayerName: String,
    val dateMillis: Long = System.currentTimeMillis(),
    val isDone: Boolean = false
)
