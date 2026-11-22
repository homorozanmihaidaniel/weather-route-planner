package com.example.myapplication

import com.example.myapplication.domain.entities.LocatieFavorita
import org.junit.Assert.*
import org.junit.Test

class LocatieFavoritaTest {

    @Test
    fun createLocatieFavorita_setsFieldsProperly() {
        val loc = LocatieFavorita(
            titlu = "Acasă",
            adresaSauNume = "București",
            latitudine = 44.4323,
            longitudine = 26.1063
        )
        assertEquals("Acasă", loc.titlu)
        assertEquals("București", loc.adresaSauNume)
        assertEquals(44.4323, loc.latitudine!!, 0.0001)
        assertEquals(26.1063, loc.longitudine!!, 0.0001)
        assertNotNull(loc.id)
    }

    @Test
    fun defaultId_isUnique() {
        val loc1 = LocatieFavorita(titlu = "Loc 1", adresaSauNume = "Cluj")
        val loc2 = LocatieFavorita(titlu = "Loc 2", adresaSauNume = "Timișoara")
        assertNotEquals(loc1.id, loc2.id)
    }
}
