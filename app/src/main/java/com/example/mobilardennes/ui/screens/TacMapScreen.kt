package com.example.mobilardennes.ui.screens


import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import org.osmdroid.config.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
// import androidx.compose.ui.graphics.Color
import android.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.mobilardennes.R
import com.example.mobilardennes.data.DbConstants
import com.example.mobilardennes.data.FluoTacItems
import com.example.mobilardennes.model.CustomMarkerTacInfoWindow
import com.example.mobilardennes.model.Hours
import com.example.mobilardennes.model.NestedFluoStopsOperator
import com.example.mobilardennes.model.NestedFluoTacStops
import com.example.mobilardennes.model.Stops
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


@Composable
fun TacMapScreen( // pour afficher toutes les lignes du réseau TAC
    navController: NavController,
    onTacStationButtonClicked: (Map<String, Int>) -> Unit,
    onTacStationNameButtonClicked : (Map<String, String>) -> Unit,
    etape: Int,
    fluoStopsOperatorUiState: FluoStopsOperatorUiState,
    // stationcyclamUiState: StateFlow<NavigationItems>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
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
    etape: Int,
    fluoStopsUiState: FluoStopsUiState,
    //fluoStopsHoursUiState: FluoStopsHoursUiState,
    fluoLineStopsHoursUiState: FluoLineStopsHoursUiState,
    // stationcyclamUiState: StateFlow<NavigationItems>,
    direction: Int?,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    when (fluoStopsUiState) {
        is FluoStopsUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())

        // is ArdennesUiState.Success -> ResultScreen(ardennesUiState.resultat, modifier.padding(top = contentPadding.calculateTopPadding()))
        is FluoStopsUiState.SuccessFluoStops -> TacOsmdroidMapView2(
            getStopsHours,
            uiFluoStateItems.lineColor,
            uiFluoStateItems.lineCode,
            fluoStopsUiState.resultat,
            //fluoStopsHoursUiState,
            fluoLineStopsHoursUiState,
            navController,
            onTacStationButtonClicked,
            onTacStationNameButtonClicked,
            direction
        ) // context = LocalContext.current)

        // modifier = modifier.fillMaxWidth()
        is FluoStopsUiState.Error -> ErrorScreen( modifier = modifier.fillMaxSize())


    }
}

/*
fun addMarker(mapView: MapView, latitude: Double, longitude: Double, title: String,
              description: String, iconResId: Int
) {
    val marker = Marker(mapView)
    marker.position = GeoPoint(latitude, longitude)
    marker.title = title

    // marker.textLabelBackgroundColor = R.color.red
    marker.snippet = description
    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    // Charger l'icône personnalisée depuis les ressources
    val drawable: Drawable? = ResourcesCompat.getDrawable(mapView.context.resources, iconResId, null)
    if (drawable != null) {
        val bitmap = (drawable as BitmapDrawable).bitmap
        val scaledDrawable =
            Bitmap.createScaledBitmap(bitmap, 16, 16, true).toDrawable(mapView.context.resources)

        marker.icon = scaledDrawable
    }
    mapView.overlays.add(marker)
}
*/

fun addTacMarkerWithInfoWindow(mapView: MapView, latitude: Double, longitude: Double, title: String,
                            description: String, iconResId: Int,
                            navController: NavController,
                               onTacStationButtonClicked: (Map<String, Int>) -> Unit,
                               onTacStationNameButtonClicked: (Map<String, String>) -> Unit,
                            station: Stops,
                               lineColor: String?,
                               lineCode: String?
) {
    Log.d("markerIcon", "markerIcon")
    val marker = Marker(mapView)
    Log.d("markerIcon lineCode", lineCode.toString())
    marker.position = GeoPoint(latitude, longitude)
    marker.title = title
//        HtmlCompat.fromHtml("<span style=\"color:DodgerBlue\">some <br><b>string</b></span>", HtmlCompat.FROM_HTML_MODE_LEGACY).toString()//title
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
    getStopsHours: (Int) -> Unit,
    latitude: Double,
    longitude: Double,
    title: String,
    description: String,
    iconResId: Int,
    navController: NavController,
    onTacStationButtonClicked: (Map<String, Int>) -> Unit,
    onTacStationNameButtonClicked:(Map<String, String>) -> Unit,
    station: Stops,
    lineColor: String?,
    lineCode: String?
) {
    // Log.d("markerIcon", "markerIcon")
    // Log.d("station Id", station.Id.toString())

    val marker = Marker(mapView)
    // Log.d("markerIcon lineCode", lineCode.toString())

    marker.position = GeoPoint(latitude, longitude)

    marker.title = title
    marker.snippet = "prochain départ :"

    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

    marker.textLabelForegroundColor = Color.WHITE //Color.parseColor("#"+lineColor) // R.color.white// Color.WHITE
    //Log.d("markerIcon lineColor", lineColor.toString())
    marker.textLabelBackgroundColor = ("#"+lineColor).toColorInt() //Color.RED //("#"+lineColor).toColorInt()
    //Log.d("markerIcon Color apres", marker.textLabelBackgroundColor.toString())
    // marker.textLabelBackgroundColor = Color.parseColor("#"+lineColor) //R.color.red//Color.RED//Color.parseColor("#"+lineColor)
    //Log.d("markerIcon lineCode", lineCode.toString())
    //Log.d("markerIcon text size avant", marker.textLabelFontSize.toString())
    marker.textLabelFontSize = 60
    //Log.d("markerIcon text size après", marker.textLabelFontSize.toString())


    //     /!\ Toutes les propriétés du TextIcon doivent être définies avant cet appel
    marker.setTextIcon(lineCode)
    val heure = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
    val temps = heure.hour.toInt()*60 + heure.minute.toInt()
    Log.d("line stop hours state", lineStopsHoursUiState.toString())
    Log.d("temps", temps.toString())

    val horaire = when (lineStopsHoursUiState) {
       // is FluoLineStopsHoursUiState.Loading ->

        // is ArdennesUiState.Success -> ResultScreen(ardennesUiState.resultat, modifier.padding(top = contentPadding.calculateTopPadding()))
        is FluoLineStopsHoursUiState.SuccessFluoLineStopsHours -> lineStopsHoursUiState.resultat.Data.Hours.filter { (it.StopId == station.Id) && ((it.TheoricDepartureTime ?: 0) >= temps)} //[0].TheoricDepartureTime ?: 0
        // modifier = modifier.fillMaxWidth()
        else -> listOf(Hours(-1,-1,false,false,-1,-1,-1,-1,-1,-1,-1,-1,station.Id,-1,-10,-1))
        //is FluoLineStopsHoursUiState.Error -> 0


    }

    Log.d("stopId", station.Id.toString() )
    Log.d("stop name", station.Name)
    Log.d("horaire", horaire.toString())
    val horaireOk = if (horaire.size == 0) {-1} else horaire[0].TheoricDepartureTime//.TheoricDepartureTime

    var horaireOk2: Int = -1//if (horaire.size == 0) {-1} else horaire.minOf { it.TheoricDepartureTime}
    horaire.forEach {
        if (it.TheoricDepartureTime != null) {

            if (it.TheoricDepartureTime > -1) {
                if (horaireOk2 == -1) {
                    horaireOk2 = it.TheoricDepartureTime
                } else {
                    if (it.TheoricDepartureTime < horaireOk2) {
                        horaireOk2 = it.TheoricDepartureTime
                    }
                }
            }
        }
        Log.d("horaireok2", horaireOk2.toString())
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
        horaireOk2
    )
    marker.showInfoWindow()
    marker.closeInfoWindow()
    mapView.overlays.add(marker)
}

@Composable
fun TacOsmdroidMapView(resultat: NestedFluoStopsOperator, navController: NavController, onTacStationButtonClicked: (Map<String, Int>) -> Unit, onTacStationNameButtonClicked: (Map<String, String>) -> Unit ) {
    val mapView = rememberMapViewWithLifecycle()
    Configuration.getInstance().userAgentValue = "MapApp" // BuildConfig.APPLICATION_ID

    AndroidView({ mapView }) { view ->
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        // Initialize the MapView
        val mapController = view.controller
        mapController.setZoom(15.0)

        // Set the map center to a specific location
        val startPoint = GeoPoint(DbConstants.LAT_CENTER_CHARLEVILLE, 4.72)
        mapController.setCenter(startPoint)

        // liste des stations cyclam

        // Add all Stations Stops Markers
        for (station in resultat.Data) {
            //addMarker(mapView = mapView,

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
                iconResId = R.drawable.marqueur_fond_bleu_clair, //R.drawable.battery4,
                lineCode = "",
                lineColor = "",
                /*iconResId = when (station.vehicules.total) {
                    0 -> R.drawable.battery0
                    1 -> R.drawable.battery1
                    2 -> R.drawable.battery2
                    3 -> R.drawable.battery3
                    else -> R.drawable.battery4


                }*/
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
    //stopsHoursUiState: FluoStopsHoursUiState,
    fluoLineStopsHoursUiState: FluoLineStopsHoursUiState,
    navController: NavController,
    onTacStationButtonClicked: (Map<String, Int>) -> Unit,
    onTacStationNameButtonClicked: (Map<String, String>) -> Unit,
    direction: Int?
) {
    val mapView = rememberMapViewWithLifecycle()
    Configuration.getInstance().userAgentValue = "MapApp" // BuildConfig.APPLICATION_ID
    // Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
    //val context = LocalContext.current

    AndroidView({ mapView }) { view ->

        mapView.setTileSource(TileSourceFactory.MAPNIK)

//        mapView.visibility = true
        mapView.setMultiTouchControls(true)

        // Initialize the MapView
        val mapController = view.controller
        mapController.setZoom(14.0)

        // Set the map center to a specific location
        val startPoint = GeoPoint(49.774, 4.72)
        mapController.setCenter(startPoint)

        mapView.overlays.clear()

        // liste des stations cyclam
        // val nbStations = resultat.Data.size

        /*
                // Add a marker
                val marker = Marker(view)
                marker.position = startPoint
                marker.title = "Charleville"
                // marker.icon =
                // marker.image = R.drawable.battery1.toDrawable()


                mapView.overlays.add(marker)
        */

        // Add all Stations Stops Markers
        val resultatFiltered = resultat.Data!!.StopDirections.filter {it.Direction.Direction == direction}
        //for (direct in resultat.Data!!.StopDirections) {
            for (stop in resultatFiltered[0].Stops) {
                //addMarker(mapView = mapView,
                addTacMarkerWithInfoWindow2(
                    mapView = mapView,
                    //stopsHoursUiState = stopsHoursUiState,
                    lineStopsHoursUiState = fluoLineStopsHoursUiState,
                    getStopsHours = getStopsHours,
                    latitude = stop.Latitude.toDouble(),
                    longitude = stop.Longitude.toDouble(),
                    title = stop.Name, // "<span><b>${station.name}<br>\\n ok</b></span>", : ne marche pas
                    description = stop.Code,//popupStationCyclam(station.vehicules.total, station.statistics.docks.free), //"${station.vehicules.total} vélo et ${station.statistics.docks.free} places" ,// ,
                    navController = navController,
                    onTacStationButtonClicked = onTacStationButtonClicked,
                    onTacStationNameButtonClicked = onTacStationNameButtonClicked,
                    station = stop,
                    iconResId = R.drawable.battery4,
                    lineCode = lineCode,
                    lineColor = lineColor,
                    /*iconResId = when (station.vehicules.total) {
                    0 -> R.drawable.battery0
                    1 -> R.drawable.battery1
                    2 -> R.drawable.battery2
                    3 -> R.drawable.battery3
                    else -> R.drawable.battery4


                }*/
                )
            }
        //}

        /*
        // Add a cluster of markers
        val cluster = FolderOverlay()
        for (i in 1..10) {
            val randomPoint = GeoPoint(
                startPoint.latitude + (Math.random() - 0.5) / 10,
                startPoint.longitude + (Math.random() - 0.5) / 10
            )
            val randomMarker = Marker(view)
            randomMarker.position = randomPoint
            randomMarker.title = "Marker $i"
            cluster.add(randomMarker)
        }
        mapView.overlays.add(cluster)

        // Handle marker click events
        marker.setOnMarkerClickListener { _, _ ->
            Toast.makeText(view.context, "Marker clicked!", Toast.LENGTH_SHORT).show()
            true
        }
        */


        // Refresh the map
        mapView.invalidate()

    }
}



@Composable
private fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
//    val mapView = remember { MapView(context) }
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



