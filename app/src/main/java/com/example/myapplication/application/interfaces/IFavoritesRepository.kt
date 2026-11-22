package com.example.myapplication.application.interfaces

import com.example.myapplication.domain.entities.LocatieFavorita

interface IFavoritesRepository {
    fun getFavorites(): List<LocatieFavorita>
    fun addFavorite(favorite: LocatieFavorita): List<LocatieFavorita>
    fun removeFavorite(id: String): List<LocatieFavorita>
    fun isFavorite(query: String): Boolean
}
