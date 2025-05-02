package com.example.mobilardennes.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobilardennes.R
import com.example.mobilardennes.ui.theme.MobilArdennesTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.substring
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobilardennes.MobilardennesScreen
import com.example.mobilardennes.OnClickButtonCyclam
import com.example.mobilardennes.batteryPercentInterval
import com.example.mobilardennes.couleur
import com.example.mobilardennes.couleurBatteryPercent
import com.example.mobilardennes.couleurString
import com.example.mobilardennes.data.NavigationItems
import com.example.mobilardennes.model.CyclamData
import com.example.mobilardennes.model.NestedCyclamStation
// import com.example.mobilardennes.network.CyclamStationApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
//import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import com.example.mobilardennes.model.NestedCyclamStationVehicules
import com.example.mobilardennes.model.VehiculeData
import com.example.mobilardennes.model.BatteryVae
import com.example.mobilardennes.model.NestedCyclamVehicules


@Composable
fun HomeScreen(
    navController: NavController,
    allStations: (String) -> Unit,
    onCyclamStationButtonClicked: (Map<String, String>) -> Unit,
    etape: Int,
    ardennesUiState: ArdennesUiState,
    stationcyclamUiState: NavigationItems, //StateFlow<NavigationItems>,
    ardennesUiStateAllVehicules : ArdennesUiStateAllVehicules,
    ardennesUiStateAllVehiculesStatus : ArdennesUiStateAllVehiculesStatus,
    listeStatus: List<String>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    when (ardennesUiState) {
        is ArdennesUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())

        // is ArdennesUiState.Success -> ResultScreen(ardennesUiState.resultat, modifier.padding(top = contentPadding.calculateTopPadding()))
        is ArdennesUiState.Success -> GrilleStationCyclam(onCyclamStationButtonClicked,
            navController,
            stationcyclamUiState,
            allStations,
            etape,
            ardennesUiState.resultat,
            ardennesUiStateAllVehicules,
            ardennesUiStateAllVehiculesStatus,
            listeStatus,
            modifier.padding(top = contentPadding.calculateTopPadding()))

        // modifier = modifier.fillMaxWidth()
        is ArdennesUiState.Error -> ErrorScreen( modifier = modifier.fillMaxSize())


    }
}

@Composable
fun GrilleStationCyclam(
    onCyclamStationButtonClicked: (Map<String, String>) -> Unit,
    navController: NavController,
    stationcyclamUiState: NavigationItems, //StateFlow<NavigationItems>,
    allStations: (String) -> Unit,
    etape:Int,
    resultat: NestedCyclamStation,
    ardennesUiStateAllVehicules: ArdennesUiStateAllVehicules,
    ardennesUiStateAllVehiculesStatus: ArdennesUiStateAllVehiculesStatus,
    listeStatus: List<String>,
    modifier: Modifier = Modifier
) {
    val sortedResult = resultat.data.sortedBy { it.name }
    val totalBicycles = sortedResult.fold(0.0) {total, cookie ->
        total + cookie.vehicules.total
    }
    val totalStations = sortedResult.size
    //stationcyclamUiState.cyclamStationsList


    Log.d("Resource string", totalBicycles.toString())
    Log.d("Resource string", totalStations.toString())



    val resultatVehicules = when (ardennesUiStateAllVehicules) {


        // is ArdennesUiState.Success -> ResultScreen(ardennesUiState.resultat, modifier.padding(top = contentPadding.calculateTopPadding()))
        is ArdennesUiStateAllVehicules.SuccessAllVehicules -> ardennesUiStateAllVehicules.resultat

        // modifier = modifier.fillMaxWidth()
        else -> NestedCyclamVehicules(listOf(VehiculeData()))


    }

    val resultatVehiculesStatus = when (ardennesUiStateAllVehiculesStatus) {


        // is ArdennesUiState.Success -> ResultScreen(ardennesUiState.resultat, modifier.padding(top = contentPadding.calculateTopPadding()))
        is ArdennesUiStateAllVehiculesStatus.SuccessAllVehiculesStatus -> ardennesUiStateAllVehiculesStatus.resultat

        // modifier = modifier.fillMaxWidth()
        else -> mapOf("rien" to NestedCyclamVehicules(listOf(VehiculeData())))
    }

    Log.d("cyclam vehicules status", resultatVehiculesStatus.toString())



    val numberMaintenance = resultatVehiculesStatus["maintenance"]?.data?.size ?: 0
    val numberService = resultatVehiculesStatus["service"]?.data?.size ?: 0
    val numberLost = resultatVehiculesStatus["lost"]?.data?.size ?: 0

    val numberStatus = listeStatus.map{it to (resultatVehiculesStatus[it]?.data?.size ?: 0)}


    var nbStatus = mutableListOf<String>()
    numberStatus.forEach{
        if (it.second >0)
        nbStatus += "${it.second} \"${it.first}\""
    }

    val chaineStatus = nbStatus.joinToString (prefix="( ", postfix = " )", separator = ", ")





    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(

            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            text= stringResource(R.string.cyclam_total, totalBicycles.toInt().toString(), totalStations.toString()),
            modifier = Modifier.padding(14.dp)
        )
        Text(

            textAlign = TextAlign.Center,
            fontStyle = FontStyle.Italic,
            fontSize = 12.sp,
            //text= stringResource(R.string.cyclam_status, numberService.toString(), numberMaintenance.toString(), numberLost.toString()),
            text = chaineStatus,
            modifier = Modifier.padding(top=1.dp, start = 14.dp, end=14.dp, bottom = 14.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(1), //GridCells.Adaptive(minSize = 200.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = modifier,
        ) {

            items(sortedResult) { result ->
                //allStations(result.stationId)



                var nbVehicules = 0
                var maxBatteryVae = 0
                val batInterval = mutableMapOf(
                    0 to 0,
                    1 to 0,
                    2 to 0,
                    3 to 0,
                    4 to 0)

                resultatVehicules.data.forEach {
                    if ( it.station == result.stationId) {
                        nbVehicules += 1
                        batInterval[batteryPercentInterval(it.battery_vae.percent)] = batInterval[batteryPercentInterval(it.battery_vae.percent)]!! + 1
                        if (it.battery_vae.percent > maxBatteryVae) {
                            maxBatteryVae = it.battery_vae.percent
                        }
                    }
                }

                val colorbat = if (resultatVehicules.data.size>0) {
                    colorResource(

                        couleur(
                            //result.vehicules.total
                            resultatVehicules.data[0].battery_vae.percent
                        )

                    )
                }
                else {Color.Red}

                var textBat = ""
                batInterval.forEach() {
                    if (it.value >0 ) {
                        Log.d("couleur bat", "${it.key} ; ${it.value} : ${colorResource(couleur(it.key))}")
                        textBat = "<span style=\"color:${couleurString(it.key)}\">${it.value}</span> - " + textBat
                    }
                    /*else {
                        textBat = "<span hidden style=\"color:white\">${it.value}</span> - " + textBat
                    }*/
                }

                textBat = if(textBat.length>2) {textBat.substring(startIndex = 0, endIndex = textBat.length-2)}
                else {"<span style=\"color:red\">0</span>"}

                val textBatHtml = AnnotatedString.fromHtml(textBat)

                Log.d("max battery", "${result.name} : ${maxBatteryVae}")
                Log.d("max battery color", "${colorResource(couleurBatteryPercent(maxBatteryVae))}")


                Row(
                    //horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.padding(top = 4.dp)
                ) {

                    Button(
                        onClick = {
                            onCyclamStationButtonClicked(
                                mapOf(
                                    "stationName" to result.name,
                                    "stationId" to result.stationId
                                )
                            )
                            Log.d("station button", "${result.name} : ${result.stationId}")
                            navController.navigate(MobilardennesScreen.CyclamStation.name)

                        }, //OnClickButtonCyclam(result.stationId)
                        modifier = Modifier
                            .padding(2.dp)
                            //.fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(

                                couleurBatteryPercent(
                                    maxBatteryVae//result.vehicules.total
                                    //resultatVehicules.data[0].battery_vae.percent
                                )

                            )
                        ),
                        enabled = true,
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Text(
                            text = result.name,
                            fontSize = 16.sp,
                            overflow = TextOverflow.Ellipsis
                        )
                    }



                    Image(
                        painter = painterResource(R.drawable.electric_bicycle),
                        contentDescription = stringResource(R.string.cyclam),
                        //contentScale = ContentScale.Crop,
                        modifier = Modifier.weight(0.1f)
                        //size(20.dp)
                    )
                    Text(
                        text = textBatHtml,  //"${result.vehicules.total} ; $nbVehicules",
                        fontSize = 16.sp,
                        //color = colorResource(couleur(result.vehicules.total))
                        modifier = Modifier.weight(0.5f)
                    )


                    Image(
                        painter = painterResource(R.drawable.cyclam_docks),
                        contentDescription = stringResource(R.string.cyclam),
                        //contentScale = ContentScale.Crop,
                        modifier = Modifier.weight(0.1f) //.size(20.dp)
                    )
                    Text(
                        text = "${result.statistics.docks.free}",
                        fontSize = 16.sp,
                        color = colorResource(couleur(result.statistics.docks.free)),
                        modifier = Modifier.weight(0.15f)
                    )


                }

            }
            item {
                Spacer(modifier = Modifier
                    .height(36.dp)
                    .padding(top = 8.dp))
            }
        }
    }

            // Text(
        //    text= "${resultat.name} : ${resultat.vehicules.total} vélo(s) dispo(s) et ${resultat.statistics.docks.free} emplacement(s) libre(s)",
        //    modifier=modifier
        //)


}


@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
    }
}


/**
 * ResultScreen displaying number of photos retrieved.
 */
@Composable
fun ResultScreen(photos: String, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(text = photos)
    }
}

@Preview(showBackground = true)
@Composable
fun ResultScreenPreview() {
    MobilArdennesTheme  {
        ResultScreen(stringResource(R.string.placeholder_result))
    }
}
