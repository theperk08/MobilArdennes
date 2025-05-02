package com.example.mobilardennes

// import android.graphics.drawable.Icon
//import android.media.Image
import android.media.Image
import android.util.Log
//import androidx.annotation.RestrictTo
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
//import androidx.compose.material.icons.filled.Notifications
//import androidx.compose.material.icons.rounded.AccountCircle
//import androidx.compose.material.icons.rounded.Email
//import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import android.graphics.Color as ColorA
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.mobilardennes.data.Gare
import com.example.mobilardennes.data.NavigationItems
import com.example.mobilardennes.ui.screens.ArdennesViewModel
import com.example.mobilardennes.ui.screens.CyclamMapScreen
import com.example.mobilardennes.ui.screens.CyclamStationScreen
//import com.example.mobilardennes.ui.screens.ErrorScreen
//import com.example.mobilardennes.ui.screens.FluoLineScreen
import com.example.mobilardennes.ui.screens.FluoScreen
//import com.example.mobilardennes.ui.screens.FluoStopsOperatorUiState
import com.example.mobilardennes.ui.screens.HomeScreen
//import com.example.mobilardennes.ui.screens.LoadingScreen
import com.example.mobilardennes.ui.screens.SncfScreen
import com.example.mobilardennes.ui.screens.TacInstantStopScreen
import com.example.mobilardennes.ui.screens.TacMapScreen
import com.example.mobilardennes.ui.screens.TacMapScreen2
//import com.example.mobilardennes.ui.screens.TacOsmdroidMapView
//import com.example.mobilardennes.ui.screens.TacStopScreen
//import com.example.mobilardennes.ui.screens.TestScreen
import com.example.mobilardennes.ui.screens.TestScreenSimple
//import com.example.mobilardennes.ui.screens.TestUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.File
//import kotlin.reflect.typeOf

enum class MobilardennesScreen(@StringRes val title: Int) {
    Start(title = R.string.cyclam),
    CyclamMap(title = R.string.cyclam_map),
    Tac(title = R.string.tac),
    Sncf(title = R.string.sncf),
    CyclamStation(title = R.string.cyclam_station),
    FluoLine(title=R.string.fluo_line),
    TacMap(title=R.string.tac_map),
    TacStop(title=R.string.tac_stop),
    Test(title=R.string.test)


    // Summary(title = R.string.sncf)

}



/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobilArdennesApp(
    viewModel: ArdennesViewModel = viewModel(factory = ArdennesViewModel.Factory),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = MobilardennesScreen.valueOf(
        backStackEntry?.destination?.route ?: MobilardennesScreen.Start.name
    )

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed

    )
    val scope = rememberCoroutineScope()

    var etape by remember { mutableStateOf("") }
    var lignes2 by remember { mutableStateOf(false) } // pour afficher 2 lignes (au lieu d'une) de Texte en haut
    var iconText by remember { mutableStateOf( R.drawable.logo_cyclam) } // pour afficher une image à la place du Texte en haut
    var colorLine by remember { mutableStateOf(Color.Black) }
    var wicon by remember { mutableStateOf(R.drawable.electric_bicycle) }
    var sncfsens by remember { mutableStateOf("Departures") }
    var sncfStation by remember { mutableStateOf("0087172007") }


    ModalNavigationDrawer(

        drawerState = drawerState,
        gesturesEnabled = false,

        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    scope = scope,
                    drawerstate = drawerState,
                    onTacButtonClicked = {navController.navigate(MobilardennesScreen.Tac.name)},
                    onCyclamButtonClicked = {navController.navigate(MobilardennesScreen.Start.name)},
                    onSncfButtonClicked = {navController.navigate(MobilardennesScreen.Sncf.name)},
                    onCyclamMapButtonClicked = {navController.navigate(MobilardennesScreen.CyclamMap.name)},
                    onTacMapButtonClicked = {navController.navigate(MobilardennesScreen.TacMap.name)},
                    onTacStopButtonClicked = {navController.navigate(MobilardennesScreen.TacStop.name)},
                    onTestButtonClicked = {navController.navigate(MobilardennesScreen.Test.name)}



                    // onCyclamStationButtonClicked = {navController.navigate(MobilardennesScreen.CyclamStation.name)}
                )
            }

        }
    ) {
        Scaffold(

            topBar = {
                TopBar(
                    lignes2 = lignes2,
                    iconText = iconText,
                    colorLine = colorLine,
                    windowIcon = ImageVector.vectorResource(wicon),
                    windowName = etape,
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    }
                )


            }
        ) { innerPadding ->
            val uiState by viewModel.uiState.collectAsState()
            val uiSncfState by viewModel.uiSncfState.collectAsState()
            val uiFluoState by viewModel.uiFluoState.collectAsState()
            val uiFluoStateLines by viewModel.uiFluoStateLines.collectAsState()
            val uiFluoInstantStateStops by viewModel.uiFluoInstantStateStops.collectAsState()


            NavHost(
                navController = navController,
                startDestination = MobilardennesScreen.Start.name,
                modifier = Modifier.padding(innerPadding)
            )
            {
                composable(route = MobilardennesScreen.Start.name)
                {
                    val context = LocalContext.current
                    lignes2 = false
                    iconText = R.drawable.logo_cyclam
                    etape = context.getString(R.string.cyclam)
                    wicon = R.drawable.electric_bicycle
                    viewModel.getArdennesCyclam()

                    //viewModel.allCyclamStations(viewModel.ardennesUiState)

                    //Log.d("initialisation stations cyclam", uiState.cyclamStationsList.toString())
                    viewModel.getArdennesCyclamAllVehicules()
                    viewModel.clearCyclamVehiculesStatus()
                    //  destroyed, hs, lost, maintenance, maj, ready, service, stock
                    val listeStatus = listOf( "destroyed", "hs", "lost", "maintenance", "maj", "ready", "service", "stock")
                    viewModel.getArdennesCyclamAllVehiculesStatus(listeStatus)

                    HomeScreen(
                        navController = navController,
                        allStations = {viewModel.getArdennesCyclamStation(it)},
                        //allVehiculesStatus = {viewModel.addCyclamVehiculesStatus(it)}
                        onCyclamStationButtonClicked = {
                            viewModel.setCyclamStation(it)
                        },
                        // quantityOptions = DataSource.quantityOptions,
                        etape = uiState.ecran,
                        ardennesUiState = viewModel.ardennesUiState,
                        stationcyclamUiState = uiState,
                        ardennesUiStateAllVehicules = viewModel.ardennesUiStateAllVehicules,
                        ardennesUiStateAllVehiculesStatus = viewModel.ardennesUiStateAllVehiculesStatus,
                        listeStatus = listeStatus,
                        modifier = Modifier
                            .fillMaxSize()
                            //.padding(dimensionResource(R.dimen.padding_medium))
                    )
                }

                composable(route = MobilardennesScreen.CyclamMap.name)
                {
                    val context = LocalContext.current
                    lignes2 = false
                    iconText = R.drawable.logo_cyclam
                    etape = context.getString(R.string.cyclam_map)
                    wicon = R.drawable.electric_bicycle
                    viewModel.getArdennesCyclam()
                    viewModel.getArdennesCyclamAllVehicules()

                    CyclamMapScreen(
                        navController = navController,
                        onCyclamStationButtonClicked = {
                            viewModel.setCyclamStation(it)
                        },
                        // quantityOptions = DataSource.quantityOptions,
                        etape = uiState.ecran,
                        ardennesUiState = viewModel.ardennesUiState,
                        stationcyclamUiState = uiState,
                        ardennesUiStateAllVehicules = viewModel.ardennesUiStateAllVehicules,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(dimensionResource(R.dimen.padding_medium))
                    )
                }

                composable(route = MobilardennesScreen.Tac.name) {
                    val context = LocalContext.current
                    lignes2 = false
                    iconText = R.drawable.ic_broken_image
                    etape = context.getString(R.string.tac)
                    wicon = R.drawable.logo_tac
                    viewModel.getTacLines("line")


                    FluoScreen(
                        navController = navController,
                        onFluoLineButtonClicked = {
                            viewModel.setFluoLine(it)
                        },
                        onFluoLineColorButtonClicked = {
                            viewModel.setFluoLineColor(it)
                        },
                        // quantityOptions = DataSource.quantityOptions,
                        // etape = uiState.ecran,

                        fluoUiState = viewModel.fluoUiState,
                        // stationcyclamUiState = viewModel.uiState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(dimensionResource(R.dimen.padding_medium))
                    )
                }

                composable(route = MobilardennesScreen.TacMap.name)
                {
                    val context = LocalContext.current
                    lignes2 = false
                    iconText = R.drawable.ic_broken_image
                    etape = context.getString(R.string.tac_map)
                    wicon = R.drawable.logo_tac

                    if (uiFluoState.tacListStopsOk == false) {
                        viewModel.getFluoStopsOperator(42)
                        viewModel.setTacListStopsOk(true)
                    }


                    TacMapScreen(
                        navController = navController,
                        onTacStationButtonClicked = {
                            viewModel.setFluoStop(it)
                        },
                        onTacStationNameButtonClicked = {
                            viewModel.setFluoStopName(it)
                        },
                        // quantityOptions = DataSource.quantityOptions,
                        etape = uiState.ecran,
                        fluoStopsOperatorUiState = viewModel.fluoStopsOperatorUiState,
                        // stationcyclamUiState = viewModel.uiState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(dimensionResource(R.dimen.padding_medium))
                    )
                }

                composable(route = MobilardennesScreen.TacStop.name) {
                    val context = LocalContext.current
                    lignes2 = false
                    iconText = R.drawable.ic_broken_image
                    etape = context.getString(R.string.tac_stop)
                    wicon = R.drawable.logo_tac

                    Log.d("stop id", uiFluoState.stopId.toString())

                    if (uiFluoState.tacListStopsOk == false)
                    {
                        viewModel.getFluoStopsOperator(42)


                            viewModel.setTacListStopsOk(true)
                            Log.d("liste stops", uiFluoState.tacListStops.toString())

                        }

                    viewModel.getFluoInstantStopsHours(stopid = uiFluoState.stopId)

                    Log.d("stop line 0", uiFluoStateLines.toString())
                    TacInstantStopScreen(
                        navController = navController,
                        stopHours = uiFluoInstantStateStops,

                        onDropDownListClick = {
                            viewModel.setFluoLineSelected(it)
                        },
                        onTacStopClicked = {stopId: Int ->
                            viewModel.getFluoInstantStopsHours(stopId)},
                        onTacStopSelected = {
                            viewModel.setFluoStop(it)
                        },

                        fluoInstantUiState = viewModel.fluoInstantStopsHoursUiState,
                        fluoOperatorUiState = viewModel.fluoStopsOperatorUiState,
                        setTacListStops = {viewModel.setTacListStops(it)},
                        // stationcyclamUiState = viewModel.uiState,
                        uiFluoState = uiFluoState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(dimensionResource(R.dimen.padding_small))
                    )

                }

                composable(route = MobilardennesScreen.FluoLine.name) {
                    lignes2 = true
                    iconText = R.drawable.ic_broken_image
                    colorLine = Color(ColorA.parseColor("#"+ uiFluoState.lineColor))
                    etape = uiFluoState.lineName.toString() + "\n-> " + uiFluoState.lineDirectionName.toString()
                    wicon = R.drawable.logo_tac

                    viewModel.getTacLinesStops(uiFluoState.lineId)

                    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                    Log.d("today", today.toString())

                    viewModel.getTacLineStopsHours(uiFluoState.lineId, uiFluoState.direction, today.toString())

                    TacMapScreen2(
                        getStopsHours = {viewModel.getFluoStopsHours2(it)},
                        uiFluoStateItems = uiFluoState,
                        navController = navController,
                        onTacStationButtonClicked = {
                            viewModel.setFluoStop(it)
                        },
                        onTacStationNameButtonClicked = {
                            viewModel.setFluoStopName(it)
                        },
                        etape = uiState.ecran,
                        fluoStopsUiState = viewModel.fluoStopsUiState,
                        fluoLineStopsHoursUiState = viewModel.fluoLineStopsUiState,
                        direction = uiFluoState.direction,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(dimensionResource(R.dimen.padding_medium))
                    )

                }

                composable(route = MobilardennesScreen.Sncf.name) {
                    val context = LocalContext.current
                    lignes2 = false
                    iconText = R.drawable.ic_broken_image
                    etape = context.getString(R.string.sncf)
                    wicon = R.drawable.logo_sncf
                    Log.d("tagperso_2",uiSncfState.sncfStationId.toString())
                    Log.d("tagperso_2",uiSncfState.sncfSens.toString())

                    var colonnes: List<String> = listOf("sncf text")
                    var gares: MutableList<Gare> = mutableListOf(Gare())

                    // import de la liste des codes gares si ce n'est pas déjà fait
                    if (uiSncfState.sncfListeGaresOk == false) {
                        try {
                            val df_gares =
                                File(context.filesDir, "gares-de-voyageurs-corrected.csv")
                              var position = listOf(0f,0f)

                            df_gares.bufferedReader().useLines { lines ->
                                lines.forEachIndexed { index, ligne ->

                                    colonnes = ligne.split(";")

                                    if (((colonnes[4].length == 4) && (colonnes[4][0] == '8')) || ((colonnes[4].length == 5) && (colonnes[4].substring(0,2) == "75"))) {

                                        val colonneposition = colonnes[3].split(",")
                                        if (colonneposition.size >1)
                                        {
                                            position = listOf(colonneposition[0].toFloat(), colonneposition[1].toFloat())
                                        }
                                        else position = listOf(0f, 0f)

                                        gares.add(Gare(colonnes[0], colonnes[1], colonnes[2].split(";")[0], position[0], position[1], colonnes[4],  colonnes[5].split(";")[0] ))
                                }
                                }
                            }
                            // suppression des 2 premières lignes (ligne d'initialisation vide et ligne du nom original des colonnes)
                            gares.removeAt(0)
                            gares.removeAt(0)

                            viewModel.setSncfListGares(gares)
                            viewModel.setSncfListGareOk(true)
                            Log.d("liste gares", uiSncfState.sncfGares.toString())

                        } catch (e: Exception) {
                            Log.d("stop exception dataframe", e.toString())
                        }

                    }

                    viewModel.getArdennesSncfStation(uiSncfState.sncfSens.toString(), uiSncfState.sncfStationId.toString())

                    SncfScreen(
                        navController = navController,
                        sncfUiState = viewModel.sncfUiStateTrains,
                        onDropDownListClick = {
                            viewModel.setSncfStationSens(it)
                        },
                        onDropDownListClickUrl = { sens: String, station: String ->
                            viewModel.getArdennesSncfStation(sens, station)
                        },
                        onDropDownListClickGare = {
                            viewModel.setSncfStationId(it)
                        },
                        onSncfTest = {
                            viewModel.setSncfTest(it)
                        }
                        ,
                        uiSncfState = uiSncfState,
                        modifier = Modifier.fillMaxHeight()
                    )
                }

                composable(route = MobilardennesScreen.CyclamStation.name) {
                    val context = LocalContext.current
                    lignes2 = false
                    iconText = R.drawable.ic_broken_image
                    etape = context.getString(R.string.cyclam_station, uiState.cyclamStationName)
                    wicon = R.drawable.electric_bicycle

                    Log.d("uistate cyclamstationid", "${uiState.cyclamStationId} : ${uiState.cyclamStationName}")
                    viewModel.getArdennesCyclamStation(uiState.cyclamStationId)

                    CyclamStationScreen(
                        navController = navController,
                        ardennesUiStateVehicules = viewModel.ardennesUiStateVehicules,
                        stationId = uiState.cyclamStationId,
                        stationName = uiState.cyclamStationName,
                        modifier = Modifier.fillMaxHeight()
                    )
                }

                composable(route = MobilardennesScreen.Test.name) {
                    val context = LocalContext.current
                    lignes2 = false
                    iconText = R.drawable.ic_broken_image
                    etape = context.getString(R.string.test)
                    wicon = R.drawable.mobile_ardennes_foreground

                    TestScreenSimple(
                        chaine = stringResource(R.string.copyright)
                    )

                }
            }
        }
    }
}

@Composable
fun DrawerContent(
    scope: CoroutineScope,
    drawerstate: DrawerState,
    onCyclamButtonClicked: () -> Unit = {},
    onTacButtonClicked: () -> Unit = {},
    onSncfButtonClicked: () -> Unit = {},
    onCyclamMapButtonClicked: () -> Unit = {},
    onTacMapButtonClicked: () -> Unit = {},
    onTacStopButtonClicked: () -> Unit = {},
    onTestButtonClicked: () -> Unit = {},
    modifier: Modifier = Modifier)
{
    Text(
        text= stringResource(R.string.app_name),
        fontSize = 24.sp,
        modifier = Modifier.padding(16.dp)
    )
    HorizontalDivider()

    Spacer(modifier = Modifier.height(4.dp))

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.electric_bicycle),
                contentDescription = stringResource(R.string.cyclam),
                modifier = Modifier.size(26.dp)
            )
        },
        label = {
            Text(
                text= stringResource(R.string.cyclam),
                fontSize = 17.sp,
                )
        },
        selected = false,
        onClick = {
            scope.launch { drawerstate.close() }
            onCyclamButtonClicked()
        }
    )

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.electric_bicycle),
                contentDescription = stringResource(R.string.cyclam_map),
                modifier = Modifier.size(26.dp)
            )
        },
        label = {
            Text(
                text= stringResource(R.string.cyclam_map),
                fontSize = 17.sp,
                )
        },
        selected = false,
        onClick = {
            scope.launch { drawerstate.close() }
            onCyclamMapButtonClicked()
        }
    )

    Spacer(modifier = Modifier.height(4.dp))

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.logo_tac),
                contentDescription = stringResource(R.string.tac),
                modifier = Modifier.size(26.dp)
            )
        },
        label = {
            Text(
                text= stringResource(R.string.tac),
                fontSize = 17.sp,
                )
        },
        selected = false,
        onClick = {
            scope.launch { drawerstate.close() }
            onTacButtonClicked()
        }
    )

    Spacer(modifier = Modifier.height(4.dp))

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.logo_tac),
                contentDescription = stringResource(R.string.tac_map),
                modifier = Modifier.size(26.dp)
            )
        },
        label = {
            Text(
                text= stringResource(R.string.tac_map),
                fontSize = 17.sp,

                )
        },
        selected = false,
        onClick = {
            scope.launch { drawerstate.close() }
            onTacMapButtonClicked()

        }
    )

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.logo_tac),
                contentDescription = stringResource(R.string.tac_stop, "s", ""),
                modifier = Modifier.size(26.dp)
            )
        },
        label = {
            Text(
                text= stringResource(R.string.tac_stop, "s", ""),
                fontSize = 17.sp,

                )
        },
        selected = false,
        onClick = {
            scope.launch { drawerstate.close() }
            onTacStopButtonClicked()

        }
    )

    Spacer(modifier = Modifier.height(4.dp))

    NavigationDrawerItem(
        
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.logo_sncf),
                contentDescription = stringResource(R.string.sncf),
                modifier = Modifier.size(26.dp)
            )
        },
        label = {
            Text(
                text= stringResource(R.string.sncf),
                fontSize = 17.sp,
                )
        },
        selected = false,
        onClick = {
            scope.launch { drawerstate.close() }
            onSncfButtonClicked()
        }
    )

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.mobile_ardennes_foreground),
                contentDescription = stringResource(R.string.test),
                modifier = Modifier.size(26.dp)
            )
        },
        label = {
            Text(
                text= stringResource(R.string.test),
                fontSize = 17.sp,
                )
        },
        selected = false,
        onClick = {
            scope.launch { drawerstate.close() }
            onTestButtonClicked()
        }
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    lignes2: Boolean,
    iconText: Int,
    windowName: String,
    windowIcon: ImageVector,
    onOpenDrawer: () -> Unit,
    colorLine: Color =Color.Black
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.6f)
        ),
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                modifier = Modifier
                    .padding(start = 16.dp, end = 8.dp)
                    .size(28.dp)
                    .clickable {
                        onOpenDrawer()
                    }
            )
        },

        title =
            if (lignes2) {
                ->
                Column {
                    Text(
                        text = windowName.split("\n")[0],
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top=2.dp),
                        color = colorLine
                    )
                    Text(
                        text = windowName.split("\n")[1],
                        fontSize = 8.sp
                    )
                }
            }
            else if (iconText != R.drawable.ic_broken_image){
                    -> Image(
                        painter = painterResource(iconText),
                        contentDescription = null
                    )
                }

            //if (painterResource(iconText) != painterResource(R.drawable.ic_broken_image))

            else {-> Text(text = windowName)}

        ,
        actions = {
            Icon(
                imageVector = windowIcon ,//Icons.Default.Notifications,
                contentDescription = "Menu",
                modifier = Modifier
                    .size(30.dp)
            )
        }
    )
}

