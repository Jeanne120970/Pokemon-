package com.example.pokemon.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import coil.compose.AsyncImage

/**
 * Pantalla principal. ScreenScaffold + Column con scroll se adapta sola al
 * espacio disponible en relojes redondos, cuadrados, grandes o pequeños.
 */
@Composable
fun HomeScreen(
    uiState: PokemonUiState,
    onSearchClick: () -> Unit,
    onInfoClick: () -> Unit,
    onRetry: () -> Unit,
) {
    ScreenScaffold {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(PaddingValues(horizontal = 16.dp, vertical = 20.dp)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                when (uiState) {
                    is PokemonUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp))
                        Text(
                            text = "Buscando...",
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 6.dp, bottom = 10.dp),
                        )
                    }

                    is PokemonUiState.Success -> {
                        AsyncImage(
                            model = uiState.pokemon.imageUrl,
                            contentDescription = uiState.pokemon.name,
                            modifier = Modifier.size(56.dp),
                        )
                        Text(text = "N.º ${uiState.pokemon.id}", fontSize = 10.sp)
                        Text(
                            text = uiState.pokemon.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 10.dp),
                        )
                    }

                    is PokemonUiState.Error -> {
                        Text(
                            text = "Sin conexión",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = uiState.message,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 2.dp, bottom = 10.dp),
                        )
                    }
                }

                // Botón 1: buscar un Pokémon por número.
                Button(
                    onClick = onSearchClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                ) {
                    Text("🔍 Buscar número")
                }

                // Botón 2: ver toda la información del Pokémon actual.
                FilledTonalButton(
                    onClick = onInfoClick,
                    enabled = uiState is PokemonUiState.Success,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("📋 Información")
                }

                if (uiState is PokemonUiState.Error) {
                    Button(
                        onClick = onRetry,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                    ) {
                        Text("Reintentar")
                    }
                }
            }
        }
    }
}
