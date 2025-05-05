package com.example.mobilardennes.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilardennes.couleurBatteryPercent
import com.example.mobilardennes.imageBatteryPercent
import com.example.mobilardennes.model.NestedCyclamStationVehicules
import com.example.mobilardennes.R
import kotlin.math.round

@Composable
fun CyclamStationScreen(
    ardennesUiStateVehicules: ArdennesUiStateVehicules,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
    ){
    when (ardennesUiStateVehicules) {
        is ArdennesUiStateVehicules.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())

        is ArdennesUiStateVehicules.SuccessVehicules -> StationCyclamVehicules( ardennesUiStateVehicules.resultat, modifier.padding(top = contentPadding.calculateTopPadding()))

        is ArdennesUiStateVehicules.Error -> ErrorScreen( modifier = modifier.fillMaxSize())
    }
}


@Composable
fun StationCyclamVehicules(
    resultat: NestedCyclamStationVehicules,
    modifier: Modifier = Modifier
) {
    val sortedResult = resultat.data.sortedBy { it.number }

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
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    enabled = true,
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(
                            couleurBatteryPercent(
                                result.battery_vae.percent
                            )
                        )
                    )

                ) {
                    Text(text ="N° ${result.number.toString()}",
                        fontSize = 16.sp,)
                    Log.d("station Button N°", "${result.number}")
                }

                Row(verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(imageBatteryPercent(result.battery_vae.percent)),
                        contentDescription = "Battery Percent",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${result.battery_vae.percent} %",
                        fontSize = 12.sp,
                        color = colorResource(couleurBatteryPercent(result.battery_vae.percent)),
                        modifier = Modifier.padding(start = 10.dp)                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.road),
                        contentDescription = "Route",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${round(result.battery_vae.remaining_distance/1000.0).toInt()} km",
                        fontSize = 12.sp,
                        color = colorResource(couleurBatteryPercent(result.battery_vae.percent)),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(36.dp).padding(top = 8.dp))
        }

    }

}