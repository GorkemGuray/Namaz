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
}
