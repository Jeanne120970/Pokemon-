package com.example.pokemon.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.example.pokemon.data.PokemonRepository
import com.example.pokemon.presentation.theme.PokemonTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }
}

private enum class Screen { HOME, SEARCH, DETAIL }

@Composable
fun WearApp() {
    PokemonTheme {
        val repository = remember { PokemonRepository() }
        val coroutineScope = rememberCoroutineScope()
        var uiState by remember { mutableStateOf<PokemonUiState>(PokemonUiState.Loading) }
        var currentScreen by remember { mutableStateOf(Screen.HOME) }

        fun loadPokemon(id: Int? = null) {
            uiState = PokemonUiState.Loading
            coroutineScope.launch {
                uiState = try {
                    val pokemon = if (id != null) {
                        repository.getPokemonById(id)
                    } else {
                        repository.getRandomPokemon()
                    }
                    PokemonUiState.Success(pokemon)
                } catch (e: Exception) {
                    PokemonUiState.Error(e.message ?: "Error de conexión")
                }
            }
        }

        // Carga un Pokémon aleatorio al abrir la app.
        LaunchedEffect(Unit) {
            loadPokemon()
        }

        // AppScaffold se declara una sola vez por app: mantiene elementos
        // estáticos (como TimeText) visibles durante las transiciones entre
        // pantallas y coordina el swipe-to-dismiss.
        AppScaffold {
            when (currentScreen) {
                Screen.HOME -> HomeScreen(
                    uiState = uiState,
                    onSearchClick = { currentScreen = Screen.SEARCH },
                    onInfoClick = { currentScreen = Screen.DETAIL },
                    onRetry = { loadPokemon() },
                )

                Screen.SEARCH -> SearchScreen(
                    onConfirm = { id ->
                        loadPokemon(id)
                        currentScreen = Screen.HOME
                    },
                    onCancel = { currentScreen = Screen.HOME },
                )

                Screen.DETAIL -> DetailScreen(
                    uiState = uiState,
                    onBack = { currentScreen = Screen.HOME },
                )
            }
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun DefaultPreview() {
    WearApp()
}
