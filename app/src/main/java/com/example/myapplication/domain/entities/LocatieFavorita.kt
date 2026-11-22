package com.example.myapplication.domain.entities

data class LocatieFavorita(
    val id: String = java.util.UUID.randomUUID().toString(),
    val titlu: String,
    val adresaSauNume: String,
    val latitudine: Double? = null,
    val longitudine: Double? = null,
    val timestamp: Long = System.currentTimeMillis()
)
