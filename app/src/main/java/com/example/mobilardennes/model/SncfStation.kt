package com.example.mobilardennes.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class NestedSncfStation (
    var data: List<SncfTrainData>

)

@Serializable
data class SncfTrainData(
    val direction: String,
    val trainNumber: String,
    val scheduledTime: String,
    val actualTime: String,
    val trainType: String,
    val trainMode: String,
    val informationStatus: InfoStatus,
    val platform: Platform,
    val traffic: Traffic,
    val presentation: Presentation,
    val stops: List<String>?,
    val stationName: String?,
    val uic: String
)

@Serializable
data class InfoStatus(
    val trainStatus: String,
    val eventLevel: String,
    val delay: Int?,
)

@Serializable
data class Platform(
    val track: String?,
    val isTrackactive: Boolean?,
    val backgroundColor: String?,
)

@Serializable
data class Traffic(
    val origin: String,
    val destination: String,
    val oldOrigin: String,
    val oldDestination: String,
    val eventStatus: String?,
    val eventLevel: String?
)

@Serializable
data class Presentation(
    val colorCode: String,
    val textColorCode: String
)