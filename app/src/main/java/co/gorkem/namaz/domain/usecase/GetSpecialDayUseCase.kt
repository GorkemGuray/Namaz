package co.gorkem.namaz.domain.usecase

import co.gorkem.namaz.domain.model.SpecialDay
import co.gorkem.namaz.domain.model.SpecialDayType
import co.gorkem.namaz.domain.model.SpecialDays
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

class GetSpecialDayUseCase @Inject constructor() {

    operator fun invoke(date: LocalDate = LocalDate.now()): SpecialDay? {
        val kandilType = SpecialDays.KANDIL_DAYS[date]
        val isFriday = date.dayOfWeek == DayOfWeek.FRIDAY

        return when {
            kandilType != null -> SpecialDay(
                type = kandilType,
                isFriday = isFriday,
                isKandil = true
            )
            isFriday -> SpecialDay(
                type = SpecialDayType.FRIDAY,
                isFriday = true,
                isKandil = false
            )
            else -> null
        }
    }
}
