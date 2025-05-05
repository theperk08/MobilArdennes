package com.example.mobilardennes.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class NestedFluoTac (
    var Data: List<TacLines> ?= listOf(TacLines(Company("",0,""),0, listOf(LineDirection(0,"")),"","", Networks("",0,""),"", 0))
)

@Serializable
data class TacLines(
    val Company: Company,
    val Id: Int,
    val LineDirections: List<LineDirection>,
    val Name: String,
    val Number: String,
    val Network: Networks,
    val Color: String,
    val Order: Int
)

@Serializable
data class LineDirection (
    val Direction: Int,
    val Name: String
)

@Serializable
data class Company (
    val Code: String,
    val Id: Int,
    val Name: String
)

@Serializable
data class Networks (
    val Code : String,
    val Id: Int,
    val Name: String
)

@Serializable
data class NestedFluoTacStops (
    var Data: TacStops ?= TacStops(0, listOf(StopDirections(LineDirection(0,""),0, listOf(Stops(0,0f,0f, "",0,"")))))
)

@Serializable
data class TacStops (
    var LineId: Int,
    var StopDirections: List<StopDirections>
)

@Serializable
data class StopDirections (
    val Direction: LineDirection,
    val DirectionId: Int,
    val Stops: List<Stops>
)

@Serializable
data class Stops (
    val Id: Int,
    val Latitude: Float,
    val Longitude: Float,
    val Name: String,
    val Order: Int,
    val Code: String
)

@Serializable
data class NestedFluoStopsOperator (
    var Data: List<Stops>
)

@Serializable
data class FluoStop (
    val Id: Int,
    val Latitude: Float,
    val Longitude: Float,
    val Name: String,
    val Code: String
)

// Horaires des Stops
@Serializable
data class NestedStopsHours (
    var Data: HoursStops
)

@Serializable
data class NestedLineStopsHours(
    var Data: HoursLineStops = HoursLineStops(listOf(Hours(0,0,false,false,0,0,0,0,0,0,0,0,0,0,0,0)),
        listOf(LineStops(0,0,0,0,)
        )
    )
)

@Serializable
data class HoursLineStops (
    var Hours: List<Hours>,
    var LineStops: List<LineStops>
)

@Serializable
data class LineStops (
    var Direction: Int,
    var LineId: Int,
    var Order: Int,
    var StopId: Int
)

@Serializable
data class HoursStops (
    var Hours: List<Hours>
)

@Serializable
data class Hours (
    val AimedArrivalTime: Int?,
    val AimedDepartureTime: Int?,
    val IsCancelled: Boolean?,
    val IsDisrupted: Boolean?,
    val LineId: Int?,
    val Order: Int?,
    val PredictedArrivalTime: Int?,
    val PredictedDepartureTime: Int?,
    val RealArrivalTime: Int?,
    val RealDepartureTime: Int?,
    val RealTimeStatus: Int?,
    val Restriction: Int?,
    val StopId: Int?,
    val TheoricArrivalTime: Int?,
    val TheoricDepartureTime: Int?,
    val VehicleJourneyId: Int?
)

@Serializable
data class NestedStopsHoursInstant(
    val stopPoint: StopPoint = StopPoint("","",0f, 0f),
    val lines: List<Lines> = listOf(Lines("","","","", listOf(Directions("","")))),
    val schedules: List<Schedules> = listOf(Schedules("","", Terminus(""), listOf(TimeStops(""))))
)

@Serializable
data class StopPoint(
    @SerialName("id") val stopPointId: String?,
    @SerialName("name") val stopPointName: String?,
    @SerialName("lat") val stopPointLat: Float?,
    @SerialName("lon") val stopPointLon: Float?,
)

@Serializable
data class Lines(
    @SerialName("id") val lineId: String?,
    @SerialName("color") val lineColor: String?,
    @SerialName("textColor") val lineTextColor: String?,
    @SerialName("lName") val lineName: String?,
    val directions: List<Directions>,
)

@Serializable
data class Directions(
    val direction: String?,
    val display: String?,
)

@Serializable
data class ListTimeStops(
    var listetimestops: List<TimeStops>
)

@Serializable
data class Schedules(
    val lineId: String?,
    val direction: String?,
    val terminus: Terminus,
    val nextStops: List<TimeStops>
): Comparable<Schedules> {
    override fun compareTo(other: Schedules)= compareValuesBy(this, other,
        {it.nextStops[0].nextStopTime},
        //{it.nextStops[0].nextStopTime}
    )
}

@Serializable
data class Terminus(
    @SerialName("name") val terminusName: String?
)
@Serializable
data class ListeTimeStops(
    val listetimestops: List<TimeStops>
): Comparable<ListeTimeStops> {
    override fun compareTo(other: ListeTimeStops)= compareValuesBy(this, other,
        {it.listetimestops[0].nextStopTime},
        //{it.listetimestops[0].nextStopTime}
    )
}

@Serializable
data class TimeStops(
    @SerialName("time") val nextStopTime: String?
)

data class LineChecked(
    val lineName: String,
    val completed: Boolean=false
)
