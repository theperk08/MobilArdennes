package com.example.mobilardennes.data

import com.example.mobilardennes.model.NestedCyclamStation
import com.example.mobilardennes.model.NestedCyclamStationVehicules
import com.example.mobilardennes.model.NestedCyclamVehicules
import com.example.mobilardennes.network.CyclamApiService
import com.example.mobilardennes.network.CyclamApiStationService
import com.example.mobilardennes.network.CyclamApiVehiculesService


interface CyclamStationsRepository {
    suspend fun getCyclamStations(): NestedCyclamStation //List<CyclamData>
    suspend fun getCyclamStationUnique(station: String?): NestedCyclamStationVehicules
    suspend fun getCyclamVehicules(): NestedCyclamVehicules
    suspend fun getCyclamVehiculesStatus(status: String?): NestedCyclamVehicules
}

class NetworkCyclamStationsRepository(
    private val cyclamApiService: CyclamApiService,
    private val cyclamApiStationService : CyclamApiStationService,
    private val cyclamApiVehiculesService: CyclamApiVehiculesService

) : CyclamStationsRepository {
    override suspend fun getCyclamStations(): NestedCyclamStation =cyclamApiService.getCyclam(limit = 100, program = "cyclam")
    override suspend fun getCyclamStationUnique(station: String?): NestedCyclamStationVehicules = cyclamApiStationService.getCyclamStation(station=station)
    override suspend fun getCyclamVehicules(): NestedCyclamVehicules = cyclamApiVehiculesService.getCyclamVehicules()
    override suspend fun getCyclamVehiculesStatus(status: String?): NestedCyclamVehicules = cyclamApiVehiculesService.getCyclamVehicules(status=status)
    }







