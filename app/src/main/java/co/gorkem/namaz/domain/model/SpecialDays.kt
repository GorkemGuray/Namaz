package co.gorkem.namaz.domain.model

import androidx.annotation.StringRes
import co.gorkem.namaz.R
import java.time.LocalDate

enum class SpecialDayType(
    @StringRes val titleRes: Int,
    @StringRes val messageRes: Int
) {
    FRIDAY(R.string.special_day_friday_title, R.string.special_day_friday_desc),
    REGAIB_KANDILI(R.string.special_day_regaib_title, R.string.special_day_regaib_desc),
    MIRAC_KANDILI(R.string.special_day_mirac_title, R.string.special_day_mirac_desc),
    BERAT_KANDILI(R.string.special_day_berat_title, R.string.special_day_berat_desc),
    KADIR_GECESI(R.string.special_day_kadir_title, R.string.special_day_kadir_desc),
    MEVLID_KANDILI(R.string.special_day_mevlid_title, R.string.special_day_mevlid_desc)
}

data class SpecialDay(
    val type: SpecialDayType,
    val isFriday: Boolean,
    val isKandil: Boolean,
    @StringRes val titleRes: Int = type.titleRes,
    @StringRes val messageRes: Int = type.messageRes
)

object SpecialDays {
    val ALL_KANDIL_TYPES: Set<SpecialDayType> = setOf(
        SpecialDayType.REGAIB_KANDILI,
        SpecialDayType.MIRAC_KANDILI,
        SpecialDayType.BERAT_KANDILI,
        SpecialDayType.KADIR_GECESI,
        SpecialDayType.MEVLID_KANDILI
    )

    val KANDIL_DAYS: Map<LocalDate, SpecialDayType> = mapOf(
        // 2026 (Diyanet İşleri Başkanlığı Resmi Dini Günler Takvimi)
        LocalDate.of(2026, 1, 15) to SpecialDayType.MIRAC_KANDILI,
        LocalDate.of(2026, 2, 2) to SpecialDayType.BERAT_KANDILI,
        LocalDate.of(2026, 3, 16) to SpecialDayType.KADIR_GECESI,
        LocalDate.of(2026, 8, 24) to SpecialDayType.MEVLID_KANDILI,
        LocalDate.of(2026, 12, 10) to SpecialDayType.REGAIB_KANDILI,

        // 2027 (Diyanet İşleri Başkanlığı Resmi Dini Günler Takvimi)
        LocalDate.of(2027, 1, 4) to SpecialDayType.MIRAC_KANDILI,
        LocalDate.of(2027, 1, 22) to SpecialDayType.BERAT_KANDILI,
        LocalDate.of(2027, 3, 5) to SpecialDayType.KADIR_GECESI,
        LocalDate.of(2027, 8, 13) to SpecialDayType.MEVLID_KANDILI,
        LocalDate.of(2027, 12, 2) to SpecialDayType.REGAIB_KANDILI,
        LocalDate.of(2027, 12, 24) to SpecialDayType.MIRAC_KANDILI,

        // 2028 (Diyanet İşleri Başkanlığı Resmi Dini Günler Takvimi)
        LocalDate.of(2028, 1, 11) to SpecialDayType.BERAT_KANDILI,
        LocalDate.of(2028, 2, 22) to SpecialDayType.KADIR_GECESI,
        LocalDate.of(2028, 8, 2) to SpecialDayType.MEVLID_KANDILI,
        LocalDate.of(2028, 11, 23) to SpecialDayType.REGAIB_KANDILI,
        LocalDate.of(2028, 12, 13) to SpecialDayType.MIRAC_KANDILI,
        LocalDate.of(2028, 12, 30) to SpecialDayType.BERAT_KANDILI
    )

    fun getKandilsForYear(year: Int): Map<LocalDate, SpecialDayType> {
        return KANDIL_DAYS.filterKeys { it.year == year }
    }
}
