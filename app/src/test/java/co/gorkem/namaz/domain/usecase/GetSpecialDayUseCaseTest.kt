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

    @Test
    fun kandilDays2028_detectedCorrectly() {
        val berat2028First = useCase(LocalDate.of(2028, 1, 11))
        assertNotNull(berat2028First)
        assertEquals(SpecialDayType.BERAT_KANDILI, berat2028First!!.type)
        assertTrue(berat2028First.isKandil)

        val kadir2028 = useCase(LocalDate.of(2028, 2, 22))
        assertNotNull(kadir2028)
        assertEquals(SpecialDayType.KADIR_GECESI, kadir2028!!.type)
        assertTrue(kadir2028.isKandil)

        val mevlid2028 = useCase(LocalDate.of(2028, 8, 2))
        assertNotNull(mevlid2028)
        assertEquals(SpecialDayType.MEVLID_KANDILI, mevlid2028!!.type)
        assertTrue(mevlid2028.isKandil)

        val regaib2028 = useCase(LocalDate.of(2028, 11, 23))
        assertNotNull(regaib2028)
        assertEquals(SpecialDayType.REGAIB_KANDILI, regaib2028!!.type)
        assertTrue(regaib2028.isKandil)

        val mirac2028 = useCase(LocalDate.of(2028, 12, 13))
        assertNotNull(mirac2028)
        assertEquals(SpecialDayType.MIRAC_KANDILI, mirac2028!!.type)
        assertTrue(mirac2028.isKandil)

        val berat2028Second = useCase(LocalDate.of(2028, 12, 30))
        assertNotNull(berat2028Second)
        assertEquals(SpecialDayType.BERAT_KANDILI, berat2028Second!!.type)
        assertTrue(berat2028Second.isKandil)
    }
}
