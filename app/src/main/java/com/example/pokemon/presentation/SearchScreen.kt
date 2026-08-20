package com.example.pokemon.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.Picker
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SwipeToDismissBox
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.rememberPickerState

private const val MAX_POKEMON_ID = 1025

/**
 * Pantalla para elegir el número del Pokémon con una rueda (Picker).
 * Soporta scroll táctil y también giro de la corona/bisel, por lo que
 * funciona igual de bien en cualquier tipo de reloj.
 * Envuelta en SwipeToDismissBox para poder salir deslizando desde el borde,
 * como en cualquier pantalla nativa de Wear OS.
 */
@Composable
fun SearchScreen(
    onConfirm: (id: Int) -> Unit,
    onCancel: () -> Unit,
) {
    SwipeToDismissBox(onDismissed = onCancel) { isBackground ->
        if (!isBackground) {
            ScreenScaffold {
                val pickerState = rememberPickerState(MAX_POKEMON_ID)
                val selectedId by remember { derivedStateOf { pickerState.selectedOptionIndex + 1 } }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Elige el número",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                    )

                    Picker(
                        state = pickerState,
                        contentDescription = "Número de Pokémon",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    ) { index ->
                        Text(
                            text = "#${index + 1}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                        )
                    }

                    Button(
                        onClick = { onConfirm(selectedId) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Buscar")
                    }
                }
            }
        }
    }
}
