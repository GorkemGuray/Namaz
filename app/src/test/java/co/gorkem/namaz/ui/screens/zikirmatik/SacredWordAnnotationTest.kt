package co.gorkem.namaz.ui.screens.zikirmatik

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SacredWordAnnotationTest {

    private val redColor = Color.Red
    private val baseColor = Color.Black

    @Test
    fun annotateSacredWords_emptyInput_returnsEmptyAnnotatedString() {
        val result = annotateSacredWords("", redColor, baseColor)
        assertEquals("", result.text)
        assertTrue(result.spanStyles.isEmpty())
    }

    @Test
    fun annotateSacredWords_bareForm_annotatesSingleRedSpan() {
        val input = "الله"
        val result = annotateSacredWords(input, redColor, baseColor)

        assertEquals(input, result.text)
        assertEquals(1, result.spanStyles.size)
        assertEquals(0, result.spanStyles[0].start)
        assertEquals(input.length, result.spanStyles[0].end)
        assertEquals(redColor, result.spanStyles[0].item.color)
    }

    @Test
    fun annotateSacredWords_ligatureForm_annotatesSingleRedSpan() {
        val input = "\uFDF2"
        val result = annotateSacredWords(input, redColor, baseColor)

        assertEquals(input, result.text)
        assertEquals(1, result.spanStyles.size)
        assertEquals(0, result.spanStyles[0].start)
        assertEquals(input.length, result.spanStyles[0].end)
        assertEquals(redColor, result.spanStyles[0].item.color)
    }

    @Test
    fun annotateSacredWords_tashkeelVariants_allAnnotatedRed() {
        val variants = listOf(
            "اَللّٰهُ",
            "اللّٰهِ",
            "اللّٰهَ",
            "اللهُ",
            "اللهِ",
            "اللّٰهِۜ",
            "اَللهُ"
        )

        for (variant in variants) {
            val result = annotateSacredWords(variant, redColor, baseColor)
            assertEquals(variant, result.text)
            val redSpans = result.spanStyles.filter { it.item.color == redColor }
            assertEquals("Failed for variant: $variant", 1, redSpans.size)
            assertEquals(0, redSpans[0].start)
            assertEquals(variant.length, redSpans[0].end)
        }
    }

    @Test
    fun annotateSacredWords_multipleOccurrences_allAnnotatedInOrder() {
        val input = "يَا ﴿جَم۪يلُ﴾ يَٓا اَللّٰهُ ❁ يَا ﴿قَر۪يبُ﴾ يَٓا اَللّٰهُ"
        val result = annotateSacredWords(input, redColor, baseColor)

        assertEquals(input, result.text)
        val redSpans = result.spanStyles.filter { it.item.color == redColor }
        assertEquals(2, redSpans.size)

        for (span in redSpans) {
            assertEquals("اَللّٰهُ", input.substring(span.start, span.end))
        }

        val baseSpans = result.spanStyles.filter { it.item.color == baseColor }
        assertTrue(baseSpans.isNotEmpty())
    }

    @Test
    fun annotateSacredWords_noMatchOtherArabic_returnsBaseColorWithoutRed() {
        val input = "سُبْحَانَ رَبِّيَ الْعَظِيمِ"
        val result = annotateSacredWords(input, redColor, baseColor)

        assertEquals(input, result.text)
        val redSpans = result.spanStyles.filter { it.item.color == redColor }
        assertTrue(redSpans.isEmpty())
        val baseSpans = result.spanStyles.filter { it.item.color == baseColor }
        assertEquals(1, baseSpans.size)
        assertEquals(0, baseSpans[0].start)
        assertEquals(input.length, baseSpans[0].end)
    }

    @Test
    fun annotateSacredWords_allahumma_notColoredRed() {
        val input = "اَللّٰهُمَّ أَنْتَ السَّلَامُ"
        val result = annotateSacredWords(input, redColor, baseColor)

        assertEquals(input, result.text)
        val redSpans = result.spanStyles.filter { it.item.color == redColor }
        assertTrue(redSpans.isEmpty())
    }

    @Test
    fun annotateSacredWords_tevhidSentence_colorsOnlySacredWord() {
        val input = "لَآ إِلٰهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ"
        val result = annotateSacredWords(input, redColor, baseColor)

        assertEquals(input, result.text)
        val redSpans = result.spanStyles.filter { it.item.color == redColor }
        assertEquals(1, redSpans.size)
        assertEquals("اللهُ", input.substring(redSpans[0].start, redSpans[0].end))
    }

    @Test
    fun annotateSacredWords_bracketedAllah_colorsOnlyWordInsideBrackets() {
        val input = "﴿اَللّٰهُ﴾"
        val result = annotateSacredWords(input, redColor, baseColor)

        assertEquals(input, result.text)
        val redSpans = result.spanStyles.filter { it.item.color == redColor }
        assertEquals(1, redSpans.size)
        assertEquals("اَللّٰهُ", input.substring(redSpans[0].start, redSpans[0].end))
    }
}
