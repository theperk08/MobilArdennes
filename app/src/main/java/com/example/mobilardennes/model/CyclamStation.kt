package com.example.mobilardennes.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class NestedCyclamStation (
    var data: List<CyclamData>

)

@Serializable
data class CyclamData(
    @SerialName("id") val stationId: String,
    val name: String,
    val position: Position,
    val statistics: Statistics,
    val vehicules: Vehicules

)

@Serializable
data class Position (
    val latitude: String,
    val longitude: String
)

@Serializable
data class Statistics (
    val docks: Docks
)

@Serializable
data class Docks (
    val free: Int
)

@Serializable
data class Vehicules (
    val total : Int,
    val data: List<String>
)


// station précise
@Serializable
data class NestedCyclamStationVehicules (
    var data: List<VehiculeData>
)

@Serializable
data class VehiculeData (
    val id: String="",
    val number : Int=0,
    val station: String?="",
    val status: String?="",
    val battery_vae: BatteryVae=BatteryVae(0,0)
)

@Serializable
data class BatteryVae (
    val percent : Int,
    val remaining_distance: Int
)


@Serializable
data class NestedCyclamVehicules (
    var data: List<VehiculeData>
)