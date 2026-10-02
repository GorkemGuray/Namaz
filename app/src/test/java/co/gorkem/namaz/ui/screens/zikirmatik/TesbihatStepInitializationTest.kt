package co.gorkem.namaz.ui.screens.zikirmatik

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TesbihatStepInitializationTest {

    @Test
    fun uzunTesbihatSteps_forAllPrayers_areNonEmptyAndDistinctFromKisa() {
        val kisaSteps = NamazTesbihatiSteps.steps
        assertTrue(kisaSteps.isNotEmpty())

        TesbihatPrayerTime.values().forEach { prayerTime ->
            val uzunSteps = LongTesbihatSteps.getSteps(prayerTime)
            assertTrue("Steps for $prayerTime should not be empty", uzunSteps.isNotEmpty())
            assertNotEquals("Uzun steps for $prayerTime should differ from Kısa steps", kisaSteps, uzunSteps)
            assertEquals("Last step of $prayerTime must be COMPLETED", StepType.COMPLETED, uzunSteps.last().type)
        }
    }

    @Test
    fun tesbihatModes_displayNameIntegrity() {
        assertEquals("Uzun", TesbihatMode.UZUN.displayName)
        assertEquals("Kısa", TesbihatMode.KISA.displayName)
    }

    @Test
    fun uzunTesbihatSteps_perPrayerStepCountsAndNoFatiha() {
        assertEquals(18, LongTesbihatSteps.sabahSteps.size)
        assertEquals(13, LongTesbihatSteps.ogleSteps.size)
        assertEquals(13, LongTesbihatSteps.ikindiSteps.size)
        assertEquals(17, LongTesbihatSteps.aksamSteps.size)
        assertEquals(13, LongTesbihatSteps.yatsiSteps.size)

        TesbihatPrayerTime.values().forEach { prayerTime ->
            val steps = LongTesbihatSteps.getSteps(prayerTime)
            assertTrue(steps.none { it.title.contains("Fatiha", ignoreCase = true) || it.title.contains("Fâtiha", ignoreCase = true) })
        }
    }
}
