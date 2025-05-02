package com.example.mobilardennes.network

import com.example.mobilardennes.model.NestedCyclamStation
import com.example.mobilardennes.model.NestedFluoStopsOperator
import com.example.mobilardennes.model.NestedFluoTac
import com.example.mobilardennes.model.NestedFluoTacStops
import com.example.mobilardennes.model.NestedLineStopsHours
import com.example.mobilardennes.model.NestedStopsHours
import com.example.mobilardennes.model.NestedStopsHoursInstant
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap


interface FluoApiService {
    @Headers("User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1)")
    @GET("api/transport/v3/{line}/{getlines}/json")
    // @GET("photos")
    suspend fun getFluoLine(
        @Path("line") line: String,
        @Path("getlines") getlines: String,
        @Query("NetworkIds") networkid: Int,
        // @QueryMap
        //@Query("program") program: String
    ): NestedFluoTac //List<NestedCyclamStation>

}
interface FluoStopsApiService {
    @Headers("User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1)")
    @GET("api/transport/v3/{line}/{getlines}/json")
    suspend fun getFluoLineStops(
        @Path("line") line: String,
        @Path("getlines") getlines: String,
        @Query("LineId") lineid: Int?,
        //@Query("program") program: String
    ): NestedFluoTacStops //List<NestedCyclamStation>
}

interface FluoStopsOperatorService {
    @Headers("User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1)")
    @GET("api/transport/v3/{line}/{getlines}/json")
    suspend fun getFluoStopsOperator(
        @Path("line") line: String,
        @Path("getlines") getlines: String,
        @Query("OperatorIds") operatorid: Int?,

    ): NestedFluoStopsOperator
}

interface FluoStopsHoursService {
    @Headers("User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1)")
    @GET("api/transport/v3/timetable/{getstops}/json")
    suspend fun getFluoStopsHours(
        @Path("getstops") getstops: String,
        @Query("StopIds") stopid: Int?=null,
        @Query("LineId") lineid: Int?=null,
        @Query("Direction") direction: Int?=null

        ): NestedStopsHours

}

interface FluoLineStopsHoursService {
    @Headers("User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1)")
    @GET("api/transport/v3/timetable/{getstops}/json")
    suspend fun getFluoLineStopsHours(
        @Path("getstops") getstops: String,
        @Query("StopIds") stopid: Int?=null,
        @Query("LineId") lineid: Int?=null,
        @Query("Direction") direction: Int?=null,
        @Query("DateTime") datetime: String?=null

    ): NestedLineStopsHours

}


interface FluoInstantStopsHoursService {
    @Headers("User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1)")
    @GET("api/v1/stopPoints/{getstops}/schedules")
    suspend fun getFluoStopsHoursInstant(
        @Path("getstops") getstops: Int?,
        //@Query("StopIds") stopid: Int?,

        ): NestedStopsHoursInstant

}
