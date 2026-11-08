package co.gorkem.namaz.domain.usecase

import co.gorkem.namaz.domain.model.SpecialDayType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetSpecialDayUseCaseTest {

    private lateinit var useCase: GetSpecialDayUseCase

    @Before
    fun setUp() {
        useCase = GetSpecialDayUseCase()
    }

    @Test
    fun regularWeekday_returnsNull() {
        val tuesday = LocalDate.of(2026, 3, 10)
        val result = useCase(tuesday)
        assertNull(result)
    }

    @Test
    fun regularWeekend_returnsNull() {
        val sunday = LocalDate.of(2026, 3, 8)
        val result = useCase(sunday)
        assertNull(result)
    }

    @Test
    fun ordinaryFriday_returnsFridaySpecialDay() {
        val friday = LocalDate.of(2026, 3, 13)
        val result = useCase(friday)
        assertNotNull(result)
        assertEquals(SpecialDayType.FRIDAY, result!!.type)
        assertTrue(result.isFriday)
        assertFalse(result.isKandil)
    }

    @Test
    fun kandilDays2026_detectedCorrectly() {
        val mirac = useCase(LocalDate.of(2026, 1, 15))
        assertNotNull(mirac)
        assertEquals(SpecialDayType.MIRAC_KANDILI, mirac!!.type)
        assertTrue(mirac.isKandil)

        val berat = useCase(LocalDate.of(2026, 2, 2))
        assertNotNull(berat)
        assertEquals(SpecialDayType.BERAT_KANDILI, berat!!.type)
        assertTrue(berat.isKandil)

        val kadir = useCase(LocalDate.of(2026, 3, 16))
        assertNotNull(kadir)
        assertEquals(SpecialDayType.KADIR_GECESI, kadir!!.type)
        assertTrue(kadir.isKandil)

        val mevlid = useCase(LocalDate.of(2026, 8, 24))
        assertNotNull(mevlid)
        assertEquals(SpecialDayType.MEVLID_KANDILI, mevlid!!.type)
        assertTrue(mevlid.isKandil)

        val regaib = useCase(LocalDate.of(2026, 12, 10))
        assertNotNull(regaib)
        assertEquals(SpecialDayType.REGAIB_KANDILI, regaib!!.type)
        assertTrue(regaib.isKandil)
    }

    @Test
    fun kandilDays2027_detectedCorrectly() {
        val berat2027 = useCase(LocalDate.of(2027, 1, 22))
        assertNotNull(berat2027)
        assertEquals(SpecialDayType.BERAT_KANDILI, berat2027!!.type)
        assertTrue(berat2027.isKandil)
        assertTrue(berat2027.isFriday)

        val kadir2027 = useCase(LocalDate.of(2027, 3, 5))
        assertNotNull(kadir2027)
        assertEquals(SpecialDayType.KADIR_GECESI, kadir2027!!.type)
        assertTrue(kadir2027.isKandil)
        assertTrue(kadir2027.isFriday)
    }
}
