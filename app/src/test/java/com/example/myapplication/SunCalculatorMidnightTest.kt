package com.example.myapplication

import com.example.myapplication.application.services.SunCalculator
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class SunCalculatorMidnightTest {

    @Test
    fun midnightLongitude_atUtcZero_isZero() {
        // La 00:00:00 UTC, miezul nopții este la longitudinea 0° (Greenwich)
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val lon = SunCalculator.calculeazaLongitudineMiezulNoptii(cal.timeInMillis)
        assertEquals(0.0, lon, 0.01)
    }

    @Test
    fun midnightLongitude_atUtcTwelve_isOneHundredEighty() {
        // La 12:00:00 UTC, miezul nopții este la 180°
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val lon = SunCalculator.calculeazaLongitudineMiezulNoptii(cal.timeInMillis)
        assertEquals(180.0, Math.abs(lon), 0.01)
    }

    @Test
    fun midnightLongitude_atUtcTwentyTwo_isThirtyEast() {
        // La 22:00:00 UTC (ora 00:00 în România - UTC+2), miezul nopții este la +30°E
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.HOUR_OF_DAY, 22)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val lon = SunCalculator.calculeazaLongitudineMiezulNoptii(cal.timeInMillis)
        assertEquals(30.0, lon, 0.01)
    }

    @Test
    fun midnightZone_generatesPolygons() {
        val polygons = SunCalculator.genereazaZonaMiezulNoptii(30.0)
        assertTrue(polygons.isNotEmpty())
    }

    @Test
    fun midnightDescription_identifiesRomaniaAroundThirtyDegrees() {
        val descriere = SunCalculator.getDescriereRegiuneMiezulNoptii(30.0)
        assertTrue(descriere.contains("România"))
    }
}
