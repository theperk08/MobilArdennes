package com.example.mobilardennes.data


import com.example.mobilardennes.model.NestedCyclamVehicules
import com.example.mobilardennes.model.Stops
import com.example.mobilardennes.model.VehiculeData

// Create Navigation Items Class to Select Unselect items
data class NavigationItems(
    var ecran: Int = 0,
    var cyclamStationName: String? = "",
    var cyclamStationId: String? = "",
    val cyclamStationsList: List<StationBatteryItems> = listOf(StationBatteryItems()),//MutableMap <String, String>? = mutableMapOf("test" to "test")
    var cyclamVehiculesStatus: NestedCyclamVehicules = NestedCyclamVehicules(listOf(VehiculeData()))
)

data class CyclamVehiculesStatus(
    var cyclamVehicules: List<NestedCyclamVehicules>
)

data class StationBatteryItems(
    val cyclamStationId: String? = "",
    val batList: Map <Int, Int> = mapOf(0 to 0),
    var maxBat: Int = 0
)

data class StationBatteryItems2(
    val cyclamStationId: String? = "",
    val batList: MutableMap <Int, Int> = mutableMapOf(0 to 0),
    var maxBat: Int = 0
)

data class SncfItems(
    var sncfStationId: String? = "0087172007",
    var sncfSens: String? = "Departures",
    var sncfOrigin: String? = "Destination",
    var sncfStationName: String? = "Charleville-Mézières",
    var sncfListeGaresOk: Boolean? = false,
    var sncfTest: String? = "",
    var sncfGares: List<Gare> = listOf(Gare("", "", "", 0f,0f, "",""))
)

data class Gare(
    val nom: String? = "",
    val trigramme: String? = "",
    val segments: String? = "",
    val lat: Float? = 0f,
    val lon: Float? = 0f,
    val codeCommune: String? = "",
    val codeUic: String? = ""
)

data class FluoTacItems(
    var lineId: Int? = 19642,
    var direction: Int? = 1,
    var lineName: String? = "",
    var lineDirectionName: String? = "",
    var lineCode: String? = "",
    var lineColor: String? = "01BBF3",
    var stopId: Int = 901915,
    var stopName: String? ="Macé",
    var stopLines: List<Int>? = listOf(19642),
    var stopLinesChecked: List<Boolean> = listOf(false),
    var lineNameSelected: String = "",
    var tacListStopsOk: Boolean? = false,
    var tacListStops: List<Stops> = listOf(Stops(0, 0f, 0f, "",0, "")),
)



