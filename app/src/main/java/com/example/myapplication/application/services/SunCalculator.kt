package com.example.myapplication.application.services

import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import java.util.Calendar
import kotlin.math.*

object SunCalculator {

    fun calculeazaUmbra(altitudineSoare: Double): Polygon {
        val D2R = Math.PI / 180.0
        val R2D = 180.0 / Math.PI

        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        var declinatie = -23.44 * cos((360.0 / 365.0) * (dayOfYear + 10) * D2R)
        if (abs(declinatie) < 0.1) declinatie = 0.1

        val utcHour = (System.currentTimeMillis() / 3600000.0) % 24.0
        val sunLon = 180.0 - (utcHour * 15.0)

        val puncte = mutableListOf<Point>()

        val sinAlt = sin(altitudineSoare * D2R)
        val sinDec = sin(declinatie * D2R)
        val cosDec = cos(declinatie * D2R)

        for (lon in -180..180 step 2) {
            val hra = (lon - sunLon) * D2R

            val a = sinDec
            val b = cosDec * cos(hra)
            val R = sqrt(a * a + b * b)

            var lat = 0.0
            val raport = sinAlt / R

            if (abs(raport) <= 1.0) {
                val alpha = atan2(a, b)
                val phi1 = alpha - acos(raport)
                val phi2 = alpha + acos(raport)

                lat = if (declinatie > 0) minOf(phi1, phi2) else maxOf(phi1, phi2)
                lat *= R2D
            } else {
                lat = if (declinatie > 0) -89.9 else 89.9
            }

            if (lat > 89.9) lat = 89.9
            if (lat < -89.9) lat = -89.9

            puncte.add(Point.fromLngLat(lon.toDouble(), lat))
        }

        if (declinatie > 0) {
            puncte.add(Point.fromLngLat(180.0, -89.9))
            puncte.add(Point.fromLngLat(-180.0, -89.9))
        } else {
            puncte.add(Point.fromLngLat(180.0, 89.9))
            puncte.add(Point.fromLngLat(-180.0, 89.9))
        }

        puncte.add(puncte.first())
        return Polygon.fromLngLats(listOf(puncte))
    }

    fun calculeazaAltitudineSoareViitor(lat: Double, lon: Double, timpViitorMs: Long): Double {
        val D2R = Math.PI / 180.0
        val R2D = 180.0 / Math.PI

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timpViitorMs
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        val declinatie = -23.44 * cos((360.0 / 365.0) * (dayOfYear + 10) * D2R)

        val utcHour = (timpViitorMs / 3600000.0) % 24.0
        val sunLon = 180.0 - (utcHour * 15.0)
        val hra = (lon - sunLon) * D2R

        val latR = lat * D2R
        val decR = declinatie * D2R

        val sinAlt = sin(latR) * sin(decR) + cos(latR) * cos(decR) * cos(hra)
        return asin(sinAlt) * R2D
    }

    fun calculeazaUnghiTerminator(lat: Double, lon: Double, timpMs: Long): Double {
        val D2R = Math.PI / 180.0
        val R2D = 180.0 / Math.PI

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timpMs
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        val declinatie = -23.44 * cos((360.0 / 365.0) * (dayOfYear + 10) * D2R)

        val utcHour = (timpMs / 3600000.0) % 24.0
        val sunLon = 180.0 - (utcHour * 15.0)
        val hra = (lon - sunLon) * D2R

        val latR = lat * D2R
        val decR = declinatie * D2R

        val y = sin(hra)
        val x = cos(hra) * sin(latR) - tan(decR) * cos(latR)
        val azimuthSoare = atan2(y, x) * R2D + 180.0

        return azimuthSoare + 90.0
    }

    fun calculeazaLongitudineMiezulNoptii(timpMs: Long = System.currentTimeMillis()): Double {
        val calendar = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = timpMs
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        val millisecond = calendar.get(Calendar.MILLISECOND)

        val utcHoursExact = hour + (minute / 60.0) + (second / 3600.0) + (millisecond / 3600000.0)
        var lon = -(utcHoursExact * 15.0)
        while (lon < -180.0) lon += 360.0
        while (lon > 180.0) lon -= 360.0
        return lon
    }

    fun genereazaZonaMiezulNoptii(lon: Double): List<Polygon> {
        val west = lon - 7.5
        val east = lon + 7.5
        return if (west < -180.0) {
            val p1 = Polygon.fromLngLats(listOf(listOf(
                Point.fromLngLat(-180.0, -85.0),
                Point.fromLngLat(east, -85.0),
                Point.fromLngLat(east, 85.0),
                Point.fromLngLat(-180.0, 85.0),
                Point.fromLngLat(-180.0, -85.0)
            )))
            val p2 = Polygon.fromLngLats(listOf(listOf(
                Point.fromLngLat(west + 360.0, -85.0),
                Point.fromLngLat(180.0, -85.0),
                Point.fromLngLat(180.0, 85.0),
                Point.fromLngLat(west + 360.0, 85.0),
                Point.fromLngLat(west + 360.0, -85.0)
            )))
            listOf(p1, p2)
        } else if (east > 180.0) {
            val p1 = Polygon.fromLngLats(listOf(listOf(
                Point.fromLngLat(west, -85.0),
                Point.fromLngLat(180.0, -85.0),
                Point.fromLngLat(180.0, 85.0),
                Point.fromLngLat(west, 85.0),
                Point.fromLngLat(west, -85.0)
            )))
            val p2 = Polygon.fromLngLats(listOf(listOf(
                Point.fromLngLat(-180.0, -85.0),
                Point.fromLngLat(east - 360.0, -85.0),
                Point.fromLngLat(east - 360.0, 85.0),
                Point.fromLngLat(-180.0, 85.0),
                Point.fromLngLat(-180.0, -85.0)
            )))
            listOf(p1, p2)
        } else {
            listOf(
                Polygon.fromLngLats(listOf(listOf(
                    Point.fromLngLat(west, -85.0),
                    Point.fromLngLat(east, -85.0),
                    Point.fromLngLat(east, 85.0),
                    Point.fromLngLat(west, 85.0),
                    Point.fromLngLat(west, -85.0)
                )))
            )
        }
    }

    fun getDescriereRegiuneMiezulNoptii(lon: Double): String {
        return when {
            lon in 165.0..180.0 || lon in -180.0..-172.5 -> "Noua Zeelandă, Fiji, Kiribati (UTC+12 / +13)"
            lon in 142.5..165.0 -> "Australia de Est (Sydney, Melbourne), Vladivostok (UTC+10 / +11)"
            lon in 127.5..142.5 -> "Japonia (Tokyo), Coreea de Sud (Seul) (UTC+9)"
            lon in 112.5..127.5 -> "China (Beijing), Singapore, Hong Kong (UTC+8)"
            lon in 97.5..112.5 -> "Thailanda (Bangkok), Vietnam, Indonezia (UTC+7)"
            lon in 82.5..97.5 -> "Bangladesh, Kazahstan (Almaty) (UTC+6)"
            lon in 67.5..82.5 -> "India (New Delhi, Mumbai), Pakistan (UTC+5 / +5:30)"
            lon in 52.5..67.5 -> "Emiratele Arabe (Dubai), Oman, Baku (UTC+4)"
            lon in 37.5..52.5 -> "Moscova, Arabia Saudită, Turcia de Est (UTC+3)"
            lon in 22.5..37.5 -> "România (București), Grecia, Ucraina, Finlanda, Egipt (UTC+2)"
            lon in 7.5..22.5 -> "Europa Centrală (Germania, Italia, Franța, Spania) (UTC+1)"
            lon in -7.5..7.5 -> "Marea Britanie (Londra), Portugalia, Ghana (UTC+0)"
            lon in -22.5..-7.5 -> "Insulele Azore, Capul Verde (UTC-1)"
            lon in -37.5..-22.5 -> "Atlanticul Central, Groenlanda de Sud (UTC-2)"
            lon in -52.5..-37.5 -> "Brazilia (Rio de Janeiro, São Paulo), Argentina (UTC-3)"
            lon in -67.5..-52.5 -> "America de Sud (Santiago, Caracas) (UTC-4)"
            lon in -82.5..-67.5 -> "Coasta de Est SUA (New York, Miami, Washington) (UTC-5)"
            lon in -97.5..-82.5 -> "Centrul SUA (Chicago, Dallas), Mexic (UTC-6)"
            lon in -112.5..-97.5 -> "Zona Munților Stâncoși SUA (Denver) (UTC-7)"
            lon in -127.5..-112.5 -> "Coasta de Vest SUA (Los Angeles, San Francisco) (UTC-8)"
            lon in -142.5..-127.5 -> "Alaska (Anchorage) (UTC-9)"
            lon in -157.5..-142.5 -> "Hawaii (Honolulu), Polinezia Franceză (UTC-10)"
            else -> "Samoa Americană, Insulele Midway (UTC-11)"
        }
    }
}
