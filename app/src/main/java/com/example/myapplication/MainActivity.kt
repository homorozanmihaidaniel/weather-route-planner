package com.example.myapplication

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.api.controllers.rememberMapState
import com.example.myapplication.domain.entities.AppTab
import com.example.myapplication.infrastructure.repositories.OpenMeteoWeatherRepository
import com.example.myapplication.infrastructure.repositories.SharedPrefsFavoritesRepository
import com.example.myapplication.infrastructure.repositories.SharedPrefsSearchHistoryRepository
import com.example.myapplication.ui.components.FavoritesScreen
import com.example.myapplication.ui.components.MapScreen
import com.mapbox.common.MapboxOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setează tokenul Mapbox programatic înainte de a inițializa orice element vizual Mapbox
        MapboxOptions.accessToken = BuildConfig.MAPBOX_SECRET_TOKEN

        val searchHistoryRepository = SharedPrefsSearchHistoryRepository(applicationContext)
        val weatherRepository = OpenMeteoWeatherRepository(applicationContext)
        val favoritesRepository = SharedPrefsFavoritesRepository(applicationContext)

        setContent {
            val mapViewportState = rememberMapViewportState {
                setCameraOptions {
                    zoom(2.0)
                    center(Point.fromLngLat(24.5574, 46.5424))
                    pitch(0.0)
                    bearing(0.0)
                }
            }

            val mapState = rememberMapState(
                mapViewportState = mapViewportState,
                searchHistoryRepository = searchHistoryRepository,
                weatherRepository = weatherRepository,
                favoritesRepository = favoritesRepository
            )

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    mapState.gasesteLocatiaMea()
                } else {
                    Toast.makeText(applicationContext, "Aplicația are nevoie de permisiune pentru a-ți arăta locația!", Toast.LENGTH_SHORT).show()
                }
            }

            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            icon = { Icon(imageVector = Icons.Default.Map, contentDescription = "Hartă") },
                            label = { Text("Hartă") },
                            selected = mapState.tabCurent == AppTab.HARTA,
                            onClick = { mapState.tabCurent = AppTab.HARTA },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF1976D2),
                                selectedTextColor = Color(0xFF1976D2),
                                indicatorColor = Color(0xFFE3F2FD)
                            )
                        )
                        NavigationBarItem(
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (mapState.listaFavorite.isNotEmpty()) {
                                            Badge(containerColor = Color(0xFF1976D2)) {
                                                Text("${mapState.listaFavorite.size}")
                                            }
                                        }
                                    }
                                ) {
                                    Icon(imageVector = Icons.Default.Favorite, contentDescription = "Favorite")
                                }
                            },
                            label = { Text("Favorite") },
                            selected = mapState.tabCurent == AppTab.FAVORITE,
                            onClick = { mapState.tabCurent = AppTab.FAVORITE },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF1976D2),
                                selectedTextColor = Color(0xFF1976D2),
                                indicatorColor = Color(0xFFE3F2FD)
                            )
                        )
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = paddingValues.calculateBottomPadding())
                ) {
                    MapScreen(
                        state = mapState,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (mapState.tabCurent == AppTab.FAVORITE) {
                        FavoritesScreen(
                            state = mapState,
                            permissionLauncher = {
                                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
