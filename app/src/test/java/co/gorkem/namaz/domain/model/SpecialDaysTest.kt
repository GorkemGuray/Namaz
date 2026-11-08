package co.gorkem.namaz.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class SpecialDaysTest {

    @Test
    fun guardTest_currentYearAndNextYear_allFiveKandilsArePresent() {
        val currentYear = LocalDate.now().year
        val targetYears = listOf(currentYear, currentYear + 1)

        for (year in targetYears) {
            val kandilsInYear = SpecialDays.getKandilsForYear(year).values.toSet()
            val missing = SpecialDays.ALL_KANDIL_TYPES - kandilsInYear
            assertTrue(
                "Guard failure: Year $year does not have all 5 kandil types resolved! Missing: $missing. " +
                    "Update SpecialDays.KANDIL_DAYS with official Diyanet calendar.",
                missing.isEmpty()
            )
        }
    }

    @Test
    fun knownYears_2026_hasAllFiveKandilTypes() {
        val kandils = SpecialDays.getKandilsForYear(2026).values.toSet()
        assertEquals(SpecialDays.ALL_KANDIL_TYPES, kandils)
    }

    @Test
    fun knownYears_2027_hasAllFiveKandilTypes() {
        val kandils = SpecialDays.getKandilsForYear(2027).values.toSet()
        assertEquals(SpecialDays.ALL_KANDIL_TYPES, kandils)
    }

    @Test
    fun knownYears_2028_hasAllFiveKandilTypes() {
        val kandils = SpecialDays.getKandilsForYear(2028).values.toSet()
        assertEquals(SpecialDays.ALL_KANDIL_TYPES, kandils)
    }

    @Test
    fun guardFailsWhenYearIsMissing() {
        // Simulates what happens in future years when the table is exhausted
        val missingYear = 2099
        val kandilsInYear = SpecialDays.getKandilsForYear(missingYear).values.toSet()
        val missing = SpecialDays.ALL_KANDIL_TYPES - kandilsInYear
        assertFalse(missing.isEmpty())
        assertEquals(SpecialDays.ALL_KANDIL_TYPES, missing)
    }
}
