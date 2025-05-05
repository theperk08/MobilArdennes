package com.example.mobilardennes.ui.screens

import android.graphics.Bitmap
import android.graphics.Color
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
import androidx.core.graphics.toColorInt
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.mobilardennes.data.DbConstants
import com.example.mobilardennes.data.FluoTacItems
import com.example.mobilardennes.model.CustomMarkerTacInfoWindow
import com.example.mobilardennes.model.Hours
import com.example.mobilardennes.model.NestedFluoStopsOperator
import com.example.mobilardennes.model.NestedFluoTacStops
import com.example.mobilardennes.model.Stops
import com.example.mobilardennes.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


@Composable
fun TacMapScreen( // pour afficher toutes les lignes du réseau TAC
    navController: NavController,
    onTacStationButtonClicked: (Map<String, Int>) -> Unit,
    onTacStationNameButtonClicked : (Map<String, String>) -> Unit,
    fluoStopsOperatorUiState: FluoStopsOperatorUiState,
    modifier: Modifier = Modifier,
) {
    when (fluoStopsOperatorUiState) {
        is FluoStopsOperatorUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())

        is FluoStopsOperatorUiState.SuccessFluoStopsOperator -> TacOsmdroidMapView(fluoStopsOperatorUiState.resultat, navController, onTacStationButtonClicked, onTacStationNameButtonClicked) // context = LocalContext.current)

        is FluoStopsOperatorUiState.Error -> ErrorScreen( modifier = modifier.fillMaxSize())
    }
}

@Composable
fun TacMapScreen2( // pour afficher une seule ligne TAC
    getStopsHours: (Int) -> Unit,
    uiFluoStateItems : FluoTacItems,
    navController: NavController,
    onTacStationButtonClicked: (Map<String, Int>) -> Unit,
    onTacStationNameButtonClicked: (Map<String, String>) -> Unit,
    fluoStopsUiState: FluoStopsUiState,
    fluoLineStopsHoursUiState: FluoLineStopsHoursUiState,
    direction: Int?,
    modifier: Modifier = Modifier,
) {
    when (fluoStopsUiState) {
        is FluoStopsUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())

        is FluoStopsUiState.SuccessFluoStops -> TacOsmdroidMapView2(
            getStopsHours,
            uiFluoStateItems.lineColor,
            uiFluoStateItems.lineCode,
            fluoStopsUiState.resultat,
            fluoLineStopsHoursUiState,
            navController,
            onTacStationButtonClicked,
            onTacStationNameButtonClicked,
            direction
        )

        is FluoStopsUiState.Error -> ErrorScreen( modifier = modifier.fillMaxSize())
    }
}

fun addTacMarkerWithInfoWindow(mapView: MapView, latitude: Double, longitude: Double, title: String,
                            description: String, iconResId: Int,
                            navController: NavController,
                               onTacStationButtonClicked: (Map<String, Int>) -> Unit,
                               onTacStationNameButtonClicked: (Map<String, String>) -> Unit,
                            station: Stops,
                               lineColor: String?,
                               lineCode: String?
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
            Bitmap.createScaledBitmap(bitmap, 16, 24, true).toDrawable(mapView.context.resources)
        marker.icon = scaledDrawable
    }

    // CHOPER LES LIGNES DU STOP ?
    marker.infoWindow = CustomMarkerTacInfoWindow(true, mapView, navController,  onTacStationButtonClicked, onTacStationNameButtonClicked, station, lineColor)
    mapView.overlays.add(marker)
}


fun addTacMarkerWithInfoWindow2(
    mapView: MapView,
    lineStopsHoursUiState: FluoLineStopsHoursUiState,
    latitude: Double,
    longitude: Double,
    title: String,
    navController: NavController,
    onTacStationButtonClicked: (Map<String, Int>) -> Unit,
    onTacStationNameButtonClicked:(Map<String, String>) -> Unit,
    station: Stops,
    lineColor: String?,
    lineCode: String?
) {
    val marker = Marker(mapView)

    marker.position = GeoPoint(latitude, longitude)
    marker.title = title
    marker.snippet = "prochain départ :"
    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    marker.textLabelForegroundColor = Color.WHITE //Color.parseColor("#"+lineColor) // R.color.white// Color.WHITE
    marker.textLabelBackgroundColor = ("#"+lineColor).toColorInt() //Color.RED //("#"+lineColor).toColorInt()
    marker.textLabelFontSize = 60

    //     /!\ Toutes les propriétés du TextIcon doivent être définies avant cet appel
    marker.setTextIcon(lineCode)
    val heure = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
    val temps = heure.hour.toInt()*60 + heure.minute.toInt()

    val horaire = when (lineStopsHoursUiState) {
        is FluoLineStopsHoursUiState.SuccessFluoLineStopsHours -> lineStopsHoursUiState.resultat.Data.Hours.filter { (it.StopId == station.Id) && ((it.TheoricDepartureTime ?: 0) >= temps)} //[0].TheoricDepartureTime ?: 0

        else -> listOf(Hours(-1,-1,false,false,-1,-1,-1,-1,-1,-1,-1,-1,station.Id,-1,-10,-1))

    }

    var horaireOk: Int = -1//if (horaire.size == 0) {-1} else horaire.minOf { it.TheoricDepartureTime}
    horaire.forEach {
        if (it.TheoricDepartureTime != null) {
            if (it.TheoricDepartureTime > -1) {
                if (horaireOk == -1) {
                    horaireOk = it.TheoricDepartureTime
                } else {
                    if (it.TheoricDepartureTime < horaireOk) {
                        horaireOk = it.TheoricDepartureTime
                    }
                }
            }
        }
    }

// Associer la fenêtre d'information personnalisée
     marker.infoWindow = CustomMarkerTacInfoWindow(
        false,
        mapView,
        navController,
        onTacStationButtonClicked,
        onTacStationNameButtonClicked,
        station,
        lineColor,
        horaireOk
    )
    marker.showInfoWindow()
    marker.closeInfoWindow()
    mapView.overlays.add(marker)
}

@Composable
fun TacOsmdroidMapView(resultat: NestedFluoStopsOperator, navController: NavController, onTacStationButtonClicked: (Map<String, Int>) -> Unit, onTacStationNameButtonClicked: (Map<String, String>) -> Unit ) {
    val mapView = rememberMapViewWithLifecycle()
    Configuration.getInstance().userAgentValue = "MapApp"

    AndroidView({ mapView }) { view ->
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        val mapController = view.controller
        mapController.setZoom(15.0)

        // Set the map center to a specific location
        val startPoint = GeoPoint(DbConstants.LAT_CENTER_CHARLEVILLE, 4.72)
        mapController.setCenter(startPoint)

        // Add all Stations Stops Markers
        for (station in resultat.Data) {

            addTacMarkerWithInfoWindow(

                mapView = mapView,
                latitude = station.Latitude.toDouble(),
                longitude = station.Longitude.toDouble(),
                title = station.Name, // "<span><b>${station.name}<br>\\n ok</b></span>", : ne marche pas
                description = station.Code,//popupStationCyclam(station.vehicules.total, station.statistics.docks.free), //"${station.vehicules.total} vélo et ${station.statistics.docks.free} places" ,// ,
                navController = navController,
                onTacStationButtonClicked = onTacStationButtonClicked,
                onTacStationNameButtonClicked =onTacStationNameButtonClicked,
                station = station,
                iconResId = R.drawable.marqueur_fond_bleu_clair,
                lineCode = "",
                lineColor = "",
            )
        }
    }

}

@Composable
fun TacOsmdroidMapView2(
    getStopsHours: (Int) -> Unit,
    lineColor: String?,
    lineCode: String?,
    resultat: NestedFluoTacStops,
    fluoLineStopsHoursUiState: FluoLineStopsHoursUiState,
    navController: NavController,
    onTacStationButtonClicked: (Map<String, Int>) -> Unit,
    onTacStationNameButtonClicked: (Map<String, String>) -> Unit,
    direction: Int?
) {
    val mapView = rememberMapViewWithLifecycle()
    Configuration.getInstance().userAgentValue = "MapApp" // BuildConfig.APPLICATION_ID

    AndroidView({ mapView }) { view ->

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        val mapController = view.controller
        mapController.setZoom(14.0)

        // Set the map center to a specific location
        val startPoint = GeoPoint(49.774, 4.72)
        mapController.setCenter(startPoint)

        mapView.overlays.clear()

        // Add all Stations Stops Markers
        val resultatFiltered = resultat.Data!!.StopDirections.filter {it.Direction.Direction == direction}
        for (stop in resultatFiltered[0].Stops) {
                addTacMarkerWithInfoWindow2(
                    mapView = mapView,
                    lineStopsHoursUiState = fluoLineStopsHoursUiState,
                    latitude = stop.Latitude.toDouble(),
                    longitude = stop.Longitude.toDouble(),
                    title = stop.Name, // "<span><b>${station.name}<br>\\n ok</b></span>", : ne marche pas
                    navController = navController,
                    onTacStationButtonClicked = onTacStationButtonClicked,
                    onTacStationNameButtonClicked = onTacStationNameButtonClicked,
                    station = stop,
                    lineCode = lineCode,
                    lineColor = lineColor,
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
//            id = R.id.osm_map
//            clipToOutline = true
        }
    }

    val observer = remember { MapTacViewLifecycleObserver(mapView) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(Unit) {
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    return mapView
}

class MapTacViewLifecycleObserver(private val mapView: MapView) : DefaultLifecycleObserver {
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



