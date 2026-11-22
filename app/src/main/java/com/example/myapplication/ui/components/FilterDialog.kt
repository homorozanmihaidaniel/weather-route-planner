package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.api.controllers.MapState

@Composable
fun FilterDialog(
    state: MapState,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = Color(0xFF1976D2)
                )
                Text(
                    text = "Filtre Căutare & Hartă",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Selectează ce filtre dorești să fie aplicate la căutare și afișate pe hartă:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )

                // Butoane rapide: Activează / Dezactivează toate
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            state.filtreState = state.filtreState.cuToateActive(true)
                            state.sincronizeazaDupaSchimbareFiltre()
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Activează tot", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            state.filtreState = state.filtreState.cuToateActive(false)
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Dezactivează tot", fontSize = 12.sp)
                    }
                }

                HorizontalDivider()

                // 1. ALERTE METEO PE TRASEU (automate pentru toate condițiile meteo)
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CloudQueue, contentDescription = null, tint = Color(0xFF0288D1))
                            Column {
                                Text(
                                    text = "Alerte Meteo pe Traseu",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Toate condițiile meteo (ploaie, ninsoare, ceață, nori)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                        Switch(
                            checked = state.filtreState.arataAlerteMeteo,
                            onCheckedChange = { activ ->
                                state.filtreState = state.filtreState.copy(arataAlerteMeteo = activ)
                            }
                        )
                    }
                }

                // 2. INCIDENTE TRAFIC (TOMTOM) (automate pentru toate incidentele)
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD32F2F))
                            Column {
                                Text(
                                    text = "Incidente Trafic (TomTom)",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Toate incidentele (accidente, lucrări, blocaje, dopuri)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                        Switch(
                            checked = state.filtreState.arataIncidenteTrafic,
                            onCheckedChange = { activ ->
                                state.comutaFiltruIncidente(activ)
                            }
                        )
                    }
                }

                // 3. TRAFIC PE TRASEU (Segmente colorate)
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Traffic, contentDescription = null, tint = Color(0xFF4CAF50))
                            Column {
                                Text(
                                    text = "Culori Trafic pe Rută",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Verde / Galben / Portocaliu / Roșu",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                        Switch(
                            checked = state.filtreState.arataTraficTraseu,
                            onCheckedChange = { activ ->
                                state.filtreState = state.filtreState.copy(arataTraficTraseu = activ)
                            }
                        )
                    }
                }

                // 4. TRANZIȚIE ZI / NOAPTE
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.WbTwilight, contentDescription = null, tint = Color(0xFFFF9800))
                            Column {
                                Text(
                                    text = "Tranziție Zi / Noapte",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Pini și linii apus / noapte pe traseu",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                        Switch(
                            checked = state.filtreState.arataTranzitieZiNoapte,
                            onCheckedChange = { activ ->
                                state.filtreState = state.filtreState.copy(arataTranzitieZiNoapte = activ)
                            }
                        )
                    }
                }

                // 5. RADAR PRECIPITAȚII
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF1976D2))
                            Column {
                                Text(
                                    text = "Radar Precipitații (RainViewer)",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Stratul radar meteo pe hartă",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                        Switch(
                            checked = state.filtreState.arataRadarPrecipitatii,
                            onCheckedChange = { activ ->
                                state.filtreState = state.filtreState.copy(arataRadarPrecipitatii = activ)
                            }
                        )
                    }
                }

                // 6. UMBRE ZI / NOAPTE
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Brightness3, contentDescription = null, tint = Color(0xFF3F51B5))
                            Column {
                                Text(
                                    text = "Umbre Solare Planetare",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Zonele de noapte și crepuscul pe glob",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                        Switch(
                            checked = state.filtreState.arataUmbreZiNoapte,
                            onCheckedChange = { activ ->
                                state.filtreState = state.filtreState.copy(arataUmbreZiNoapte = activ)
                            }
                        )
                    }
                }

                // 7. LINIE & ZONĂ ORA 00:00 (MIEZUL NOPȚII / ANUL NOU)
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Celebration, contentDescription = null, tint = Color(0xFFFFB300))
                            Column {
                                Text(
                                    text = "Unde este ora 00:00 (Anul Nou)",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Linie aurie și zonă pe glob ce arată miezul nopții în timp real",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                        Switch(
                            checked = state.filtreState.arataMiezulNoptii,
                            onCheckedChange = { activ ->
                                state.filtreState = state.filtreState.copy(arataMiezulNoptii = activ)
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Gata")
            }
        }
    )
}
