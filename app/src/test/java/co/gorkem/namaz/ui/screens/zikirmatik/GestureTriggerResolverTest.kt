package co.gorkem.namaz.ui.screens.zikirmatik

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GestureTriggerResolverTest {

    @Test
    fun extractGestureTriggers_quotedSingle_extractsPhraseAndYereEmoji() {
        val gesture = "\"ve’l-âfât\" derken avuç içleri yere bakacak şekilde çevrilir, bitince de eski haline getirilir."
        val triggers = extractGestureTriggers(gesture)

        assertEquals(1, triggers.size)
        assertEquals("ve’l-âfât", triggers[0].phrase)
        assertFalse(triggers[0].isPrefix)
        assertEquals("🫳", triggers[0].emoji)
    }

    @Test
    fun extractGestureTriggers_multiTriggerWithEllipsis_extractsOrderAndDirectionEmojis() {
        val gesture = "Avuçlar yukarı gelecek şekilde eller kaldırılır; \"ve min-külli nâr...\" derken avuç içleri yere çevrilir; \"Bi-ʿafvike yâ Mücîr...\" derken avuç içleri tekrar yukarı çevrilir."
        val triggers = extractGestureTriggers(gesture)

        assertEquals(2, triggers.size)
        assertEquals("ve min-külli nâr", triggers[0].phrase)
        assertTrue(triggers[0].isPrefix)
        assertEquals("🫳", triggers[0].emoji)

        assertEquals("Bi-ʿafvike yâ Mücîr", triggers[1].phrase)
        assertTrue(triggers[1].isPrefix)
        assertEquals("🤲", triggers[1].emoji)
    }

    @Test
    fun extractGestureTriggers_quoteless_returnsEmptyList() {
        val gesture = "Dua ederken eller kaldırılır ve avuç içleri yere bakacak şekilde tutulur."
        val triggers = extractGestureTriggers(gesture)

        assertTrue(triggers.isEmpty())
    }

    @Test
    fun extractGestureTriggers_emptyOrBlank_returnsEmptyList() {
        assertTrue(extractGestureTriggers("").isEmpty())
        assertTrue(extractGestureTriggers("   ").isEmpty())
    }

    @Test
    fun findTriggerRanges_hyphenAndApostropheTolerantMatch() {
        val reading = "Šalâten tüncînâ bihâ min-cemîʿı’l-ehvâli ve’lâfât. Ve takḍîlenâ bi-hâ cemîʿa’l-ḥâcât."
        val triggers = listOf(
            GestureTrigger(phrase = "ve'l-âfât", isPrefix = false, emoji = "🫳")
        )

        val ranges = findTriggerRanges(reading, triggers)

        assertEquals(1, ranges.size)
        val range = ranges[0]
        assertEquals("ve’lâfât", reading.substring(range))
    }

    @Test
    fun findTriggerRanges_prefixMatch_expandsToWindow() {
        val reading = "Ve min-külli nâr vaḥfeẓnâ min-şerri’n-nefsi ve’ş-şeyṭân ve minşerri’l-cinni"
        val triggers = listOf(
            GestureTrigger(phrase = "ve min-külli nâr", isPrefix = true, emoji = "🫳")
        )

        val ranges = findTriggerRanges(reading, triggers)

        assertEquals(1, ranges.size)
        val range = ranges[0]
        assertEquals(0, range.first)
        assertEquals(24, range.last - range.first + 1)
        assertTrue(reading.substring(range).startsWith("Ve min-külli nâr"))
    }

    @Test
    fun findTriggerRanges_multiTrigger_matchesBothInOrder() {
        val reading = "Ve min-külli nâr vaḥfeẓnâ min-şerri’n-nefsi. Bi-ʿafvike yâ Mücîr, bi-faḍlike yâ Ğaffâr."
        val triggers = listOf(
            GestureTrigger(phrase = "ve min-külli nâr", isPrefix = true, emoji = "🫳"),
            GestureTrigger(phrase = "Bi-ʿafvike yâ Mücîr", isPrefix = true, emoji = "🤲")
        )

        val ranges = findTriggerRanges(reading, triggers)

        assertEquals(2, ranges.size)
        assertTrue(ranges[0].first < ranges[1].first)
        assertTrue(reading.substring(ranges[0]).startsWith("Ve min-külli nâr"))
        assertTrue(reading.substring(ranges[1]).startsWith("Bi-ʿafvike yâ Mücîr"))
    }

    @Test
    fun findTriggerRanges_noMatch_returnsEmptyList() {
        val reading = "Lâ ilâhe illallâhü vaḥdehû lâ şerîke leh"
        val triggers = listOf(
            GestureTrigger(phrase = "ve'l-âfât", isPrefix = false, emoji = "🫳")
        )

        val ranges = findTriggerRanges(reading, triggers)

        assertTrue(ranges.isEmpty())
    }

    @Test
    fun findTriggerRanges_emptyReadingOrTriggers_returnsEmptyList() {
        val triggers = listOf(GestureTrigger("ve'l-âfât", false, "🫳"))
        assertTrue(findTriggerRanges("", triggers).isEmpty())
        assertTrue(findTriggerRanges("   ", triggers).isEmpty())
        assertTrue(findTriggerRanges("Örnek okuma", emptyList()).isEmpty())
    }

    @Test
    fun actualLongTesbihatSteps_tuncinaAndTercumanAnchoringVerification() {
        val sabahSteps = LongTesbihatSteps.sabahSteps

        val tuncinaStep = sabahSteps[1]
        val tuncinaTriggers = extractGestureTriggers(tuncinaStep.handGesture ?: "")
        assertEquals(1, tuncinaTriggers.size)
        assertEquals("🫳", tuncinaTriggers[0].emoji)
        val tuncinaRanges = findTriggerRanges(tuncinaStep.textTurkish, tuncinaTriggers)
        assertEquals(1, tuncinaRanges.size)
        assertEquals("ve’lâfât", tuncinaStep.textTurkish.substring(tuncinaRanges[0]))

        val tercumanStep = sabahSteps.first { it.title.contains("Tercüman-ı İsm-i A'zâm") }
        val tercumanTriggers = extractGestureTriggers(tercumanStep.handGesture ?: "")
        assertEquals(2, tercumanTriggers.size)
        val tercumanRanges = findTriggerRanges(tercumanStep.textTurkish, tercumanTriggers)
        assertEquals(2, tercumanRanges.size)
        assertTrue(tercumanStep.textTurkish.substring(tercumanRanges[0]).startsWith("Ve min-külli nâr"))
        assertTrue(tercumanStep.textTurkish.substring(tercumanRanges[1]).startsWith("Bi-ʿafvike yâ Mücîr"))

        val istiazeStep = sabahSteps.first { it.title.contains("İstiâze") }
        val istiazeTriggers = extractGestureTriggers(istiazeStep.handGesture ?: "")
        assertTrue(istiazeTriggers.isEmpty())
        val istiazeRanges = findTriggerRanges(istiazeStep.textTurkish, istiazeTriggers)
        assertTrue(istiazeRanges.isEmpty())
    }

    @Test
    fun buildAnnotatedReading_anchorsEmojiAndHighlightSpan() {
        val reading = "Öncesi ve’lâfât sonrasi"
        val trigger = GestureTrigger("ve'l-âfât", false, "🫳")
        val matches = listOf(GestureTriggerMatch(trigger, 7..14))
        val annotated = buildAnnotatedReading(
            reading = reading,
            matches = matches,
            highlightColor = androidx.compose.ui.graphics.Color.Yellow,
            onHighlightColor = androidx.compose.ui.graphics.Color.Black
        )

        assertEquals("Öncesi ve’lâfât 🫳 sonrasi", annotated.text)
        assertTrue(annotated.spanStyles.isNotEmpty())
        assertEquals(7, annotated.spanStyles[0].start)
        assertEquals(15, annotated.spanStyles[0].end)
    }
}

