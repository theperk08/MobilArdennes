package com.example.mobilardennes.ui.screens

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.mobilardennes.batteryPercentInterval
import com.example.mobilardennes.data.DbConstants
import com.example.mobilardennes.model.CustomMarkerInfoWindow
import com.example.mobilardennes.model.CyclamData
import com.example.mobilardennes.model.NestedCyclamStation
import com.example.mobilardennes.model.NestedCyclamVehicules
import com.example.mobilardennes.model.VehiculeData
import com.example.mobilardennes.popupStationCyclam
import com.example.mobilardennes.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun CyclamMapScreen(
    navController: NavController,
    onCyclamStationButtonClicked: (Map<String, String>) -> Unit,
    ardennesUiState: ArdennesUiState,
    ardennesUiStateAllVehicules : ArdennesUiStateAllVehicules,
    modifier: Modifier = Modifier,
) {
    when (ardennesUiState) {
        is ArdennesUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is ArdennesUiState.Success -> OsmdroidMapView(ardennesUiState.resultat, ardennesUiStateAllVehicules, navController, onCyclamStationButtonClicked)
        is ArdennesUiState.Error -> ErrorScreen( modifier = modifier.fillMaxSize())
    }
}

fun addMarkerWithInfoWindow(mapView: MapView,
                            latitude: Double,
                            longitude: Double,
                            title: String,
                            description: String,
                            iconResId: Int,
                            maxBatteryPercent: Int,
                            navController: NavController,
                            onCyclamStationButtonClicked: (Map<String, String>) -> Unit,
                            station: CyclamData
) {
    val marker = Marker(mapView)
    marker.position = GeoPoint(latitude, longitude)
    marker.title = title
    marker.snippet = description
    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    val drawable: Drawable? = ResourcesCompat.getDrawable(mapView.context.resources, iconResId, null)
    if (drawable != null) {
        val bitmap = (drawable as BitmapDrawable).bitmap
        val scaledDrawable =
            Bitmap.createScaledBitmap(bitmap, 40, 60, true).toDrawable(mapView.context.resources)
        marker.icon = scaledDrawable
    }

    // Associer la fenêtre d'information personnalisée
    marker.infoWindow = CustomMarkerInfoWindow(mapView, navController,  onCyclamStationButtonClicked, maxBatteryPercent, station)
    mapView.overlays.add(marker)
}


@Composable
fun OsmdroidMapView(
    resultat: NestedCyclamStation,
    ardennesUiStateAllVehicules: ArdennesUiStateAllVehicules,
    navController: NavController,
    onCyclamStationButtonClicked: (Map<String, String>) -> Unit
) {
    val mapView = rememberMapViewWithLifecycle()
    Configuration.getInstance().userAgentValue = "MapApp"

    val resultatVehicules = when (ardennesUiStateAllVehicules) {
        is ArdennesUiStateAllVehicules.SuccessAllVehicules -> ardennesUiStateAllVehicules.resultat
        else -> NestedCyclamVehicules(listOf(VehiculeData()) )
    }

    AndroidView({ mapView }) { view ->
        mapView.setTileSource(TileSourceFactory.MAPNIK)

        mapView.setMultiTouchControls(true)

        // Initialize the MapView
        val mapController = view.controller
        mapController.setZoom(12.0)

        // Set the map center to a specific location
        val startPoint = GeoPoint(DbConstants.LAT_CENTER_CHARLEVILLE, DbConstants.LON_CENTER_CHARLEVILLE )
        mapController.setCenter(startPoint)

        // liste des stations cyclam
        val imagesStations = listOf(
            listOf(R.drawable.marqueur_0_color_0),
            listOf(R.drawable.marqueur_1_color_0, R.drawable.marqueur_1_color_1, R.drawable.marqueur_1_color_2, R.drawable.marqueur_1_color_3, R.drawable.marqueur_1_color_4 ),
            listOf(R.drawable.marqueur_2_color_0, R.drawable.marqueur_2_color_1, R.drawable.marqueur_2_color_2, R.drawable.marqueur_2_color_3, R.drawable.marqueur_2_color_4 ),
            listOf(R.drawable.marqueur_3_color_0, R.drawable.marqueur_3_color_1, R.drawable.marqueur_3_color_2, R.drawable.marqueur_3_color_3, R.drawable.marqueur_3_color_4 ),
            listOf(R.drawable.marqueur_4_color_0, R.drawable.marqueur_4_color_1, R.drawable.marqueur_4_color_2, R.drawable.marqueur_4_color_3, R.drawable.marqueur_4_color_4 ),
            listOf(R.drawable.marqueur_5_color_0, R.drawable.marqueur_5_color_1, R.drawable.marqueur_5_color_2, R.drawable.marqueur_5_color_3, R.drawable.marqueur_5_color_4 ),
            listOf(R.drawable.marqueur_6_color_0, R.drawable.marqueur_6_color_1, R.drawable.marqueur_6_color_2, R.drawable.marqueur_6_color_3, R.drawable.marqueur_6_color_4 ),
            listOf(R.drawable.marqueur_7_color_0, R.drawable.marqueur_7_color_1, R.drawable.marqueur_7_color_2, R.drawable.marqueur_7_color_3, R.drawable.marqueur_7_color_4 ),
            listOf(R.drawable.marqueur_8_color_0, R.drawable.marqueur_8_color_1, R.drawable.marqueur_8_color_2, R.drawable.marqueur_8_color_3, R.drawable.marqueur_8_color_4 ),
            listOf(R.drawable.marqueur_9_color_0, R.drawable.marqueur_9_color_1, R.drawable.marqueur_9_color_2, R.drawable.marqueur_9_color_3, R.drawable.marqueur_9_color_4 ),
            listOf(R.drawable.marqueur_9plus_color_0, R.drawable.marqueur_9plus_color_1, R.drawable.marqueur_9plus_color_2, R.drawable.marqueur_9plus_color_3, R.drawable.marqueur_9plus_color_4 )
        )

        // Add all Stations Markers
        for (station in resultat.data) {
            var maxBatteryVae = 0
            val batInterval = mutableMapOf(
                0 to 0,
                1 to 0,
                2 to 0,
                3 to 0,
                4 to 0)

            resultatVehicules.data.forEach {
                if ( it.station == station.stationId) {
                    batInterval[batteryPercentInterval(it.battery_vae.percent)] = batInterval[batteryPercentInterval(it.battery_vae.percent)]!! + 1
                    if (it.battery_vae.percent > maxBatteryVae) {
                        maxBatteryVae = it.battery_vae.percent
                    }
                }
            }
            val nbVelos = when(station.vehicules.total){
                in 0..9 -> station.vehicules.total
                else -> 10
            }

            addMarkerWithInfoWindow(
                mapView = mapView,
                latitude = station.position.latitude.toDouble(),
                longitude = station.position.longitude.toDouble(),
                title = station.name, // "<span><b>${station.name}<br>\\n ok</b></span>", : ne marche pas
                description = popupStationCyclam(station.vehicules.total, station.statistics.docks.free),
                navController = navController,
                onCyclamStationButtonClicked = onCyclamStationButtonClicked,
                station = station,
                maxBatteryPercent = maxBatteryVae,
                iconResId = imagesStations[nbVelos][batteryPercentInterval(maxBatteryVae)]

            )
        }

        // Refresh the map
        mapView.invalidate()
    }
}


@Composable
private fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
        }
    }

    val observer = remember { MapViewLifecycleObserver(mapView) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(Unit) {
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    return mapView
}

class MapViewLifecycleObserver(private val mapView: MapView) : DefaultLifecycleObserver {
    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        mapView.onResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        mapView.onPause()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        mapView.onDetach()
    }
}



