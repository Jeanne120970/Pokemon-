package com.example.pokemon.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL


class PokemonRepository {

    companion object {
        private const val BASE_URL = "https://pokeapi.co/api/v2/pokemon/"
        private const val MAX_POKEMON_ID = 1025

        private val STAT_LABELS = mapOf(
            "hp" to "PS",
            "attack" to "Ataque",
            "defense" to "Defensa",
            "special-attack" to "At. Esp.",
            "special-defense" to "Def. Esp.",
            "speed" to "Velocidad",
        )
    }

    suspend fun getRandomPokemon(): PokemonInfo = withContext(Dispatchers.IO) {
        val randomId = (1..MAX_POKEMON_ID).random()
        fetchPokemon(randomId)
    }

    /** Busca un Pokémon por su número exacto (usado por la pantalla de búsqueda). */
    suspend fun getPokemonById(id: Int): PokemonInfo = withContext(Dispatchers.IO) {
        val safeId = id.coerceIn(1, MAX_POKEMON_ID)
        fetchPokemon(safeId)
    }

    private fun fetchPokemon(id: Int): PokemonInfo {
        val connection = (URL(BASE_URL + id).openConnection() as HttpURLConnection)
        connection.requestMethod = "GET"
        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000

        try {
            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw Exception("Error del servidor: código $responseCode")
            }

            val body = connection.inputStream.bufferedReader().use(BufferedReader::readText)
            val json = JSONObject(body)

            val name = json.getString("name")
                .replaceFirstChar { it.uppercase() }

            // Se intenta usar el artwork oficial y, si no existe, el sprite estándar.
            val sprites = json.getJSONObject("sprites")
            val officialArtwork = sprites
                .optJSONObject("other")
                ?.optJSONObject("official-artwork")
                ?.optString("front_default", null)
            val imageUrl = officialArtwork ?: sprites.optString("front_default", null)

            val typesArray = json.getJSONArray("types")
            val types = (0 until typesArray.length()).map { i ->
                typesArray.getJSONObject(i)
                    .getJSONObject("type")
                    .getString("name")
                    .replaceFirstChar { it.uppercase() }
            }

            val abilitiesArray = json.getJSONArray("abilities")
            val abilities = (0 until abilitiesArray.length()).map { i ->
                abilitiesArray.getJSONObject(i)
                    .getJSONObject("ability")
                    .getString("name")
                    .replace("-", " ")
                    .replaceFirstChar { it.uppercase() }
            }

            val statsArray = json.getJSONArray("stats")
            val stats = (0 until statsArray.length()).map { i ->
                val statObj = statsArray.getJSONObject(i)
                val statKey = statObj.getJSONObject("stat").getString("name")
                PokemonStat(
                    name = STAT_LABELS[statKey] ?: statKey,
                    value = statObj.getInt("base_stat"),
                )
            }

            return PokemonInfo(
                id = json.getInt("id"),
                name = name,
                imageUrl = imageUrl,
                types = types,
                heightMeters = json.getInt("height") / 10.0,
                weightKg = json.getInt("weight") / 10.0,
                abilities = abilities,
                stats = stats,
            )
        } finally {
            connection.disconnect()
        }
    }
}
