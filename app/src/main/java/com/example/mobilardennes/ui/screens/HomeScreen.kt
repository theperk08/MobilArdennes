package com.example.mobilardennes.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobilardennes.MobilardennesScreen
import com.example.mobilardennes.batteryPercentInterval
import com.example.mobilardennes.couleur
import com.example.mobilardennes.couleurBatteryPercent
import com.example.mobilardennes.couleurString
import com.example.mobilardennes.model.NestedCyclamStation
import com.example.mobilardennes.model.VehiculeData
import com.example.mobilardennes.model.NestedCyclamVehicules


@Composable
fun HomeScreen(
    navController: NavController,
    onCyclamStationButtonClicked: (Map<String, String>) -> Unit,
    ardennesUiState: ArdennesUiState,
    ardennesUiStateAllVehicules : ArdennesUiStateAllVehicules,
    ardennesUiStateAllVehiculesStatus : ArdennesUiStateAllVehiculesStatus,
    listeStatus: List<String>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    when (ardennesUiState) {
        is ArdennesUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())

        is ArdennesUiState.Success -> GrilleStationCyclam(
            onCyclamStationButtonClicked,
            navController,
            ardennesUiState.resultat,
            ardennesUiStateAllVehicules,
            ardennesUiStateAllVehiculesStatus,
            listeStatus,
            modifier.padding(top = contentPadding.calculateTopPadding())
        )

        is ArdennesUiState.Error -> ErrorScreen( modifier = modifier.fillMaxSize())
    }
}


@Composable
fun GrilleStationCyclam(
    onCyclamStationButtonClicked: (Map<String, String>) -> Unit,
    navController: NavController,
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
    val resultatVehicules = when (ardennesUiStateAllVehicules) {

        is ArdennesUiStateAllVehicules.SuccessAllVehicules -> ardennesUiStateAllVehicules.resultat

        else -> NestedCyclamVehicules(listOf(VehiculeData()))
    }

    val resultatVehiculesStatus = when (ardennesUiStateAllVehiculesStatus) {

        is ArdennesUiStateAllVehiculesStatus.SuccessAllVehiculesStatus -> ardennesUiStateAllVehiculesStatus.resultat

        else -> mapOf("rien" to NestedCyclamVehicules(listOf(VehiculeData())))
    }

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
            text = chaineStatus,
            modifier = Modifier.padding(top=1.dp, start = 14.dp, end=14.dp, bottom = 14.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = modifier,
        ) {

            items(sortedResult) { result ->

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

                var textBat = ""
                batInterval.forEach() {
                    if (it.value >0 ) {
                        Log.d("couleur bat", "${it.key} ; ${it.value} : ${colorResource(couleur(it.key))}")
                        textBat = "<span style=\"color:${couleurString(it.key)}\">${it.value}</span> - " + textBat
                    }
                }

                textBat = if(textBat.length>2) {
                    textBat.substring(startIndex = 0, endIndex = textBat.length-2)
                }
                else {"<span style=\"color:red\">0</span>"}

                val textBatHtml = AnnotatedString.fromHtml(textBat)

                Row(
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
                            navController.navigate(MobilardennesScreen.CyclamStation.name)
                        },
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(
                                couleurBatteryPercent(
                                    maxBatteryVae
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
                        modifier = Modifier.weight(0.1f)
                    )
                    Text(
                        text = textBatHtml,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(0.5f)
                    )

                    Image(
                        painter = painterResource(R.drawable.cyclam_docks),
                        contentDescription = stringResource(R.string.cyclam),
                        modifier = Modifier.weight(0.1f)
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
