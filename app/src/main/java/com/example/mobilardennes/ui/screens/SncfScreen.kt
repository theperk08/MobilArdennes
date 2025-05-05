package com.example.mobilardennes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.mobilardennes.data.SncfItems
import com.example.mobilardennes.model.SncfTrainData
import com.example.mobilardennes.R
import com.example.mobilardennes.sncfTime


@Composable
fun SncfScreen(
    sncfUiState: SncfUiStateTrains,
    uiSncfState: SncfItems,
    onDropDownListClick: (Map<String, String>) -> Unit,
    onDropDownListClickGare: (Map<String, String>) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onDropDownListClickUrl: (String, String) -> Unit,
) {

    when (sncfUiState) {
        is SncfUiStateTrains.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())

        is SncfUiStateTrains.SuccessTrains -> GrilleStationSncf(
            sncfUiState.resultat,
            uiSncfState,
            onDropDownListClick,
            onDropDownListClickUrl,
            onDropDownListClickGare,
            modifier.padding(top = contentPadding.calculateTopPadding())
        )

        is SncfUiStateTrains.Error -> ErrorScreen(modifier = modifier.fillMaxSize())

    }
}


@Composable
fun GrilleStationSncf(
    resultat: List<SncfTrainData>,
    uiSncfState: SncfItems,
    onDropDownListClick: (Map<String, String>) -> Unit,
    onDropDownListClickUrl: (String, String) -> Unit,
    onDropDownListClickGare: (Map<String, String>) -> Unit,
    modifier: Modifier = Modifier
)
{
    val sortedResult = resultat.sortedBy { it.scheduledTime }
    val itemList = listOf<String>("Départs", "Arrivées")
    val itemListUrl = listOf("Departures", "Arrivals")
    val originList = listOf("Destination", "Provenance")
    val itemListGares =  uiSncfState.sncfGares.map { it.nom.toString() }
    val itemListGaresId = uiSncfState.sncfGares.map { "00" + it.codeUic.toString()}
    var selectedIndexSens by remember { mutableStateOf(itemListUrl.indexOf(uiSncfState.sncfSens)) }
    var selectedIndexGare by remember { mutableStateOf(itemListGaresId.indexOf(uiSncfState.sncfStationId)) }
    var buttonModifier = Modifier.width(80.dp)
    val backColorList = listOf(listOf(R.color.sncf_blue_light, R.color.sncf_blue_dark) , listOf(R.color.sncf_green_light, R.color.sncf_green_dark))
    var backcolor = backColorList[0][0]

    Column {

        Row(horizontalArrangement = Arrangement.Center) {
            Text("",
                modifier = Modifier.weight(0.5f))
            Box(modifier = Modifier.weight(2f))
            {
                DropdownListGaresExposed(
                    itemList = itemListGares,
                    itemListId = itemListGaresId,
                    selectedIndexGare = selectedIndexGare,
                    selectedIndexSens = selectedIndexSens,
                    onItemClick = { selectedIndexGare = it },
                    onDropDownListClick = onDropDownListClickGare,
                    onDropDownListClickUrl = onDropDownListClickUrl,
                    uiSncfState = uiSncfState,
                )
            }
            Text("",
                modifier = Modifier.weight(0.5f))
        }

        Row() {
            Box(modifier=Modifier.weight(1f))
            {
                DropdownList(
                    itemList = itemList,
                    itemListUrl = itemListUrl,
                    selectedIndex = selectedIndexSens,
                    originList = originList,
                    modifier = buttonModifier,
                    onItemClick = { selectedIndexSens = it },
                    onDropDownListClick = onDropDownListClick,
                    onDropDownListClickUrl = onDropDownListClickUrl,
                    uiSncfState = uiSncfState,
                )
            }
            Text(text=originList[selectedIndexSens],
                modifier=Modifier.weight(3f))

            Text(text="Voie",
                textAlign = TextAlign.Center,
                modifier=Modifier.weight(0.5f)
                    )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier,
        ) {

            itemsIndexed(sortedResult) { index, result ->
                backcolor = backColorList[selectedIndexSens][index%2]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier
                        .background(colorResource(backcolor))
                ) {
                    var times = if (result.scheduledTime == result.actualTime) {
                        "<b><span style=\"color:yellow\">${sncfTime(result.scheduledTime)}</span></b>"
                    } else {
                        "<s><span style=\"color:yellow\">${sncfTime(result.scheduledTime)}</span></s> <br> <span style=\"color:red\">${sncfTime(result.actualTime)}</span>"
                    }

                    var origine = if (selectedIndexSens==0) {result.traffic.destination} else {result.traffic.origin}
                    origine += "<br><span style=\"font-size:50%\">"  + result.trainType + " | " + result.trainNumber + "</span>"

                    if ("SUPPRESSION" in result.informationStatus.trainStatus) {
                        times = "<s>" + times + "</s>"
                        origine = "<s>" + origine + "</s><span style=\"color:red\"> SUPPRIMÉ</span>"
                    }

                    val htmlTimes = AnnotatedString.fromHtml(times)
                    val htmlOrigine=AnnotatedString.fromHtml(origine)

                    Text(
                        text = htmlTimes,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = htmlOrigine,
                        color = Color.White,
                        fontSize = 16.sp,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(3f)
                    )

                    Text(
                        text = if (result.platform.track!=null) {result.platform.track.toString()} else "",
                        fontSize = 16.sp,
                        color = Color.White,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.5f)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(36.dp).padding(top = 8.dp))
            }

        }

    }

}

@Composable
fun DropdownList(itemList: List<String>,
                 itemListUrl: List<String>,
                 selectedIndex: Int,
                 modifier: Modifier,
                 originList: List<String>,
                 onItemClick: (Int) -> Unit,
                 onDropDownListClick: (Map<String, String>) -> Unit,
                 onDropDownListClickUrl: (String, String) -> Unit,
                 uiSncfState: SncfItems,
                 ) {

    var showDropdown by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {

        val colorSncf = listOf(R.color.sncf_blue_dark, R.color.sncf_green_dark)
        val colorback = colorSncf[selectedIndex]

        Box(

            modifier = modifier
                .background(colorResource(colorback))
                .clickable { showDropdown = true }
                .border(2.dp, Color.White)
            ,
            contentAlignment = Alignment.Center
        ) {
            Text(text = itemList[selectedIndex],
                color = Color.White,
            )
        }

        // dropdown list
        Box {
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
                                    .background(colorResource(colorSncf[index]))
                                    .fillMaxWidth()
                                    .clickable {
                                        showDropdown = !showDropdown
                                        onItemClick(index)
                                        if (index != selectedIndex) {
                                            onDropDownListClick(
                                                mapOf("sens" to itemListUrl[index],
                                                    "origin" to originList[index])
                                            )
                                            onDropDownListClickUrl(uiSncfState.sncfSens.toString(), uiSncfState.sncfStationId.toString())
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item,
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

// https://composables.com/material3/exposeddropdownmenubox : marche pas
// https://stackoverflow.com/questions/76039608/editable-dynamic-exposeddropdownmenubox-in-jetpack-compose : ok

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownListGaresExposed(
    itemList: List<String>,
    itemListId: List<String>,
    selectedIndexGare: Int,
    selectedIndexSens: Int,
    onItemClick: (Int) -> Unit,
    onDropDownListClick: (Map<String, String>) -> kotlin.Unit,
    onDropDownListClickUrl: (String, String) -> Unit,
    uiSncfState: SncfItems,
) {
    val options = itemList
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(itemList[selectedIndexGare]) }

    val colorSncf = listOf(R.color.sncf_blue_dark, R.color.sncf_green_dark)
    val colorSncfun = listOf(R.color.sncf_blue_light, R.color.sncf_green_light)
    val colorback = colorSncf[selectedIndexSens]
    val colorbackun = colorSncfun[selectedIndexSens]

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            // The `menuAnchor` modifier must be passed to the text field for correctness.
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .background(color = colorResource(colorbackun)),
            value = selectedOptionText,
            onValueChange = { selectedOptionText = it
                            expanded = true
            },
            label = { Text("gare", color = Color.Black) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = colorResource(colorback),
                unfocusedContainerColor = colorResource(colorbackun)
            ),
        )
        // filter options based on text field value
        val filteringOptions = options.filter { it.contains(selectedOptionText, ignoreCase = true) }
        if (filteringOptions.isNotEmpty()) {
            DropdownMenu(
                modifier = Modifier
                    .background(colorResource(colorback))
                    .exposedDropdownSize(true)
                ,
                properties = PopupProperties(focusable = false),
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                filteringOptions.forEach { selectionOption ->
                    val index = options.indexOf(selectionOption)

                    DropdownMenuItem(
                        text = { Text(selectionOption, color=Color.White) },
                        onClick = {
                            selectedOptionText = selectionOption
                            expanded = false
                            onItemClick(index)
                            if (index != selectedIndexGare) {

                                onDropDownListClick(mapOf(
                                    "stationId" to itemListId[index],
                                    "stationName" to itemList[index])
                                )
                                onDropDownListClickUrl(uiSncfState.sncfSens.toString(), uiSncfState.sncfStationId.toString())
                            }
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}
