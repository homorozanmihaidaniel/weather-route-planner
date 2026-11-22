package com.example.myapplication

import com.example.myapplication.domain.entities.FiltreState
import org.junit.Assert.*
import org.junit.Test

class FiltreStateTest {

    @Test
    fun defaultFilters_areAllActive() {
        val filtre = FiltreState()
        assertTrue(filtre.toateFiltreleActive)
        assertTrue(filtre.areFiltreActive)
        assertEquals(7, filtre.numarFiltreActive)
        assertTrue(filtre.arataAlerteMeteo)
        assertTrue(filtre.arataIncidenteTrafic)
        assertTrue(filtre.arataTraficTraseu)
        assertTrue(filtre.arataTranzitieZiNoapte)
        assertTrue(filtre.arataRadarPrecipitatii)
        assertTrue(filtre.arataUmbreZiNoapte)
        assertTrue(filtre.arataMiezulNoptii)
    }

    @Test
    fun deactivateAll_disablesEverything() {
        val filtre = FiltreState().cuToateActive(false)
        assertFalse(filtre.toateFiltreleActive)
        assertFalse(filtre.areFiltreActive)
        assertEquals(0, filtre.numarFiltreActive)
        assertFalse(filtre.arataAlerteMeteo)
        assertFalse(filtre.arataIncidenteTrafic)
        assertFalse(filtre.arataTraficTraseu)
        assertFalse(filtre.arataTranzitieZiNoapte)
        assertFalse(filtre.arataRadarPrecipitatii)
        assertFalse(filtre.arataUmbreZiNoapte)
        assertFalse(filtre.arataMiezulNoptii)
    }

    @Test
    fun activateAll_enablesEverythingAgain() {
        val deactivated = FiltreState().cuToateActive(false)
        val activated = deactivated.cuToateActive(true)
        assertTrue(activated.toateFiltreleActive)
        assertTrue(activated.areFiltreActive)
        assertEquals(7, activated.numarFiltreActive)
    }

    @Test
    fun categoryToggle_updatesCorrectly() {
        val filtre = FiltreState().copy(
            arataAlerteMeteo = false,
            arataIncidenteTrafic = true,
            arataMiezulNoptii = true
        )
        assertFalse(filtre.toateFiltreleActive)
        assertTrue(filtre.areFiltreActive)
        assertFalse(filtre.arataAlerteMeteo)
        assertTrue(filtre.arataIncidenteTrafic)
        assertTrue(filtre.arataMiezulNoptii)
        assertEquals(6, filtre.numarFiltreActive)
    }
}
