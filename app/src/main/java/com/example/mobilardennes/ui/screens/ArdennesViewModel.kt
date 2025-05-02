package com.example.mobilardennes.ui.screens
// import com.example.mobilardennes.network.CyclamApi
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
// import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilardennes.CyclamStationsApplication
import com.example.mobilardennes.data.CyclamStationsRepository
import kotlinx.coroutines.launch
import java.io.IOException
import com.example.mobilardennes.data.NetworkCyclamStationsRepository
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mobilardennes.MobilardennesScreen
import com.example.mobilardennes.batteryPercentInterval
import com.example.mobilardennes.data.FluoTacInstantRepository
import com.example.mobilardennes.data.FluoTacItems
import com.example.mobilardennes.data.FluoTacRepository
import com.example.mobilardennes.data.Gare
import com.example.mobilardennes.data.NavigationItems
import com.example.mobilardennes.data.NetworkTestRepository
import com.example.mobilardennes.data.SncfItems
import com.example.mobilardennes.data.SncfRepository
import com.example.mobilardennes.data.SncfRepository2
import com.example.mobilardennes.data.StationBatteryItems
import com.example.mobilardennes.data.StationBatteryItems2
import com.example.mobilardennes.data.TestRepository
import com.example.mobilardennes.model.CyclamData
import com.example.mobilardennes.model.Lines
import com.example.mobilardennes.model.NestedCyclamStation
import com.example.mobilardennes.model.NestedCyclamStationVehicules
import com.example.mobilardennes.model.NestedCyclamVehicules
import com.example.mobilardennes.model.NestedFluoStopsOperator
import com.example.mobilardennes.model.NestedFluoTac
import com.example.mobilardennes.model.NestedFluoTacStops
import com.example.mobilardennes.model.NestedLineStopsHours
import com.example.mobilardennes.model.NestedStopsHours
import com.example.mobilardennes.model.NestedStopsHoursInstant
import com.example.mobilardennes.model.SncfTrainData
import com.example.mobilardennes.model.StopPoint
import com.example.mobilardennes.model.Stops
import com.example.mobilardennes.model.TacLines
import com.example.mobilardennes.model.TacStops
import com.example.mobilardennes.model.VehiculeData
import com.example.mobilardennes.network.TestApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.io.readCSV

import java.io.File
import java.io.InputStreamReader


sealed interface ArdennesUiState {
    data class Success(val resultat: NestedCyclamStation) : ArdennesUiState
    //data class SuccessVehicules(val resultat: NestedCyclamStationVehicules) : ArdennesUiState
    object Error : ArdennesUiState
    object Loading : ArdennesUiState
}


sealed interface ArdennesUiStateVehicules {
    //data class Success(val resultat: NestedCyclamStation) : ArdennesUiState
    data class SuccessVehicules(val resultat: NestedCyclamStationVehicules) : ArdennesUiStateVehicules
    object Error : ArdennesUiStateVehicules
    object Loading : ArdennesUiStateVehicules
}

sealed interface ArdennesUiStateAllVehicules {
    //data class Success(val resultat: NestedCyclamStation) : ArdennesUiState
    data class SuccessAllVehicules(val resultat: NestedCyclamVehicules) : ArdennesUiStateAllVehicules
    object Error : ArdennesUiStateAllVehicules
    object Loading : ArdennesUiStateAllVehicules
}

sealed interface ArdennesUiStateAllVehiculesStatus {
    //data class Success(val resultat: NestedCyclamStation) : ArdennesUiState
    data class SuccessAllVehiculesStatus(val resultat: Map< String, NestedCyclamVehicules>) : ArdennesUiStateAllVehiculesStatus
    object Error : ArdennesUiStateAllVehiculesStatus
    object Loading : ArdennesUiStateAllVehiculesStatus
}

sealed interface SncfUiStateTrains {
    //data class Success(val resultat: NestedCyclamStation) : ArdennesUiState
    data class SuccessTrains(val resultat: List<SncfTrainData>) : SncfUiStateTrains
    data class SuccessTrains2(val resultat: String) : SncfUiStateTrains
    object Error : SncfUiStateTrains
    object Loading : SncfUiStateTrains
}



sealed interface FluoUiState {
    //data class Success(val resultat: NestedCyclamStation) : ArdennesUiState
    data class SuccessFluo(val resultat: NestedFluoTac) : FluoUiState
    // data class SuccessFluoStops(val resultat: NestedFluoTacStops) : FluoUiState
    object Error : FluoUiState
    object Loading : FluoUiState
}

sealed interface FluoStopsUiState {
    //data class Success(val resultat: NestedCyclamStation) : ArdennesUiState
    // data class SuccessFluo(val resultat: NestedFluoTac) : FluoUiState
    data class SuccessFluoStops(val resultat: NestedFluoTacStops) : FluoStopsUiState
    object Error : FluoStopsUiState
    object Loading : FluoStopsUiState
}

sealed interface FluoStopsOperatorUiState {
    //data class Success(val resultat: NestedCyclamStation) : ArdennesUiState
    // data class SuccessFluo(val resultat: NestedFluoTac) : FluoUiState
    data class SuccessFluoStopsOperator(val resultat: NestedFluoStopsOperator) : FluoStopsOperatorUiState
    object Error : FluoStopsOperatorUiState
    object Loading : FluoStopsOperatorUiState
}

sealed interface FluoStopsHoursUiState {
    //data class Success(val resultat: NestedCyclamStation) : ArdennesUiState
    // data class SuccessFluo(val resultat: NestedFluoTac) : FluoUiState
    data class SuccessFluoStopsHours(val resultat: NestedStopsHours) : FluoStopsHoursUiState
    object Error : FluoStopsHoursUiState
    object Loading : FluoStopsHoursUiState
}

sealed interface FluoLineStopsHoursUiState {
    //data class Success(val resultat: NestedCyclamStation) : ArdennesUiState
    // data class SuccessFluo(val resultat: NestedFluoTac) : FluoUiState
    data class SuccessFluoLineStopsHours(val resultat: NestedLineStopsHours) : FluoLineStopsHoursUiState
    object Error : FluoLineStopsHoursUiState
    object Loading : FluoLineStopsHoursUiState
}

sealed interface FluoInstantStopsHoursUiState {
    //data class Success(val resultat: NestedCyclamStation) : ArdennesUiState
    // data class SuccessFluo(val resultat: NestedFluoTac) : FluoUiState
    data class SuccessFluoInstantStopsHours(val resultat: NestedStopsHoursInstant) : FluoInstantStopsHoursUiState
    object Error : FluoInstantStopsHoursUiState
    object Loading : FluoInstantStopsHoursUiState
}

sealed interface TestUiState {
    data class SuccessTest(val resultat: String) : TestUiState
    //data class SuccessVehicules(val resultat: NestedCyclamStationVehicules) : ArdennesUiState
    object Error : TestUiState
    object Loading : TestUiState
}

class ArdennesViewModel(
    private val cyclamStationsRepository: CyclamStationsRepository,
    private val sncfRepository: SncfRepository,
    private val sncfRepository2: SncfRepository2,
    private val fluoRepository: FluoTacRepository,
    private val fluoInstantRepository: FluoTacInstantRepository,
    private val testRepository: TestRepository,
): ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var ardennesUiState: ArdennesUiState by mutableStateOf(ArdennesUiState.Loading)
        private set

    var ardennesUiStateVehicules: ArdennesUiStateVehicules by mutableStateOf(ArdennesUiStateVehicules.Loading)
        private set

    var ardennesUiStateAllVehicules: ArdennesUiStateAllVehicules by mutableStateOf(ArdennesUiStateAllVehicules.Loading)
        private set

    var ardennesUiStateAllVehiculesStatus: ArdennesUiStateAllVehiculesStatus by mutableStateOf(ArdennesUiStateAllVehiculesStatus.Loading)
        private set

    var sncfUiStateTrains: SncfUiStateTrains by mutableStateOf(SncfUiStateTrains.Loading)
        private set

    var fluoUiState: FluoUiState by mutableStateOf(FluoUiState.Loading)
        private set

    var fluoStopsUiState: FluoStopsUiState by mutableStateOf(FluoStopsUiState.Loading)
        private set

    var fluoLineStopsUiState: FluoLineStopsHoursUiState by mutableStateOf(FluoLineStopsHoursUiState.Loading)
        private set

    var fluoStopsOperatorUiState: FluoStopsOperatorUiState by mutableStateOf(FluoStopsOperatorUiState.Loading)
        private set

    var fluoStopsHoursUiState: FluoStopsHoursUiState by mutableStateOf(FluoStopsHoursUiState.Loading)
        private set

    var fluoInstantStopsHoursUiState: FluoInstantStopsHoursUiState by mutableStateOf(FluoInstantStopsHoursUiState.Loading)
        private set

    var testUiState: TestUiState by mutableStateOf(TestUiState.Loading)
        private set

    private val _uiState = MutableStateFlow(NavigationItems())
    val uiState: StateFlow<NavigationItems> = _uiState.asStateFlow()

    private val _uiSncfState = MutableStateFlow(SncfItems())
    val uiSncfState: StateFlow<SncfItems> = _uiSncfState.asStateFlow()

    private val _uiFluoState = MutableStateFlow(FluoTacItems())
    val uiFluoState: StateFlow<FluoTacItems> = _uiFluoState.asStateFlow()


    private val _uiFluoStateLines = MutableStateFlow(NestedFluoTac())
    val uiFluoStateLines: StateFlow<NestedFluoTac> = _uiFluoStateLines.asStateFlow()

    private val _uiFluoStateStops = MutableStateFlow(NestedFluoTacStops())
    val uiFluoStateStops: StateFlow<NestedFluoTacStops> = _uiFluoStateStops.asStateFlow()

    private val _uiFluoInstantStateStops = MutableStateFlow(NestedStopsHoursInstant())
    val uiFluoInstantStateStops: StateFlow<NestedStopsHoursInstant> = _uiFluoInstantStateStops.asStateFlow()




    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */

    init {



        //getArdennesCyclam()
        // stocke identifiant des stations et de leurs noms associés
        //allCyclamStations(ardennesUiState)

        //Log.d("initialisation cyclam", _uiState.collectAsState())

    }

    /*
     * Gets Mars photos information from the Mars API Retrofit service and updates the
     * [MarsPhoto] [List] [MutableList].
     */
    fun getArdennesCyclam() {
        viewModelScope.launch {
            ardennesUiState = try {
                Log.d("tagperso1", "getArdennesSncfStation:0 ")
                val listStations = cyclamStationsRepository.getCyclamStations()
                Log.d("tagperso1", "getArdennesSncfStation:1 ")
                //val listResult =
                //    CyclamApi.retrofitService.getCyclam(limit = 100, program = "cyclam")
                // ArdennesUiState.Success(listResult)
                // ArdennesUiState.Success("Success: station : ${listStations[0].name}, nb vélos dispos : ${listStations[0].vehicules.total}")
                ArdennesUiState.Success(listStations)//listStations[0])
            } catch (e: IOException) {
                Log.d("tagperso1", "getArdennesSncfStation:2")
                ArdennesUiState.Error

            }
        }
    }



    /*fun allCyclamStations(cyclamUiState: ArdennesUiState)
    {
        when (cyclamUiState) {
            // is ArdennesUiState.Success -> ResultScreen(ardennesUiState.resultat, modifier.padding(top = contentPadding.calculateTopPadding()))
            is ArdennesUiState.Success -> getCyclamStations(cyclamUiState.resultat)
            // modifier = modifier.fillMaxWidth()
            else -> ""


        }

    }
*/

    /*fun getCyclamStations(stations : NestedCyclamStation) {
       val listStations: MutableList<StationBatteryItems> = mutableListOf(StationBatteryItems())// MutableMap<String, String> = mutableMapOf("test" to "test")

       stations.data.forEach{
           listStations.add(StationBatteryItems("${it.name}", "${it.stationId}",  mutableMapOf(0 to 0),     0

           )) //"${it.stationId}"]="${it.name}"
       }

        /*_uiState.update { currentState ->
            currentState.copy(
                cyclamStationsList = listStations


            )
        }*/
    }
*/


    fun getArdennesCyclamAllVehicules() {
        viewModelScope.launch {
            ardennesUiStateAllVehicules = try {
                Log.d("tagperso1", "getArdennesAllVehicules:0 ")
                val listVehicules = cyclamStationsRepository.getCyclamVehicules()
                Log.d("tagperso1", "getArdennesAllVehicules:1 ")
                //val listResult =
                //    CyclamApi.retrofitService.getCyclam(limit = 100, program = "cyclam")
                // ArdennesUiState.Success(listResult)
                // ArdennesUiState.Success("Success: station : ${listStations[0].name}, nb vélos dispos : ${listStations[0].vehicules.total}")
                ArdennesUiStateAllVehicules.SuccessAllVehicules(listVehicules)//listStations[0])
            } catch (e: IOException) {
                Log.d("tagperso1", "getArdennesAllVehicules:2")
                ArdennesUiStateAllVehicules.Error

            }
        }
    }

    fun getArdennesCyclamAllVehiculesStatus(liste: List<String>) {
        viewModelScope.launch {
            ardennesUiStateAllVehiculesStatus = try {
                /*Log.d("tagperso1", "getArdennesAllVehicules:0 ")
                val listVehiculesMaintenance = cyclamStationsRepository.getCyclamVehiculesStatus("maintenance")
                val listVehiculesService = cyclamStationsRepository.getCyclamVehiculesStatus("service")
                val listVehiculesLost = cyclamStationsRepository.getCyclamVehiculesStatus("lost")
*/
                Log.d("tagperso1", "getArdennesAllVehicules:1 ")
                //val listResult =
                //    CyclamApi.retrofitService.getCyclam(limit = 100, program = "cyclam")
                // ArdennesUiState.Success(listResult)
                // ArdennesUiState.Success("Success: station : ${listStations[0].name}, nb vélos dispos : ${listStations[0].vehicules.total}")

                //ArdennesUiStateAllVehiculesStatus.SuccessAllVehiculesStatus(mapOf("maintenance" to listVehiculesMaintenance, "service" to listVehiculesService, "lost" to listVehiculesLost))//listStations[0])
                ArdennesUiStateAllVehiculesStatus.SuccessAllVehiculesStatus(liste.map{it to cyclamStationsRepository.getCyclamVehiculesStatus(it)}.toMap())//listStations[0])

            } catch (e: IOException) {
                Log.d("tagperso1", "getArdennesAllVehicules:2")
                ArdennesUiStateAllVehiculesStatus.Error

            }
        }
    }


    fun clearCyclamVehiculesStatus() {
        _uiState.update { currentState ->
            currentState.copy(
                cyclamVehiculesStatus = NestedCyclamVehicules(listOf(VehiculeData()))


            )
        }
    }

    fun addCyclamVehiculesStatus(vehicules: List<VehiculeData>) {
        _uiState.update { currentState ->
            currentState.copy(
                cyclamVehiculesStatus = NestedCyclamVehicules(vehicules)


            )
        }
    }






    fun allCyclamVehicules(ardennesUiStateAllVehicules: ArdennesUiStateAllVehicules)
    {
        when (ardennesUiStateAllVehicules) {
            // is ArdennesUiState.Success -> ResultScreen(ardennesUiState.resultat, modifier.padding(top = contentPadding.calculateTopPadding()))
            is ArdennesUiStateAllVehicules.SuccessAllVehicules -> getCyclamAllVehicules(ardennesUiStateAllVehicules.resultat)
            // modifier = modifier.fillMaxWidth()
            else -> ""


        }

    }

    fun getCyclamAllVehicules(vehicules: NestedCyclamVehicules): MutableList<StationBatteryItems2> {
        val listStations: MutableList<StationBatteryItems2> = mutableListOf(StationBatteryItems2())// MutableMap<String, String> = mutableMapOf("test" to "test")
        var nbVehicules = 0
        var maxBatteryVae = 0
        val batInterval0 = mutableMapOf(
            0 to 0,
            1 to 0,
            2 to 0,
            3 to 0,
            4 to 0)



        vehicules.data.forEach {
            val station = it.station
            val foundStation = listStations.filter { stationf -> stationf.cyclamStationId == station }
            if ( foundStation.size>0) {
                foundStation[0].batList[batteryPercentInterval(it.battery_vae.percent)] = foundStation[0].batList[batteryPercentInterval(it.battery_vae.percent)]!! + 1


                if (it.battery_vae.percent > foundStation[0].maxBat) {
                    foundStation[0].maxBat = it.battery_vae.percent
                }
            }
            else {
                var batInterval = mutableMapOf(
                    0 to 0,
                    1 to 0,
                    2 to 0,
                    3 to 0,
                    4 to 0)
                batInterval[batteryPercentInterval(it.battery_vae.percent)] = batInterval[batteryPercentInterval(it.battery_vae.percent)]!! + 1
                listStations.add(StationBatteryItems2(it.station, batInterval, it.battery_vae.percent))

            }
        }


        /*_uiState.update { currentState ->
            currentState.copy(
                cyclamStationsList = listStations


            )
        }*/
        return listStations
    }

    /*List<StationBatteryItems> = listOf(StationBatteryItems())//MutableMap <String, String>? = mutableMapOf("test" to "test")
)

data class StationBatteryItems(
    //val cyclamStationName: String? = "",
    val cyclamStationId: String? = "",
    val batList: Map <Int, Int> = mapOf(0 to 0),
    var maxBat: Int = 0

)

     */

    fun getArdennesCyclamComplet() {
        viewModelScope.launch {
            ardennesUiState = try {
                Log.d("tagperso1", "getArdennesSncfStation:0 ")
                val listStations = cyclamStationsRepository.getCyclamStations()
                Log.d("tagperso1", "getArdennesSncfStation:1 ")
                //val listResult =
                //    CyclamApi.retrofitService.getCyclam(limit = 100, program = "cyclam")
                // ArdennesUiState.Success(listResult)
                // ArdennesUiState.Success("Success: station : ${listStations[0].name}, nb vélos dispos : ${listStations[0].vehicules.total}")
                ArdennesUiState.Success(listStations)//listStations[0])
            } catch (e: IOException) {
                Log.d("tagperso1", "getArdennesSncfStation:2")
                ArdennesUiState.Error

            }
        }
    }

    fun setCyclamStation(station: Map<String, String>) {
        _uiState.update { currentState ->
            currentState.copy(
                cyclamStationName = station["stationName"]


            )
        }
            _uiState.update {currentState ->
                currentState.copy(
                    cyclamStationId = station["stationId"]
                )
            }

    }

    fun getCyclamBatteryList() //listStationBattery: MutableList<StationBatteryItems2>)
    {

        _uiState.update { currentState ->
            currentState.copy(
                cyclamStationsList = listOf(StationBatteryItems()) //"""stationName"]


            )
        }
    }



    fun getArdennesCyclamStation(station: String?) {
        viewModelScope.launch {
            ardennesUiStateVehicules = try {

                val listStation = cyclamStationsRepository.getCyclamStationUnique(station)

                //val listResult =
                //    CyclamApi.retrofitService.getCyclam(limit = 100, program = "cyclam")
                // ArdennesUiState.Success(listResult)
                // ArdennesUiState.Success("Success: station : ${listStations[0].name}, nb vélos dispos : ${listStations[0].vehicules.total}")
                ArdennesUiStateVehicules.SuccessVehicules(listStation)//listStations[0])
            } catch (e: IOException) {
                ArdennesUiStateVehicules.Error

            }
        }
    }


    fun getArdennesSncfStation(sens: String?, station: String?) {
        viewModelScope.launch {
            sncfUiStateTrains = try {

                Log.d("tagperso", "getArdennesSncfStation: ")

                val listStation = sncfRepository.getSncfTrains(sens, station)
                // val listStation2 = sncfRepository2.getSncfTrains2(sens, station)
                Log.d("tagperso2","getArdennesSncfStation: ")

                //val listResult =
                //    CyclamApi.retrofitService.getCyclam(limit = 100, program = "cyclam")
                // ArdennesUiState.Success(listResult)
                // ArdennesUiState.Success("Success: station : ${listStations[0].name}, nb vélos dispos : ${listStations[0].vehicules.total}")
                SncfUiStateTrains.SuccessTrains(listStation)//listStations[0])
            } catch (e: IOException) {
                val TAG = "tagperso3"
                Log.d(TAG, "getArdennesSncfStation: ")
                SncfUiStateTrains.Error

            }
        }
    }



    fun setSncfStationId(station: Map<String, String>) {
        _uiSncfState.update { currentState ->
            currentState.copy(
                sncfStationId = station["stationId"]
            )
        }
        _uiSncfState.update { currentState ->
            currentState.copy(
                sncfStationName = station["stationName"]
            )
        }
    }

    fun setSncfStationSens(sens: Map<String, String>) {

        _uiSncfState.update {currentState ->
            currentState.copy(
                sncfSens = sens["sens"]
            )
        }
        _uiSncfState.update {currentState ->
            currentState.copy(
                sncfOrigin = sens["origin"]
            )
        }

    }


    fun setSncfTest(chaine: String) {
        _uiSncfState.update { currentState ->
            currentState.copy(
                sncfTest = chaine
            )
        }
    }
    fun setSncfListGareOk(ok: Boolean) {
        _uiSncfState.update { currentState ->
            currentState.copy(
                sncfListeGaresOk = ok
            )
        }
    }

    fun setSncfListGares(gares: MutableList<Gare>) {
        _uiSncfState.update { currentState ->
            currentState.copy(
                sncfGares = gares.toList()
            )
        }

    }



    fun getTacLines(line: String) {
        viewModelScope.launch {
            fluoUiState = try {
                Log.d("tagperso1", "getArdennesfluo:0 ")
                val listStations = fluoRepository.getTacLines()
                setFluoAllLines(mapOf("Data" to listStations.Data))
                Log.d("tagperso1", "getArdennesfluo:1 ")
                //val listResult =
                //    CyclamApi.retrofitService.getCyclam(limit = 100, program = "cyclam")
                // ArdennesUiState.Success(listResult)
                // ArdennesUiState.Success("Success: station : ${listStations[0].name}, nb vélos dispos : ${listStations[0].vehicules.total}")
                FluoUiState.SuccessFluo(listStations)//listStations[0])
            } catch (e: IOException) {
                Log.d("tagperso1", "getArdennesSncfStation:2")
                FluoUiState.Error

            }
        }
    }

    fun setFluoLine(line: Map<String, Int>) {
        _uiFluoState.update { currentState ->
            currentState.copy(
                lineId = line["lineId"],
                direction = line["direction"]


            )
        }
    }



    fun setFluoLineSelected(line: String) {
        _uiFluoState.update { currentState ->
            currentState.copy(
                lineNameSelected = line
            )
        }
    }

    fun setFluoAllLines(lines: Map<String, List<TacLines>?>) {
        _uiFluoStateLines.update { currentState ->
            currentState.copy(
                Data = lines["Data"]


            )
        }
    }

    fun setFluoLineColor(line: Map<String, String>) {
        _uiFluoState.update { currentState ->
            currentState.copy(
                lineColor = line["lineColor"],
                lineCode = line["lineCode"],
                lineName = line["lineName"],
                lineDirectionName = line["lineDirectionName"]


            )
        }
    }


    fun setFluoInstantStopHoursStopPoint(stopHours: Map<String, NestedStopsHoursInstant>) {
        _uiFluoInstantStateStops.update { currentState ->
            currentState.copy(
                stopPoint = stopHours["data"]!!.stopPoint,
                lines = stopHours["data"]!!.lines,
                schedules = stopHours["data"]!!.schedules
            )
        }
    }


    fun setFluoStop(stop: Map<String, Int>) {

            _uiFluoState.update { currentState ->
                currentState.copy(
                    stopId = stop["stopId"] ?: 901915


                )
            }

    }

    fun setFluoAllStops(stops: Map<String, TacStops>) {
        _uiFluoStateStops.update { currentState ->
            currentState.copy(
                Data = stops["TacStops"]


            )
        }
    }

    fun setFluoStopName(stop: Map<String, String>) {
        _uiFluoState.update { currentState ->
            currentState.copy(
                stopName = stop["stopName"]

            )
        }
    }

    fun setTacListStopsOk(ok: Boolean) {
        _uiFluoState.update { currentState ->
            currentState.copy(
                tacListStopsOk = ok
            )
        }
    }

    fun setTacListStops(stops: List<Stops>) {// stops: MutableList<Stops>) {

        _uiFluoState.update { currentState ->
            currentState.copy(
                tacListStops = stops.sortedWith(compareBy({it.Name}, {it.Id})).toList()
            )
        }

    }

    fun getTacLinesStops(lineId: Int?) {
        viewModelScope.launch {
            fluoStopsUiState = try {
                Log.d("tagperso1", "getArdennesfluo:3 ")
                val listLineStops = fluoRepository.getTacLineStops(lineId)
                Log.d("tagperso1", listLineStops.Data?.LineId.toString())

                //val listResult =
                //    CyclamApi.retrofitService.getCyclam(limit = 100, program = "cyclam")
                // ArdennesUiState.Success(listResult)
                // ArdennesUiState.Success("Success: station : ${listStations[0].name}, nb vélos dispos : ${listStations[0].vehicules.total}")
                FluoStopsUiState.SuccessFluoStops(listLineStops)//listStations[0])


            } catch (e: IOException) {
                Log.d("tagperso1", "getTacLinesStops:5")
                FluoStopsUiState.Error

            }
        }
    }

    fun getTacLineStopsHours(lineId: Int?, direction: Int?, datetime: String?) {
        viewModelScope.launch {
            fluoLineStopsUiState = try {
                Log.d("tagperso1", "getArdennesfluo:3 ")
                val listLineStopsHours = fluoRepository.getTacLineStopsHours(lineId, direction, datetime)
                //Log.d("tagperso1", listLineStopsHours.Data?.LineId.toString())

                //val listResult =
                //    CyclamApi.retrofitService.getCyclam(limit = 100, program = "cyclam")
                // ArdennesUiState.Success(listResult)
                // ArdennesUiState.Success("Success: station : ${listStations[0].name}, nb vélos dispos : ${listStations[0].vehicules.total}")
                FluoLineStopsHoursUiState.SuccessFluoLineStopsHours(listLineStopsHours)//listStations[0])


            } catch (e: IOException) {
                Log.d("tagperso1", "getArdennesSncfStation:5")
                FluoLineStopsHoursUiState.Error

            }
        }
    }

    fun getFluoStopsOperator(operator: Int?) {

        viewModelScope.launch {
            fluoStopsOperatorUiState = try {
                Log.d("tagperso1", "getArdennesfluo:3 ")
                val listStopsOperator = fluoRepository.getFluoStopOperator(operator)
                Log.d("tagperso1", listStopsOperator.Data.size.toString())

                //val listResult =
                //    CyclamApi.retrofitService.getCyclam(limit = 100, program = "cyclam")
                // ArdennesUiState.Success(listResult)
                // ArdennesUiState.Success("Success: station : ${listStations[0].name}, nb vélos dispos : ${listStations[0].vehicules.total}")
                //FluoStopsUiState.SuccessFluoStops(listLineStops)//listStations[0])
                FluoStopsOperatorUiState.SuccessFluoStopsOperator(listStopsOperator)

            } catch (e: IOException) {
                Log.d("tagperso1", "getArdennesSncfStation:5")
                FluoStopsOperatorUiState.Error

            }
        }
    }

    fun getFluoStopsHours(stopid: Int?) {

        viewModelScope.launch {
            fluoStopsHoursUiState = try {
                val listStopsHours = fluoRepository.getFluoStopHours(stopid)
                Log.d("tagperso stop hours", listStopsHours.Data.Hours.size.toString())

                //val listResult =
                //    CyclamApi.retrofitService.getCyclam(limit = 100, program = "cyclam")
                // ArdennesUiState.Success(listResult)
                // ArdennesUiState.Success("Success: station : ${listStations[0].name}, nb vélos dispos : ${listStations[0].vehicules.total}")
                //FluoStopsUiState.SuccessFluoStops(listLineStops)//listStations[0])
                FluoStopsHoursUiState.SuccessFluoStopsHours(listStopsHours)

            } catch (e: IOException) {
                Log.d("tagperso1", "getArdennesSncfStation:5")
                FluoStopsHoursUiState.Error

            }
        }
    }

    fun getFluoStopsHours2(stopid: Int?) {

        viewModelScope.launch {
            fluoStopsHoursUiState = try {
                //val listStopsHours2 = fluoRepository.getFluoStopHours2(stopid)
                //val listStopsHours = fluoRepository.getFluoStopHours(stopid)

                //Log.d("tagperso stop hours", listStopsHours.Data.Hours.size.toString())

                //val listResult =
                //    CyclamApi.retrofitService.getCyclam(limit = 100, program = "cyclam")
                // ArdennesUiState.Success(listResult)
                // ArdennesUiState.Success("Success: station : ${listStations[0].name}, nb vélos dispos : ${listStations[0].vehicules.total}")
                //FluoStopsUiState.SuccessFluoStops(listLineStops)//listStations[0])
                FluoStopsHoursUiState.SuccessFluoStopsHours(fluoRepository.getFluoStopHours(stopid))

            } catch (e: IOException) {
                Log.d("stop hours", "get fluo stops hours:5")
                FluoStopsHoursUiState.Error

            }
        }
    }

    fun getFluoInstantStopsHours(stopid: Int?) {

        viewModelScope.launch {
            fluoInstantStopsHoursUiState = try {
                //val listStopsHours2 = fluoRepository.getFluoStopHours2(stopid)
                val listStopsHours = fluoInstantRepository.getFluoInstantStopHours(stopid)
                setFluoInstantStopHoursStopPoint(mapOf("data" to listStopsHours))

                //Log.d("tagperso stop hours", listStopsHours.Data.Hours.size.toString())

                //val listResult =
                //    CyclamApi.retrofitService.getCyclam(limit = 100, program = "cyclam")
                // ArdennesUiState.Success(listResult)
                // ArdennesUiState.Success("Success: station : ${listStations[0].name}, nb vélos dispos : ${listStations[0].vehicules.total}")
                //FluoStopsUiState.SuccessFluoStops(listLineStops)//listStations[0])
                FluoInstantStopsHoursUiState.SuccessFluoInstantStopsHours(fluoInstantRepository.getFluoInstantStopHours(stopid))

            } catch (e: IOException) {
                Log.d("stop hours", "get fluo stops hours:5")
                FluoInstantStopsHoursUiState.Error

            }
        }
    }

    fun testExample1() {
        viewModelScope.launch {
            testUiState = try {
                Log.d("tagperso1", "getArdennesSncfStation:0 ")
                val resultTest = testRepository.getTest1(chaine = "fr") //"schedule-table/Departures/0087172007") //"search")//"previsions-meteo-par-ville.html")
                Log.d("tagperso1", "getArdennesSncfStation:1 ")
                //val listResult =
                //    CyclamApi.retrofitService.getCyclam(limit = 100, program = "cyclam")
                // ArdennesUiState.Success(listResult)
                // ArdennesUiState.Success("Success: station : ${listStations[0].name}, nb vélos dispos : ${listStations[0].vehicules.total}")
                TestUiState.SuccessTest(resultTest)//listStations[0])
            } catch (e: IOException) {
                Log.d("tagperso1", "getArdennesSncfStation:2")
                TestUiState.Error

            }
        }
    }


    fun readDataFile(filename: String): DataFrame<*>{
        // https://kotlin.github.io/dataframe/read.html
        val df = DataFrame.readCSV(
            filename,
            delimiter = ';',
            //header = listOf("A", "B", "C", "D"),
            //parserOptions = ParserOptions(nullStrings = setOf("not assigned")),
        )
        return df

    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as CyclamStationsApplication)
                val cyclamStationsRepository = application.container.cyclamStationsRepository
                val sncfRepository = application.container.sncfRepository
                val sncfRepository2 = application.container.sncfRepository2
                val fluoRepository = application.container.fluoLinesRepository
                val testRepository = application.container.testRepository
                val fluoInstantRepository = application.container.fluoInstantLinesRepository
                ArdennesViewModel(
                    cyclamStationsRepository = cyclamStationsRepository,
                    sncfRepository =  sncfRepository,
                sncfRepository2 = sncfRepository2,
                fluoRepository = fluoRepository,
                    testRepository = testRepository,
                    fluoInstantRepository = fluoInstantRepository
                )
            }
        }
    }


}