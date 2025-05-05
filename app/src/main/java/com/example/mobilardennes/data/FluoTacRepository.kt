package com.example.mobilardennes.data


import android.util.Log
import com.example.mobilardennes.model.Directions
import com.example.mobilardennes.model.Hours
import com.example.mobilardennes.model.HoursLineStops
import com.example.mobilardennes.model.HoursStops
import com.example.mobilardennes.model.Lines
import com.example.mobilardennes.model.LineStops
import com.example.mobilardennes.model.NestedFluoStopsOperator
import com.example.mobilardennes.model.NestedFluoTac
import com.example.mobilardennes.model.NestedFluoTacStops
import com.example.mobilardennes.model.NestedLineStopsHours
import com.example.mobilardennes.model.NestedStopsHours
import com.example.mobilardennes.model.NestedStopsHoursInstant
import com.example.mobilardennes.model.Schedules
import com.example.mobilardennes.model.StopPoint
import com.example.mobilardennes.model.Terminus
import com.example.mobilardennes.model.TimeStops
import com.example.mobilardennes.network.FluoApiService
import com.example.mobilardennes.network.FluoInstantStopsHoursService
import com.example.mobilardennes.network.FluoLineStopsHoursService
import com.example.mobilardennes.network.FluoStopsApiService
import com.example.mobilardennes.network.FluoStopsHoursService
import com.example.mobilardennes.network.FluoStopsOperatorService


interface FluoTacRepository {
    suspend fun getTacLines(): NestedFluoTac
    suspend fun getTacLineStops(lineid: Int?): NestedFluoTacStops
    suspend fun getFluoStopOperator(operator: Int?): NestedFluoStopsOperator
    suspend fun getFluoStopHours(stopid: Int?): NestedStopsHours
    suspend fun getTacLineStopsHours(lineid: Int?, direction: Int?, datetime: String?): NestedLineStopsHours
}

interface FluoTacInstantRepository {
    suspend fun getFluoInstantStopHours(stopid: Int?): NestedStopsHoursInstant
}


class NetworkFluoTacRepository(
    private val fluoApiService: FluoApiService,
    private val fluoStopsApiService: FluoStopsApiService,
    private val fluoStopsOperatorService: FluoStopsOperatorService,
    private val fluoStopsHoursService: FluoStopsHoursService,
    private val fluoLineStopsHoursService: FluoLineStopsHoursService

) : FluoTacRepository {
    override suspend fun getTacLines(): NestedFluoTac  = fluoApiService.getFluoLine(line = "line", getlines = "GetLines", networkid = 75)
    override suspend fun getTacLineStops(lineid: Int?): NestedFluoTacStops = fluoStopsApiService.getFluoLineStops(line="stop", getlines = "GetLineStopsOrder", lineid=lineid)
    override suspend fun getFluoStopOperator(operator: Int?): NestedFluoStopsOperator = fluoStopsOperatorService.getFluoStopsOperator(line="stop", getlines = "GetStops", operatorid = operator)
    override suspend fun getFluoStopHours(stopid: Int?): NestedStopsHours =
         try {
             Log.d("stop hours id", stopid.toString())
            fluoStopsHoursService.getFluoStopsHours(getstops = "GetNextStopHoursForStops",
                stopid = stopid)
        } catch (e: Exception) {
            Log.d("stop exception repo", e.toString())
            NestedStopsHours(
                HoursStops(listOf(Hours(0,0,false,false, 0,0,0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                ) )))
         }

    override suspend fun getTacLineStopsHours(lineid: Int?, direction: Int?, datetime: String?): NestedLineStopsHours =
        try {
            Log.d("line id", lineid.toString())
            fluoLineStopsHoursService.getFluoLineStopsHours(getstops = "GetLineHours",
                lineid = lineid, direction = direction, datetime= datetime)
        } catch (e: Exception) {
            Log.d("stop exception lineid direction", e.toString())
            NestedLineStopsHours(
                HoursLineStops(listOf(Hours(0,0,false,false,0,0,0,0,0,0,0,0,0,0,0,0)),
            listOf(
                LineStops(0,0,0,0,)
            )
            )
            )
        }
    }

     class NetworkFluoTacInstantRepository(
        private val fluoInstantStopsHoursService: FluoInstantStopsHoursService,

    ) : FluoTacInstantRepository {
        override suspend fun getFluoInstantStopHours(stopid: Int?): NestedStopsHoursInstant =
            try {
                Log.d("stop hours id", stopid.toString())
                fluoInstantStopsHoursService.getFluoStopsHoursInstant(getstops = stopid)
            } catch (e: Exception) {
                Log.d("stop exception repo", e.toString())
                NestedStopsHoursInstant(
                    StopPoint("","",0f, 0f),
                    listOf(Lines("","","","", listOf(Directions("","")))),
                    listOf(Schedules("","", Terminus(""), listOf(TimeStops(""))))
                    )
            }
    }



