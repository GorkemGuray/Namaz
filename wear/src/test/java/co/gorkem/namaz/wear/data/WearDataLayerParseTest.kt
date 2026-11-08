package co.gorkem.namaz.wear.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class WearDataLayerParseTest {

    @Test
    fun parsePrayerData_withValidJson_returnsObject() {
        val json = """
            {
                "cityName": "Istanbul",
                "prayers": [
                    {"name": "IMSAK", "time": "05:00"},
                    {"name": "GUNES", "time": "06:30"}
                ],
                "themeId": 1
            }
        """.trimIndent()

        val result = WearDataLayerService.parsePrayerData(json)
        assertNotNull(result)
        assertEquals("Istanbul", result?.cityName)
        assertEquals(2, result?.prayers?.size)
        assertEquals("IMSAK", result?.prayers?.get(0)?.name)
        assertEquals("05:00", result?.prayers?.get(0)?.time)
        assertEquals(1, result?.themeId)
    }

    @Test
    fun parsePrayerData_withCorruptJson_returnsNull() {
        val invalidJson = "{ invalid_json: 123 "
        val result = WearDataLayerService.parsePrayerData(invalidJson)
        assertNull(result)
    }

    @Test
    fun parsePrayerData_withEmptyString_returnsNull() {
        val result = WearDataLayerService.parsePrayerData("")
        assertNull(result)
    }
}
