package com.example.mobilardennes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.mobilardennes.MobilardennesScreen
import com.example.mobilardennes.model.NestedFluoTac


@Composable
fun FluoScreen(
    navController: NavController,
    onFluoLineButtonClicked: (Map<String, Int>) -> Unit,
    onFluoLineColorButtonClicked: (Map<String, String>) -> Unit,
    fluoUiState: FluoUiState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    when (fluoUiState) {
        is FluoUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())

        is FluoUiState.SuccessFluo -> GrilleTacLines(onFluoLineButtonClicked, onFluoLineColorButtonClicked ,navController, fluoUiState.resultat, modifier.padding(top = contentPadding.calculateTopPadding()))

        else -> ErrorScreen( modifier = modifier.fillMaxSize())
    }
}

@Composable
fun GrilleTacLines(
    onFluoLineButtonClicked: (Map<String, Int>) -> Unit,
    onFluoLineColorButtonClicked: (Map<String, String>) -> Unit,
    navController: NavController,
    resultat: NestedFluoTac,
    modifier: Modifier = Modifier
) {
    val sortedResult = resultat.Data!!.sortedBy { it.Order}

    LazyVerticalGrid (
        columns = GridCells.Fixed(1), //GridCells.Adaptive(minSize = 200.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
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
                    enabled = true,
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors( Color(("#" + result.Color).toColorInt()))
                ) {
                    Text(
                        text =result.Name,
                        fontSize = 16.sp,
                        color = Color.White,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(("#" + result.Color).toColorInt()) )
                            .fillMaxWidth()
                    )
                }

                result.LineDirections.forEach { direction ->

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "${direction.Direction} : ",
                            fontSize = 12.sp,
                        )

                        Button(
                            onClick = {
                                onFluoLineButtonClicked(mapOf(
                                    "lineId" to result.Id,
                                    "direction" to direction.Direction
                                ))
                                onFluoLineColorButtonClicked(mapOf(
                                    "lineColor" to result.Color,
                                    "lineName" to result.Name,
                                    "lineCode" to result.Number,
                                    "lineDirectionName" to direction.Name
                                ))
                                navController.navigate(MobilardennesScreen.FluoLine.name)
                            },
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            enabled = true,
                            contentPadding = PaddingValues(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors( Color(("#" + result.Color).toColorInt()))
                        ) {
                            Text(
                                text ="${direction.Name}", //result.Name,
                                fontSize = 16.sp,
                                color = Color.White,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(("#" + result.Color).toColorInt()) )
                                    .fillMaxWidth()
                            )
                        }

                    }
                }

            }

        }
        item {
            Spacer(modifier = Modifier.height(36.dp).padding(top = 8.dp))
        }
    }


}
