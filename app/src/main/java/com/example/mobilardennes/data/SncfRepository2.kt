package com.example.mobilardennes.data

import android.util.Log
import com.example.mobilardennes.network.SncfApiStationService2


interface SncfRepository2 {
    suspend fun getSncfTrains2(sens: String?, station: String?): String
}


class NetworkSncfRepository2(
    private val sncfApiStationService2 : SncfApiStationService2,

    ) : SncfRepository2 {
        override suspend fun getSncfTrains2(sens: String?, station: String?): String =
        try {
            Log.d("stop exception sens", sens.toString())
            Log.d("stop exception station", station.toString())
            sncfApiStationService2.getSncf2(sens=sens, station=station)
        }
        catch (e: Exception) {
            Log.d("stop exception repo", e.toString())
            "nothing !"
        }
}
