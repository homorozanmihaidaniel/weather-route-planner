package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.api.controllers.MapState
import com.example.myapplication.domain.entities.AppTab
import com.example.myapplication.domain.entities.LocatieFavorita

@Composable
fun FavoritesScreen(
    state: MapState,
    permissionLauncher: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var inputTitlu by remember { mutableStateOf("") }
    var inputAdresa by remember { mutableStateOf("") }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Locații Favorite",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Apasă pe o locație pentru a calcula traseul rapid",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                FilledTonalButton(
                    onClick = {
                        inputTitlu = ""
                        inputAdresa = state.textCautat
                        showAddDialog = true
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adaugă locație",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Adaugă")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Lista de favorite
            if (state.listaFavorite.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                        Text(
                            text = "Nu ai nicio locație favorită",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Apasă pe butonul de adăugare sau folosește inimioara din bara de căutare de pe hartă.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Button(
                            onClick = {
                                inputTitlu = ""
                                inputAdresa = state.textCautat
                                showAddDialog = true
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Adaugă prima locație")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.listaFavorite, key = { it.id }) { locatie ->
                        FavoriteItemCard(
                            locatie = locatie,
                            onSelect = {
                                state.tabCurent = AppTab.HARTA
                                state.efectueazaCautarea(locatie.adresaSauNume, permissionLauncher)
                            },
                            onDelete = {
                                state.stergeFavorit(locatie.id)
                            }
                        )
                    }
                }
            }
        }
    }

    // Dialog Adăugare Favorit
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.Red
                    )
                    Text("Adaugă locație favorită")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = inputTitlu,
                        onValueChange = { inputTitlu = it },
                        label = { Text("Nume / Etichetă (ex: Acasă, Birou)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = inputAdresa,
                        onValueChange = { inputAdresa = it },
                        label = { Text("Adresă sau Oraș (ex: Cluj-Napoca)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (state.textCautat.isNotBlank() && inputAdresa != state.textCautat) {
                        SuggestionChip(
                            onClick = { inputAdresa = state.textCautat },
                            label = { Text("Folosește: ${state.textCautat}") }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputAdresa.isNotBlank()) {
                            val titluFinal = if (inputTitlu.isBlank()) inputAdresa else inputTitlu
                            state.adaugaFavorit(titluFinal, inputAdresa)
                            showAddDialog = false
                        }
                    },
                    enabled = inputAdresa.isNotBlank()
                ) {
                    Text("Salvează")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Anulează")
                }
            }
        )
    }
}

@Composable
fun FavoriteItemCard(
    locatie: LocatieFavorita,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    val iconAles: ImageVector = when {
        locatie.titlu.contains("acasă", ignoreCase = true) || locatie.titlu.contains("acasa", ignoreCase = true) -> Icons.Default.Home
        locatie.titlu.contains("birou", ignoreCase = true) || locatie.titlu.contains("serviciu", ignoreCase = true) || locatie.titlu.contains("muncă", ignoreCase = true) -> Icons.Default.Work
        locatie.titlu.contains("munte", ignoreCase = true) || locatie.titlu.contains("vacanță", ignoreCase = true) -> Icons.Default.Terrain
        else -> Icons.Default.Place
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = iconAles,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = locatie.titlu,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (locatie.titlu != locatie.adresaSauNume) {
                    Text(
                        text = locatie.adresaSauNume,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onSelect
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = "Navighează spre locație",
                        tint = Color(0xFF1976D2)
                    )
                }

                IconButton(
                    onClick = onDelete
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Șterge din favorite",
                        tint = Color.Red.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
