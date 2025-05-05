package com.example.mobilardennes

import android.app.Application
import androidx.core.graphics.toColorInt
import com.example.mobilardennes.data.AppContainer
import com.example.mobilardennes.data.DefaultAppContainer

class CyclamStationsApplication: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}

fun couleur(nb: Int): Int {
    val coul = when (nb) {
        0 -> R.color.red
        1 -> R.color.orange
        2 -> R.color.turquoise
        3 -> R.color.dodger_blue
        else -> R.color.blue
    }
    return coul
}

fun couleurString(nb: Int): String{
    val coul = when (nb) {
        0 -> "#FF0000"
        1 -> "#EE9900"
        2 -> "#00CBFE"
        3 -> "#3366CC"
        else -> "#0000FF"
    }
    return coul
}

fun couleurHexa(nb: Int): Int{
    val coul = when (nb) {
        0 -> "#FF0000".toColorInt()
        1 -> "#EE9900".toColorInt()
        2 -> "#00CBFE".toColorInt()
        3 -> "#3366CC".toColorInt()
        else -> "#0000FF".toColorInt()
    }
    return coul
}


fun couleurBatteryPercent(nb: Int): Int {
    val coul = when (nb) {
        in 0..5 -> R.color.red
        in 6..24 -> R.color.orange
        in 25..49 -> R.color.turquoise
        in 50..74 -> R.color.dodger_blue
        else -> R.color.blue
    }
    return coul
}

fun batteryPercentInterval(nb: Int): Int {
    val inter = when(nb) {
        in 0..5 -> 0
        in 6..24 -> 1
        in 25..49 -> 2
        in 50..74 -> 3
        else -> 4
    }
    return inter
}

fun imageBatteryPercent(nb: Int): Int {
    val im = when (nb) {
        in 0..5 -> R.drawable.battery0
        in 6..24 -> R.drawable.battery1
        in 25..49 -> R.drawable.battery2
        in 50..74 -> R.drawable.battery3
        else -> R.drawable.battery4
    }
    return im
}

fun popupStationCyclam(nbVelos: Int, nbPlaces: Int): String
{
    var velos = if (nbVelos > 1) {"${nbVelos} vélos"} else {"${nbVelos} vélo "}
    var places = if (nbPlaces > 1) {"${nbPlaces} emplacements"} else {"${nbPlaces} emplacement "}
    val html = "${velos} \n ${places}"
    return html
}

fun sncfTime(chaine: String): String {
    // pour extraire l'heure HH:MM du format de date-heure complet
    var ch = ""
    if (chaine.length == 25) {
        ch = chaine.subSequence(startIndex = 11, endIndex = 16).toString()
    }
    return ch
}

fun horairesTac(heure: Int?): String {
    var hr = if (heure != null) {
        if (heure > -1)
            (heure / 60).toInt().toString().padStart(2, '0') + ":" + (heure % 60).toString().padStart(2, '0')
        else
            "?"
    }
    else "?"

    return hr
}


