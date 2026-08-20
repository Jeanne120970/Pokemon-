package com.example.pokemon.data

data class PokemonStat(
    val name: String,
    val value: Int,
    val maxValue: Int = 255,
)

data class PokemonInfo(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val types: List<String> = emptyList(),
    val heightMeters: Double = 0.0,
    val weightKg: Double = 0.0,
    val abilities: List<String> = emptyList(),
    val stats: List<PokemonStat> = emptyList(),
)
