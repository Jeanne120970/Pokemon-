package com.example.pokemon.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SwipeToDismissBox
import androidx.wear.compose.material3.Text
import coil.compose.AsyncImage
import com.example.pokemon.data.PokemonStat
import com.example.pokemon.presentation.theme.PokemonTypeColors

/**
 * Pantalla con toda la información del Pokémon. Usa ScalingLazyColumn dentro
 * de ScreenScaffold(scrollState = ...), el patrón recomendado por Wear OS
 * para que el contenido y el scroll se ajusten solos según la forma
 * (redonda o cuadrada) y el tamaño de cada reloj.
 */
@Composable
fun DetailScreen(
    uiState: PokemonUiState,
    onBack: () -> Unit,
) {
    SwipeToDismissBox(onDismissed = onBack) { isBackground ->
        if (!isBackground) {
            val listState = rememberScalingLazyListState()
            ScreenScaffold(scrollState = listState) { contentPadding ->
                when (uiState) {
                    is PokemonUiState.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(32.dp))
                        }
                    }

                    is PokemonUiState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = "No hay datos disponibles",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                            )
                        }
                    }

                    is PokemonUiState.Success -> {
                        val pokemon = uiState.pokemon
                        ScalingLazyColumn(
                            state = listState,
                            contentPadding = contentPadding,
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            item {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    AsyncImage(
                                        model = pokemon.imageUrl,
                                        contentDescription = pokemon.name,
                                        modifier = Modifier.size(72.dp),
                                    )
                                    Text(text = "N.º ${pokemon.id}", fontSize = 11.sp)
                                    Text(
                                        text = pokemon.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                    )
                                }
                            }

                            if (pokemon.types.isNotEmpty()) {
                                item {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        pokemon.types.forEach { type -> TypeChip(type = type) }
                                    }
                                }
                            }

                            item {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text(text = "⬍ ${pokemon.heightMeters} m", fontSize = 11.sp)
                                    Text(text = "⚖ ${pokemon.weightKg} kg", fontSize = 11.sp)
                                }
                            }

                            if (pokemon.abilities.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Habilidades",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 6.dp),
                                    )
                                }
                                item {
                                    Text(
                                        text = pokemon.abilities.joinToString(" • "),
                                        fontSize = 11.sp,
                                    )
                                }
                            }

                            if (pokemon.stats.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Estadísticas",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 6.dp),
                                    )
                                }
                                items(pokemon.stats) { stat -> StatRow(stat = stat) }
                            }

                            item {
                                Button(
                                    onClick = onBack,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp),
                                ) {
                                    Text("Volver")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TypeChip(type: String) {
    Box(
        modifier = Modifier
            .background(
                color = PokemonTypeColors.colorFor(type),
                shape = RoundedCornerShape(50),
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(text = type, fontSize = 10.sp, color = Color.White)
    }
}

@Composable
private fun StatRow(stat: PokemonStat) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = stat.name, fontSize = 10.sp)
            Text(text = stat.value.toString(), fontSize = 10.sp)
        }
        val fraction = (stat.value.coerceIn(0, stat.maxValue) / stat.maxValue.toFloat())
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Color.DarkGray, RoundedCornerShape(2.dp)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = fraction)
                    .height(4.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp)),
            )
        }
    }
}
