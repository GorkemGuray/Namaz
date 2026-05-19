package co.gorkem.namaz.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class QiblaUseCaseTest {

    private val useCase = QiblaUseCase()

    @Test
    fun istanbul_to_kaaba_bearing() {
        val result = useCase(41.0082, 28.9784)
        assertEquals(151.6, result.bearing, 1.0)
    }

    @Test
    fun nyc_to_kaaba_bearing() {
        val result = useCase(40.7128, -74.0060)
        assertEquals(58.5, result.bearing, 1.0)
    }

    @Test
    fun sydney_to_kaaba_bearing() {
        val result = useCase(-33.8688, 151.2093)
        assertEquals(277.5, result.bearing, 1.0)
    }

    @Test
    fun kaaba_to_kaaba_bearing() {
        val result = useCase(21.4225, 39.8262)
        assertEquals(0.0, result.bearing, 1.0)
    }

    @Test
    fun cardinal_direction_istanbul() {
        val result = useCase(41.0082, 28.9784)
        assertEquals("GD", result.cardinalDirection)
    }

    @Test
    fun cardinal_direction_nyc() {
        val result = useCase(40.7128, -74.0060)
        assertEquals("KD", result.cardinalDirection)
    }
}
