package com.example.myapplication.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.example.myapplication.api.controllers.MapState

@Composable
fun SearchBar(
    state: MapState,
    permissionLauncher: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = state.textCautat,
                onValueChange = { state.textCautat = it },
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { state.baraEsteFocusata = it.isFocused },
                placeholder = { Text("Caută...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Căutare") },
                trailingIcon = {
                    if (state.textCautat.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val esteFav = state.esteFavorit(state.textCautat)
                            IconButton(onClick = { state.comutaFavoritCurent() }) {
                                Icon(
                                    imageVector = if (esteFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = if (esteFav) "Elimină din favorite" else "Adaugă la favorite",
                                    tint = if (esteFav) Color.Red else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(onClick = { state.textCautat = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Șterge text")
                            }
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        state.efectueazaCautarea(state.textCautat, permissionLauncher)
                    }
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    state.showFilterDialog = true
                },
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Filtre",
                        tint = if (state.filtreState.areFiltreActive) Color(0xFF1976D2) else Color.Gray,
                        modifier = Modifier.size(26.dp)
                    )

                    if (!state.filtreState.toateFiltreleActive) {
                        Badge(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 6.dp, end = 6.dp),
                            containerColor = if (state.filtreState.areFiltreActive) Color(0xFF1976D2) else Color.Red
                        ) {
                            Text(
                                text = "${state.filtreState.numarFiltreActive}",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }

        // Lista cu istoricul
        AnimatedVisibility(visible = state.baraEsteFocusata && state.istoricCautari.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column {
                    state.istoricCautari.forEach { cautareVeche ->
                        ListItem(
                            headlineContent = { Text(cautareVeche) },
                            leadingContent = { Icon(Icons.Default.History, contentDescription = "Istoric", tint = Color.Gray) },
                            modifier = Modifier.clickable {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                state.efectueazaCautarea(cautareVeche, permissionLauncher)
                            }
                        )
                    }

                    HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)

                    TextButton(
                        onClick = {
                            state.stergeIstoric()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Șterge istoricul", color = Color.Red)
                    }
                }
            }
        }
    }
}
