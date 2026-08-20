package com.example.pokemon.presentation

import com.example.pokemon.data.PokemonInfo

sealed class PokemonUiState {
    object Loading : PokemonUiState()
    data class Success(val pokemon: PokemonInfo) : PokemonUiState()
    data class Error(val message: String) : PokemonUiState()
}
