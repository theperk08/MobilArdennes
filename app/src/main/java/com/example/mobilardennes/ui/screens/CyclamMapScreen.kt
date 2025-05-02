package com.example.mobilardennes.ui.screens

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.Toast
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Typeface
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.text.HtmlCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.mobilardennes.MobilardennesScreen
import com.example.mobilardennes.R
import com.example.mobilardennes.batteryPercentInterval
import com.example.mobilardennes.couleur
import com.example.mobilardennes.data.DbConstants
// import com.example.mobilardennes.data.CustomInfoWindow
import com.example.mobilardennes.data.NavigationItems
import com.example.mobilardennes.model.BatteryVae
import com.example.mobilardennes.model.CustomMarkerInfoWindow
import com.example.mobilardennes.model.CyclamData
import com.example.mobilardennes.model.NestedCyclamStation
import com.example.mobilardennes.model.NestedCyclamVehicules
import com.example.mobilardennes.model.VehiculeData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView

import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.FolderOverlay

import org.osmdroid.views.overlay.Marker
import com.example.mobilardennes.popupStationCyclam


@Composable
fun CyclamMapScreen(
    navController: NavController,
    onCyclamStationButtonClicked: (Map<String, String>) -> Unit,
    etape: Int,
    ardennesUiState: ArdennesUiState,
    stationcyclamUiState: NavigationItems,
    ardennesUiStateAllVehicules : ArdennesUiStateAllVehicules,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    when (ardennesUiState) {
        is ArdennesUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())

        // is ArdennesUiState.Success -> ResultScreen(ardennesUiState.resultat, modifier.padding(top = contentPadding.calculateTopPadding()))
        is ArdennesUiState.Success -> OsmdroidMapView(ardennesUiState.resultat, stationcyclamUiState, ardennesUiStateAllVehicules, navController, onCyclamStationButtonClicked) // context = LocalContext.current)

        // modifier = modifier.fillMaxWidth()
        is ArdennesUiState.Error -> ErrorScreen( modifier = modifier.fillMaxSize())


    }
}

@Composable
fun OsmMap(context: Context) {


    AndroidView(factory = { ctx ->
        // var mapView: MapView
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        MapView(ctx).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(11.0)
            controller.setCenter(GeoPoint(DbConstants.LAT_CENTER_CHARLEVILLE, DbConstants.LON_CENTER_CHARLEVILLE))
        }

        // Ajouter un marqueur avec une fenêtre d'information personnalisée
        // addMarkerWithInfoWindow(mapView, 48.8566, 2.3522, "Paris", "Capitale de la France")


    })


}

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
//        HtmlCompat.fromHtml("<span style=\"color:DodgerBlue\">some <br><b>string</b></span>", HtmlCompat.FROM_HTML_MODE_LEGACY).toString()//title
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

/*
@Composable
fun MapScreen() {
    val mapView = rememberMapViewWithLifecycle()
    val context = LocalContext.current
    AndroidView(
        {mapView}
    ){
        mapView ->
        CoroutineScope(Dispatchers.Main).launch {
            val map=mapView.awaitMap()
        }
    }
}
*/


@Composable
fun OsmdroidMapView(
    resultat: NestedCyclamStation,
    stationcyclamUiState: NavigationItems,
    ardennesUiStateAllVehicules: ArdennesUiStateAllVehicules,
    navController: NavController,
    onCyclamStationButtonClicked: (Map<String, String>) -> Unit
) {
    val mapView = rememberMapViewWithLifecycle()
    Configuration.getInstance().userAgentValue = "MapApp" // BuildConfig.APPLICATION_ID
    // Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
    //val context = LocalContext.current


    val resultatVehicules = when (ardennesUiStateAllVehicules) {


        // is ArdennesUiState.Success -> ResultScreen(ardennesUiState.resultat, modifier.padding(top = contentPadding.calculateTopPadding()))
        is ArdennesUiStateAllVehicules.SuccessAllVehicules -> ardennesUiStateAllVehicules.resultat

        // modifier = modifier.fillMaxWidth()
        else -> NestedCyclamVehicules(listOf(VehiculeData()) )


    }

    AndroidView({ mapView }) { view ->
        mapView.setTileSource(TileSourceFactory.MAPNIK)

//        mapView.visibility = true
        mapView.setMultiTouchControls(true)

        // Initialize the MapView
        val mapController = view.controller
        mapController.setZoom(12.0)

        // Set the map center to a specific location
        val startPoint = GeoPoint(DbConstants.LAT_CENTER_CHARLEVILLE, DbConstants.LON_CENTER_CHARLEVILLE )
        mapController.setCenter(startPoint)

        // liste des stations cyclam
        val nbStations = resultat.data.size

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

/*
        // Add a marker
        val marker = Marker(view)
        marker.position = startPoint
        marker.title = "Charleville"
        // marker.icon =
        // marker.image = R.drawable.battery1.toDrawable()


        mapView.overlays.add(marker)
*/

        // Add all Stations Markers
        for (station in resultat.data) {
            //var nbVehicules = 0
            var maxBatteryVae = 0
            val batInterval = mutableMapOf(
                0 to 0,
                1 to 0,
                2 to 0,
                3 to 0,
                4 to 0)

            resultatVehicules.data.forEach {
                if ( it.station == station.stationId) {
                    //nbVehicules += 1
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



            // addMarker(mapView = mapView,
            addMarkerWithInfoWindow(
                mapView = mapView,
                latitude = station.position.latitude.toDouble(),
                longitude = station.position.longitude.toDouble(),
                title = station.name, // "<span><b>${station.name}<br>\\n ok</b></span>", : ne marche pas
                description = popupStationCyclam(station.vehicules.total, station.statistics.docks.free),
                // "${station.vehicules.total} vélo et ${station.statistics.docks.free} places"
                navController = navController,
                onCyclamStationButtonClicked = onCyclamStationButtonClicked,
                station = station,
                maxBatteryPercent = maxBatteryVae,
                iconResId = imagesStations[nbVelos][batteryPercentInterval(maxBatteryVae)]

                /*iconResId = when (batteryPercentInterval(maxBatteryVae)) {
                    0 -> R.drawable.battery0
                    1 -> R.drawable.battery1
                    2 -> R.drawable.battery2
                    3 -> R.drawable.battery3
                    else -> R.drawable.battery4
                }*/
            )
        }

        //val position = intent?.extras?.getInt("word") ?: -1
        //val stringId = resources.getIdentifier("word$position", "string", packageName)
        //title.text = getString(stringId)

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

@Composable
fun GrilleStationCyclamMap(
    onCyclamStationButtonClicked: (Map<String, String>) -> Unit,
    navController: NavController,
    stationcyclamUiState: StateFlow<NavigationItems>,
    etape:Int,
    resultat: NestedCyclamStation,
    modifier: Modifier = Modifier
) {

    val sortedResult = resultat.data.sortedBy { it.name }



    LazyVerticalGrid (
        columns = GridCells.Fixed(2), //GridCells.Adaptive(minSize = 200.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    )  {

        items(sortedResult) { result ->

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier.padding(top = 4.dp)
            ) {

                Button(
                    onClick = {
                        onCyclamStationButtonClicked(mapOf(
                            "stationName" to result.name,
                            "stationId" to result.stationId
                        ))
                        navController.navigate(MobilardennesScreen.CyclamStation.name)
                    }, //OnClickButtonCyclam(result.stationId)
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(couleur(result.vehicules.total))),
                    enabled = true,
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Text(text =result.name,
                        fontSize = 16.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                }


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(R.drawable.electric_bicycle),
                        contentDescription = stringResource(R.string.cyclam),
                        //contentScale = ContentScale.Crop,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${result.vehicules.total}",
                        fontSize = 12.sp,
                        color = colorResource(couleur(result.vehicules.total))
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.cyclam_docks),
                        contentDescription = stringResource(R.string.cyclam),
                        //contentScale = ContentScale.Crop,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${result.statistics.docks.free}",
                        fontSize = 12.sp,
                        color = colorResource(couleur(result.statistics.docks.free))
                    )
                }


            }

        }
        item {
            Spacer(modifier = Modifier.height(36.dp).padding(top = 8.dp))
        }
    }

    // Text(
    //    text= "${resultat.name} : ${resultat.vehicules.total} vélo(s) dispo(s) et ${resultat.statistics.docks.free} emplacement(s) libre(s)",
    //    modifier=modifier
    //)


}

