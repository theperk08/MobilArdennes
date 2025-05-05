package com.example.mobilardennes.network

import com.example.mobilardennes.model.NestedCyclamStation
import com.example.mobilardennes.model.NestedCyclamStationVehicules
import com.example.mobilardennes.model.NestedCyclamVehicules
import retrofit2.http.GET
import retrofit2.http.Query


interface CyclamApiService {
    @GET("2024_04_03/stations")
    suspend fun getCyclam(
        @Query("limit") limit: Int,
        @Query("program") program: String
    ): NestedCyclamStation //List<NestedCyclamStation>
}


interface CyclamApiStationService {
    @GET("2024_04_03/vehicules")
    suspend fun getCyclamStation(
        @Query("dockless") dockless: String? = "false",
        @Query("program") program: String = "cyclam",
        @Query("station") station: String?,
    ): NestedCyclamStationVehicules
}

interface CyclamApiVehiculesService {
    @GET("2024_04_03/vehicules")
    suspend fun getCyclamVehicules(
        @Query("program") program: String = "cyclam",
        @Query("limit") limit: String = "100",
        @Query("status") status: String?=null // paramètre optionnel // status = all ou sinon status=destroyed, hs, lost, maintenance, maj, ready, service, stock
    ): NestedCyclamVehicules
}

// parametres acceptés pour vehicules:

// battery: type int correspondant à la value (pourcentage de batterie restante)
// battery_vae: type int correspondant à la value (pourcentage de batterie restante)
// chain_type: type string: checking, invalid, myself, null, valid
// connected: type boolean
// created: type integer : (time stamp unix à 10 chiffres)
// dockless: type boolean
// fw: type string (?? seulement "0173" ou "0174")
// lock: locked, unlocked, undefined
// model: type string: knot, titibike, x2
// program: type string
// rentable: type boolean
// score: type int
// station: type string
// status: type string : destroyed, hs, lost, maintenance, maj, ready, service, stock
// type: : type string : boat, classic, scooter, vae, vae_hybrid, vae_otgr, otgr_vae





// /!\ Pour obtenir détail d'un seul cyclam: /vehicules/vhcl_kTYWh8VBDTPwEuqFT8Tba6

// list des docks et véhicules rattachés
// https://api.cyclist.ecovelo.mobi/2024_04_03/docks?program=cyclam&limit=100

// https://api.cyclist.ecovelo.mobi/2024_04_03/programs


// https://api.cyclist.ecovelo.mobi/2024_04_03/trips/trip_nUssNYH9txs7QbBwTekbjM
// possible mais requiert login autorisation

