package co.gorkem.namaz.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.gorkem.namaz.domain.model.QadaPrayer

@Entity(tableName = "qada_prayers")
data class QadaPrayerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val prayerName: String,
    val dateMillis: Long,
    val isDone: Boolean
)

fun QadaPrayerEntity.toDomain(): QadaPrayer = QadaPrayer(
    id = id,
    prayerName = prayerName,
    dateMillis = dateMillis,
    isDone = isDone
)

fun QadaPrayer.toEntity(): QadaPrayerEntity = QadaPrayerEntity(
    id = id,
    prayerName = prayerName,
    dateMillis = dateMillis,
    isDone = isDone
)
