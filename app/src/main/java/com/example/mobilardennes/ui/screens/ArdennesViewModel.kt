package com.example.mobilardennes.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import com.example.mobilardennes.batteryPercentInterval
import com.example.mobilardennes.CyclamStationsApplication
import com.example.mobilardennes.data.CyclamStationsRepository
import com.example.mobilardennes.data.FluoTacInstantRepository
import com.example.mobilardennes.data.FluoTacItems
import com.example.mobilardennes.data.FluoTacRepository
import com.example.mobilardennes.data.Gare
import com.example.mobilardennes.data.NavigationItems
import com.example.mobilardennes.data.SncfItems
import com.example.mobilardennes.data.SncfRepository
import com.example.mobilardennes.data.SncfRepository2
import com.example.mobilardennes.data.StationBatteryItems
import com.example.mobilardennes.data.StationBatteryItems2
import com.example.mobilardennes.data.TestRepository
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
import com.example.mobilardennes.model.Stops
import com.example.mobilardennes.model.TacLines
import com.example.mobilardennes.model.TacStops
import com.example.mobilardennes.model.VehiculeData
import java.io.IOException
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readCSV

sealed interface ArdennesUiState {
    data class Success(val resultat: NestedCyclamStation) : ArdennesUiState
    object Error : ArdennesUiState
    object Loading : ArdennesUiState
}

sealed interface ArdennesUiStateVehicules {
    data class SuccessVehicules(val resultat: NestedCyclamStationVehicules) : ArdennesUiStateVehicules
    object Error : ArdennesUiStateVehicules
    object Loading : ArdennesUiStateVehicules
}

sealed interface ArdennesUiStateAllVehicules {
    data class SuccessAllVehicules(val resultat: NestedCyclamVehicules) : ArdennesUiStateAllVehicules
    object Error : ArdennesUiStateAllVehicules
    object Loading : ArdennesUiStateAllVehicules
}

sealed interface ArdennesUiStateAllVehiculesStatus {
    data class SuccessAllVehiculesStatus(val resultat: Map< String, NestedCyclamVehicules>) : ArdennesUiStateAllVehiculesStatus
    object Error : ArdennesUiStateAllVehiculesStatus
    object Loading : ArdennesUiStateAllVehiculesStatus
}

sealed interface SncfUiStateTrains {
    data class SuccessTrains(val resultat: List<SncfTrainData>) : SncfUiStateTrains
    object Error : SncfUiStateTrains
    object Loading : SncfUiStateTrains
}

sealed interface FluoUiState {
    data class SuccessFluo(val resultat: NestedFluoTac) : FluoUiState
    object Error : FluoUiState
    object Loading : FluoUiState
}

sealed interface FluoStopsUiState {
    data class SuccessFluoStops(val resultat: NestedFluoTacStops) : FluoStopsUiState
    object Error : FluoStopsUiState
    object Loading : FluoStopsUiState
}

sealed interface FluoStopsOperatorUiState {
    data class SuccessFluoStopsOperator(val resultat: NestedFluoStopsOperator) : FluoStopsOperatorUiState
    object Error : FluoStopsOperatorUiState
    object Loading : FluoStopsOperatorUiState
}

sealed interface FluoStopsHoursUiState {
    data class SuccessFluoStopsHours(val resultat: NestedStopsHours) : FluoStopsHoursUiState
    object Error : FluoStopsHoursUiState
    object Loading : FluoStopsHoursUiState
}

sealed interface FluoLineStopsHoursUiState {
    data class SuccessFluoLineStopsHours(val resultat: NestedLineStopsHours) : FluoLineStopsHoursUiState
    object Error : FluoLineStopsHoursUiState
    object Loading : FluoLineStopsHoursUiState
}

sealed interface FluoInstantStopsHoursUiState {
    data class SuccessFluoInstantStopsHours(val resultat: NestedStopsHoursInstant) : FluoInstantStopsHoursUiState
    object Error : FluoInstantStopsHoursUiState
    object Loading : FluoInstantStopsHoursUiState
}

sealed interface TestUiState {
    data class SuccessTest(val resultat: String) : TestUiState
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

    private val _uiState = MutableStateFlow(NavigationItems())
    val uiState: StateFlow<NavigationItems> = _uiState.asStateFlow()

    private val _uiSncfState = MutableStateFlow(SncfItems())
    val uiSncfState: StateFlow<SncfItems> = _uiSncfState.asStateFlow()

    private val _uiFluoState = MutableStateFlow(FluoTacItems())
    val uiFluoState: StateFlow<FluoTacItems> = _uiFluoState.asStateFlow()

    private val _uiFluoStateLines = MutableStateFlow(NestedFluoTac())
    val uiFluoStateLines: StateFlow<NestedFluoTac> = _uiFluoStateLines.asStateFlow()

    //private val _uiFluoStateStops = MutableStateFlow(NestedFluoTacStops())
    //val uiFluoStateStops: StateFlow<NestedFluoTacStops> = _uiFluoStateStops.asStateFlow()

    private val _uiFluoInstantStateStops = MutableStateFlow(NestedStopsHoursInstant())
    val uiFluoInstantStateStops: StateFlow<NestedStopsHoursInstant> = _uiFluoInstantStateStops.asStateFlow()


    init {
        //getArdennesCyclam()
        // stocke identifiant des stations et de leurs noms associés
    }

    fun getArdennesCyclam() {
        viewModelScope.launch {
            ardennesUiState = try {
                val listStations = cyclamStationsRepository.getCyclamStations()
                ArdennesUiState.Success(listStations)
            } catch (e: IOException) {
                Log.d("tag error", "getArdennesCyclam")
                ArdennesUiState.Error
            }
        }
    }


    fun getArdennesCyclamAllVehicules() {
        viewModelScope.launch {
            ardennesUiStateAllVehicules = try {
                val listVehicules = cyclamStationsRepository.getCyclamVehicules()
                ArdennesUiStateAllVehicules.SuccessAllVehicules(listVehicules)//listStations[0])
            } catch (e: IOException) {
                Log.d("tag error", "getArdennesCyclamAllVehicules")
                ArdennesUiStateAllVehicules.Error
            }
        }
    }

    fun getArdennesCyclamAllVehiculesStatus(liste: List<String>) {
        viewModelScope.launch {
            ardennesUiStateAllVehiculesStatus = try {
                ArdennesUiStateAllVehiculesStatus.SuccessAllVehiculesStatus(liste.map{it to cyclamStationsRepository.getCyclamVehiculesStatus(it)}.toMap())//listStations[0])
            } catch (e: IOException) {
                Log.d("tag error", "getArdennesCyclamAllVehiculesStatus")
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


    fun getArdennesCyclamStation(station: String?) {
        viewModelScope.launch {
            ardennesUiStateVehicules = try {
                val listStation = cyclamStationsRepository.getCyclamStationUnique(station)
                ArdennesUiStateVehicules.SuccessVehicules(listStation)
            } catch (e: IOException) {
                Log.d("tag error", "getArdennesCyclamStation")
                ArdennesUiStateVehicules.Error
            }
        }
    }


    fun getArdennesSncfStation(sens: String?, station: String?) {
        viewModelScope.launch {
            sncfUiStateTrains = try {
                val listStation = sncfRepository.getSncfTrains(sens, station)
                SncfUiStateTrains.SuccessTrains(listStation)
            } catch (e: IOException) {
                Log.d("tag error", "getArdennesSncfStation")
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
                val listStations = fluoRepository.getTacLines()
                setFluoAllLines(mapOf("Data" to listStations.Data))
                FluoUiState.SuccessFluo(listStations)
            } catch (e: IOException) {
                Log.d("tag error", "getTacLines")
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

    fun setTacListStops(stops: List<Stops>) {
        _uiFluoState.update { currentState ->
            currentState.copy(
                tacListStops = stops.sortedWith(compareBy({it.Name}, {it.Id})).toList()
            )
        }
    }

    fun getTacLinesStops(lineId: Int?) {
        viewModelScope.launch {
            fluoStopsUiState = try {
                val listLineStops = fluoRepository.getTacLineStops(lineId)
                FluoStopsUiState.SuccessFluoStops(listLineStops)
            } catch (e: IOException) {
                Log.d("tag error", "getTacLinesStops")
                FluoStopsUiState.Error
            }
        }
    }

    fun getTacLineStopsHours(lineId: Int?, direction: Int?, datetime: String?) {
        viewModelScope.launch {
            fluoLineStopsUiState = try {
                val listLineStopsHours = fluoRepository.getTacLineStopsHours(lineId, direction, datetime)
                FluoLineStopsHoursUiState.SuccessFluoLineStopsHours(listLineStopsHours)
            } catch (e: IOException) {
                Log.d("tag error", "getTacLineStopsHours")
                FluoLineStopsHoursUiState.Error
            }
        }
    }

    fun getFluoStopsOperator(operator: Int?) {
        viewModelScope.launch {
            fluoStopsOperatorUiState = try {
                val listStopsOperator = fluoRepository.getFluoStopOperator(operator)
                FluoStopsOperatorUiState.SuccessFluoStopsOperator(listStopsOperator)

            } catch (e: IOException) {
                Log.d("tag error", "getFluoStopsOperator")
                FluoStopsOperatorUiState.Error
            }
        }
    }


    fun getFluoStopsHours2(stopid: Int?) {
        viewModelScope.launch {
            fluoStopsHoursUiState = try {
                FluoStopsHoursUiState.SuccessFluoStopsHours(fluoRepository.getFluoStopHours(stopid))

            } catch (e: IOException) {
                Log.d("tag error", "getFluoStopsHours2")
                FluoStopsHoursUiState.Error
            }
        }
    }

    fun getFluoInstantStopsHours(stopid: Int?) {
        viewModelScope.launch {
            fluoInstantStopsHoursUiState = try {
                val listStopsHours = fluoInstantRepository.getFluoInstantStopHours(stopid)
                setFluoInstantStopHoursStopPoint(mapOf("data" to listStopsHours))
                FluoInstantStopsHoursUiState.SuccessFluoInstantStopsHours(fluoInstantRepository.getFluoInstantStopHours(stopid))
            } catch (e: IOException) {
                Log.d("tag error", "getFluoInstantStopsHours")
                FluoInstantStopsHoursUiState.Error
            }
        }
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