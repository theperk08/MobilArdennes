package com.example.mobilardennes.network

import com.example.mobilardennes.model.SncfTrainData
import retrofit2.http.GET
import retrofit2.http.Path


interface SncfApiStationService {
    @GET("schedule-table/{sens}/{station}")
    suspend fun getSncf(
        @Path("sens") sens: String?,
        @Path("station") station: String?
    ): List<SncfTrainData>
}

interface SncfApiStationService2 {
   @GET("schedule-table/{sens}/{station}")
   suspend fun getSncf2(
        @Path("sens") sens: String?,
        @Path("station") station: String?
    ): String
}