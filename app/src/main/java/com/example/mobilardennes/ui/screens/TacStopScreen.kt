package com.example.mobilardennes.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.core.graphics.toColorInt
import com.example.mobilardennes.data.FluoTacItems
import com.example.mobilardennes.model.Lines
import com.example.mobilardennes.model.NestedStopsHoursInstant
import com.example.mobilardennes.model.Stops


@Composable
fun TacInstantStopScreen(
    stopHours: NestedStopsHoursInstant,
    onDropDownListClick: (String) -> Unit,
    onTacStopClicked: (Int) -> Unit,
    onTacStopSelected: (Map<String, Int>) -> Unit,
    uiFluoState: FluoTacItems,
    fluoInstantUiState: FluoInstantStopsHoursUiState,
    fluoOperatorUiState: FluoStopsOperatorUiState,
    setTacListStops: (List<Stops>) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
        when (fluoOperatorUiState) {
            is FluoStopsOperatorUiState.Loading -> ""
            is FluoStopsOperatorUiState.SuccessFluoStopsOperator -> setTacListStops(fluoOperatorUiState.resultat.Data)
            is FluoStopsOperatorUiState.Error -> ""

        }

    when (fluoInstantUiState) {
        is FluoInstantStopsHoursUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())

        is FluoInstantStopsHoursUiState.SuccessFluoInstantStopsHours -> StopsHoursInstant(
            uiFluoState,
            stopHours,
            onDropDownListClick,
            onTacStopClicked,
            onTacStopSelected,
            modifier.padding(top = contentPadding.calculateTopPadding())
        )

        is FluoInstantStopsHoursUiState.Error -> ErrorScreen(modifier = modifier.fillMaxSize())
    }
}

@Composable
fun StopsHoursInstant(
    uiFluoState: FluoTacItems,
    stopHours: NestedStopsHoursInstant,
    onDropDownListClick: (String) -> Unit,
    onTacStopClicked: (Int) -> Unit,
    onTacStopSelected: (Map<String, Int>) -> Unit,
    modifier: Modifier
) {

    val sortedResult = stopHours.schedules.sorted()
    val linesId = stopHours.lines.groupBy { it.lineId }
    var linesIdMap= linesId.values.map{it[0].lineName.toString()}
    val linesIdMap2 = linesId.values.map{it[0]}
    val stopsIds = uiFluoState.tacListStops.map{it.Id}
    var selectedIndexLigne by remember { mutableStateOf(if (linesIdMap.indexOf(uiFluoState.lineName) > -1) {linesIdMap.indexOf(uiFluoState.lineName)} else {0}) }
    var selectedIndexStop by remember { mutableStateOf( if (stopsIds.indexOf(uiFluoState.stopId) > -1) {stopsIds.indexOf(uiFluoState.stopId)} else {0})}
    val linenameselect = if (selectedIndexLigne < linesIdMap.size) {linesIdMap[selectedIndexLigne]} else ""
    val lineName = stopHours.lines.find { it.lineName == linenameselect }
    val sortedResultFiltered = sortedResult.filter { it.lineId == lineName?.lineId }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {

        DropdownListStops(
            stops = uiFluoState.tacListStops,
            itemList = uiFluoState.tacListStops.map {it.Name},
            selectedIndex = selectedIndexStop,
            modifier = Modifier,
            onItemClick = {selectedIndexStop = it},
            onDropDownListClickUrl = onTacStopClicked,
            onDropDownTacSelect = onTacStopSelected,
            uiFluoState = uiFluoState,
        )
                DropdownListStopLines(
                    lines = linesIdMap2,
                    itemList = linesIdMap,
                    itemListUrl = linesIdMap,
                    selectedIndex = selectedIndexLigne,
                    modifier = Modifier,
                    onItemClick = { selectedIndexLigne = it },
                    onDropDownListClick = onDropDownListClick,
                    onDropDownListClickUrl = onTacStopClicked,
                    uiFluoState = uiFluoState,
                )

        if ((sortedResultFiltered.size>0) && (sortedResultFiltered[0].lineId != "")) {

            LazyVerticalGrid(
            columns = GridCells.Fixed(1), //GridCells.Adaptive(minSize = 200.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = modifier,
        ) {
                items(sortedResultFiltered) { result ->

                    val line = stopHours.lines.find { it.lineId == result.lineId }

                    Row(
                        modifier = modifier.padding(top = 2.dp)
                    ) {

                        Text(
                            text = "${line?.lineName}\n(-> ${result.terminus.terminusName})", //, Stop ${result.StopId.toString()}",
                            color = Color(line!!.lineTextColor!!.toColorInt()),
                            modifier = Modifier
                                .background(color = Color(line.lineColor!!.toColorInt()))
                                .weight(1f),
                            fontSize = 14.sp,
                            )

                        Text(
                            text = "${result.nextStops[0].nextStopTime.toString()}",
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(0.2f)
                                .align(Alignment.CenterVertically)
                        )

                    }
                }
            }

        }
        else {
            Row(
                modifier = modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = "",
                    modifier = Modifier
                        .weight(1f),
                    fontSize = 14.sp,
                    )
                Text(
                    text = "",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(0.2f)
                        .align(Alignment.CenterVertically)
                )

            }

        }
    }
}


@Composable
fun DropdownListStopLines(
    lines: List<Lines>,
    itemList: List<String>,
    itemListUrl: List<String>,
    selectedIndex: Int,
    modifier: Modifier,
    onItemClick: (Int) -> Unit,
    onDropDownListClick: (String) -> kotlin.Unit,
    onDropDownListClickUrl: (Int) -> Unit,
    uiFluoState: FluoTacItems,
)
{
    var showDropdown by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        if ((selectedIndex < lines.size) && (lines[0].lineId != "")) {

            Box(
                modifier = Modifier
                    .background(
                        Color(
                            lines[selectedIndex].lineColor.toString().toColorInt()
                        )
                    )
                    .clickable { showDropdown = true }
                    .border(2.dp, Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = itemList[selectedIndex],
                    fontSize = 18.sp,
                    color = Color(
                        lines[selectedIndex].lineTextColor.toString().toColorInt()
                    )
                )
            }

            // dropdown list
            Box() {
                if (showDropdown) {
                    Popup(
                        alignment = Alignment.TopCenter,
                        properties = PopupProperties(
                            excludeFromSystemGesture = true,
                            focusable = true,
                        ),
                        // to dismiss on click outside
                        onDismissRequest = { showDropdown = false }
                    ) {

                        Column(
                            modifier = Modifier
                                .heightIn(max = 90.dp)
                                .verticalScroll(state = scrollState)
                                .border(width = 1.dp, color = Color.Gray),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {

                            itemList.onEachIndexed { index, item ->

                                Box(
                                    modifier = modifier
                                        .background(
                                            Color(
                                                lines[index].lineColor.toString().toColorInt()
                                            )
                                        )
                                        .clickable {
                                            showDropdown = !showDropdown
                                            onItemClick(index)
                                            if (index != selectedIndex) {

                                                onDropDownListClick(
                                                    itemListUrl[index]
                                                )
                                                onDropDownListClickUrl(uiFluoState.stopId)
                                            }
                                        }
                                    ,
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item,
                                        fontSize = 18.sp,
                                        color = Color(
                                            lines[index].lineTextColor.toString().toColorInt()
                                        )
                                        )
                                }
                            }

                        }
                    }
                }
            }
        }
        else {
            Box(

                modifier = Modifier
                    .background(
                        Color.Black
                    )
                    .clickable { showDropdown = true }
                    .border(2.dp, Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "",
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }
        }
    }

}

@Composable
fun DropdownListStops(
    stops: List<Stops>,
    itemList: List<String>,
    selectedIndex: Int,
    modifier: Modifier,
    onItemClick: (Int) -> Unit,
    onDropDownListClickUrl: (Int) -> Unit,
    onDropDownTacSelect: (Map<String, Int>) -> Unit,
    uiFluoState: FluoTacItems,
    )
{
    var showDropdown by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {

        if ((selectedIndex < stops.size) && (stops[0].Id != null)) {
            Log.d("stop selected", stops[selectedIndex].toString())
            Box(
                modifier = Modifier
                    .background(Color.Black)
                    .clickable { showDropdown = true }
                    .border(2.dp, Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stops[selectedIndex].Name,
                    fontSize = 20.sp,
                    color = Color.White,
                )
            }

            // dropdown list
            Box() {
                if (showDropdown) {
                    Popup(
                        alignment = Alignment.TopCenter,
                        properties = PopupProperties(
                            excludeFromSystemGesture = true,
                            focusable = true,
                        ),
                        // to dismiss on click outside
                        onDismissRequest = { showDropdown = false }
                    ) {
                        Column(
                            modifier = Modifier
                                .heightIn(max = 90.dp)
                                .verticalScroll(state = scrollState)
                                .border(width = 1.dp, color = Color.Gray),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {

                            itemList.onEachIndexed { index, item ->

                                Box(
                                    modifier = modifier
                                        .background(Color.Black)
                                        .clickable {
                                            showDropdown = !showDropdown
                                            onItemClick(index)
                                            if (index != selectedIndex) {
                                                onDropDownTacSelect(
                                                    mapOf("stopId" to stops[index].Id)
                                                )
                                                onDropDownListClickUrl(uiFluoState.stopId)
                                            }
                                        }
                                    ,
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item,
                                        fontSize = 20.sp,
                                        color = Color.White,
                                        )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}