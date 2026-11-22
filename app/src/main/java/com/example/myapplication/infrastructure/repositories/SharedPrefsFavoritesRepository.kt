package com.example.myapplication.infrastructure.repositories

import android.content.Context
import com.example.myapplication.application.interfaces.IFavoritesRepository
import com.example.myapplication.domain.entities.LocatieFavorita
import org.json.JSONArray
import org.json.JSONObject

class SharedPrefsFavoritesRepository(context: Context) : IFavoritesRepository {
    private val prefs = context.getSharedPreferences("MapAppFavoritesPrefs", Context.MODE_PRIVATE)
    private val key = "locatii_favorite"

    override fun getFavorites(): List<LocatieFavorita> {
        val jsonString = prefs.getString(key, null) ?: return defaultFavorites()
        return try {
            val array = JSONArray(jsonString)
            val list = mutableListOf<LocatieFavorita>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    LocatieFavorita(
                        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                        titlu = obj.optString("titlu", "Locație"),
                        adresaSauNume = obj.optString("adresaSauNume", ""),
                        latitudine = if (obj.has("latitudine") && !obj.isNull("latitudine")) obj.getDouble("latitudine") else null,
                        longitudine = if (obj.has("longitudine") && !obj.isNull("longitudine")) obj.getDouble("longitudine") else null,
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
            list
        } catch (e: Exception) {
            defaultFavorites()
        }
    }

    override fun addFavorite(favorite: LocatieFavorita): List<LocatieFavorita> {
        val current = getFavorites().toMutableList()
        current.removeAll { it.adresaSauNume.trim().equals(favorite.adresaSauNume.trim(), ignoreCase = true) }
        current.add(0, favorite)
        saveFavorites(current)
        return current
    }

    override fun removeFavorite(id: String): List<LocatieFavorita> {
        val current = getFavorites().toMutableList()
        current.removeAll { it.id == id }
        saveFavorites(current)
        return current
    }

    override fun isFavorite(query: String): Boolean {
        if (query.isBlank()) return false
        val clean = query.trim()
        return getFavorites().any {
            it.adresaSauNume.trim().equals(clean, ignoreCase = true) || it.titlu.trim().equals(clean, ignoreCase = true)
        }
    }

    private fun saveFavorites(list: List<LocatieFavorita>) {
        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("titlu", item.titlu)
                put("adresaSauNume", item.adresaSauNume)
                if (item.latitudine != null) put("latitudine", item.latitudine)
                if (item.longitudine != null) put("longitudine", item.longitudine)
                put("timestamp", item.timestamp)
            }
            array.put(obj)
        }
        prefs.edit().putString(key, array.toString()).apply()
    }

    private fun defaultFavorites(): List<LocatieFavorita> {
        return listOf(
            LocatieFavorita(titlu = "Acasă", adresaSauNume = "București"),
            LocatieFavorita(titlu = "Birou", adresaSauNume = "Cluj-Napoca"),
            LocatieFavorita(titlu = "Vacanță", adresaSauNume = "Brașov")
        )
    }
}
