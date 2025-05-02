package com.example.mobilardennes.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobilardennes.data.FluoTacItems
import com.example.mobilardennes.model.NestedFluoTac
import com.example.mobilardennes.model.NestedFluoTacStops
import kotlinx.coroutines.flow.StateFlow


@Composable
fun FluoLineScreen(
    navController: NavController,
    // onCyclamStationButtonClicked: (Map<String, String>) -> Unit,
    // etape: Int,
    fluoStopsUiState: FluoStopsUiState,
    // fluoLineUiState: StateFlow<FluoTacItems>,

    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    when (fluoStopsUiState) {
        is FluoStopsUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())

        // is ArdennesUiState.Success -> ResultScreen(ardennesUiState.resultat, modifier.padding(top = contentPadding.calculateTopPadding()))
        is FluoStopsUiState.SuccessFluoStops -> GrilleTacLineStops(navController, fluoStopsUiState.resultat, modifier.padding(top = contentPadding.calculateTopPadding()))

        // modifier = modifier.fillMaxWidth()
        else -> ErrorScreen( modifier = modifier.fillMaxSize())


    }
}

@Composable
fun GrilleTacLineStops(
    // onCyclamStationButtonClicked: (Map<String, String>) -> Unit,
    navController: NavController,
    // stationcyclamUiState: StateFlow<NavigationItems>,
    // etape:Int,
    resultat: NestedFluoTacStops,
    modifier: Modifier = Modifier
) {
    val sortedResult0 = resultat.Data!!.StopDirections[0].Stops.sortedBy{ it.Order}
    // val sortedResult1 = if (resultat.Data.StopDirections.size > 1) {resultat.Data.StopDirections[1].Stops.sortedBy{ it.Order}} else null


/*
    LazyVerticalGrid (
        columns = GridCells.Fixed(2), //GridCells.Adaptive(minSize = 200.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    )  {

        items(sortedResult0) { result ->
*/
            Row()
            {
                Column(modifier = Modifier
                    .verticalScroll(rememberScrollState())) {
                    sortedResult0.forEach {
                        Text(text= "${it.Name} : ${it.Order} : ${it.Latitude} , ${it.Longitude}")

                    }
                }

                // if (sortedResult1 != null) Text (text=)
            }
            /*Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier.padding(top = 4.dp)
            ) {

                Button(
                    onClick = {

                    }, //OnClickButtonCyclam(result.stationId)
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    //colors = ButtonDefaults.buttonColors(containerColor = colorResource(couleur(result.vehicules.total))),
                    enabled = true,
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Text(text =result.Name,
                        fontSize = 16.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                }


                Row(verticalAlignment = Alignment.CenterVertically
                    ,
                ) {
                    Text(
                        text = "${result.LineDirections[0].Direction}",
                        fontSize = 12.sp,
                        //color = colorResource(couleur(result.LineDirections.))
                    )
                    Text(
                        text = "${result.LineDirections[0].Name}",
                        fontSize = 12.sp,
                        //color = colorResource(couleur(result.LineDirections.))
                    )
                }
                if (result.LineDirections.size>1) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${result.LineDirections[1].Direction}",
                            fontSize = 12.sp,
                            //color = colorResource(couleur(result.LineDirections.))
                        )
                        Text(
                            text = "${result.LineDirections[1].Name}",
                            fontSize = 12.sp,
                            //color = colorResource(couleur(result.statistics.docks.free))
                        )
                    }
                }


            }
            */


        /*item {
            Spacer(modifier = Modifier.height(36.dp).padding(top = 8.dp))
        }*/


    // Text(
    //    text= "${resultat.name} : ${resultat.vehicules.total} vélo(s) dispo(s) et ${resultat.statistics.docks.free} emplacement(s) libre(s)",
    //    modifier=modifier
    //)

}
