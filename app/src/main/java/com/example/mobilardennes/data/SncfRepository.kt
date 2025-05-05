package com.example.mobilardennes.data

import android.util.Log
import com.example.mobilardennes.model.InfoStatus
import com.example.mobilardennes.model.Platform
import com.example.mobilardennes.model.Presentation
import com.example.mobilardennes.model.SncfTrainData
import com.example.mobilardennes.model.Traffic
import com.example.mobilardennes.network.SncfApiStationService

interface SncfRepository {
    suspend fun getSncfTrains(sens: String?, station: String?): List<SncfTrainData>
}


class NetworkSncfRepository(
    private val sncfApiStationService : SncfApiStationService,

    ) : SncfRepository {
      override suspend fun getSncfTrains(sens: String?, station: String?): List<SncfTrainData> =
        try {
            Log.d("stop exception sens", sens.toString())
            Log.d("stop exception station", station.toString())
            sncfApiStationService.getSncf(sens=sens, station=station)
        }
        catch (e: Exception) {
            Log.d("stop exception repo", e.toString())
            listOf(SncfTrainData("","","","", "","",
                InfoStatus("","",0),
                Platform("", false, ""),
                Traffic("","","","","",""),
                Presentation("",""),
                listOf(""),
                "",
                ""
                ))
        }
}

