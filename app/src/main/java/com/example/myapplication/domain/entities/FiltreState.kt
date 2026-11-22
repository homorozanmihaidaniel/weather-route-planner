package com.example.myapplication.domain.entities

data class FiltreState(
    // 1. Alerte Meteo pe traseu (ploaie, ninsoare, ceață, nori denși)
    val arataAlerteMeteo: Boolean = true,

    // 2. Incidente Trafic TomTom (accidente, lucrări, blocaje, aglomerație, etc.)
    val arataIncidenteTrafic: Boolean = true,

    // 3. Condiții Trafic pe Traseu (segmente colorate verde/galben/portocaliu/roșu)
    val arataTraficTraseu: Boolean = true,

    // 4. Tranziție Zi / Noapte (pini și linii de crepuscul/noapte pe traseu)
    val arataTranzitieZiNoapte: Boolean = true,

    // 5. Radar Precipitații (RainViewer live radar)
    val arataRadarPrecipitatii: Boolean = true,

    // 6. Umbre Solare Zi / Noapte (poligoanele de umbră planetară pe glob)
    val arataUmbreZiNoapte: Boolean = true
) {
    val areFiltreActive: Boolean
        get() = arataAlerteMeteo || arataIncidenteTrafic || arataTraficTraseu ||
                arataTranzitieZiNoapte || arataRadarPrecipitatii || arataUmbreZiNoapte

    val toateFiltreleActive: Boolean
        get() = arataAlerteMeteo && arataIncidenteTrafic &&
                arataTraficTraseu && arataTranzitieZiNoapte && arataRadarPrecipitatii && arataUmbreZiNoapte

    val numarFiltreActive: Int
        get() {
            var count = 0
            if (arataAlerteMeteo) count++
            if (arataIncidenteTrafic) count++
            if (arataTraficTraseu) count++
            if (arataTranzitieZiNoapte) count++
            if (arataRadarPrecipitatii) count++
            if (arataUmbreZiNoapte) count++
            return count
        }

    val totalCategorii: Int = 6

    fun cuToateActive(activ: Boolean): FiltreState {
        return FiltreState(
            arataAlerteMeteo = activ,
            arataIncidenteTrafic = activ,
            arataTraficTraseu = activ,
            arataTranzitieZiNoapte = activ,
            arataRadarPrecipitatii = activ,
            arataUmbreZiNoapte = activ
        )
    }
}
