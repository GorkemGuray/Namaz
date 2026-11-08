package co.gorkem.namaz.data.local.room

import co.gorkem.namaz.domain.model.Zikir

fun ZikirEntity.toDomain(): Zikir = Zikir(
    id = id,
    title = title,
    targetCount = targetCount,
    currentCount = currentCount,
    isNamazTesbihati = isNamazTesbihati,
    currentPhase = currentPhase
)

fun Zikir.toEntity(): ZikirEntity = ZikirEntity(
    id = id,
    title = title,
    targetCount = targetCount,
    currentCount = currentCount,
    isNamazTesbihati = isNamazTesbihati,
    currentPhase = currentPhase
)
