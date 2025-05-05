package com.example.mobilardennes.model

import android.graphics.Color
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.mobilardennes.batteryPercentInterval
import com.example.mobilardennes.couleur
import com.example.mobilardennes.couleurHexa
import com.example.mobilardennes.horairesTac
import com.example.mobilardennes.MobilardennesScreen
import com.example.mobilardennes.R
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import org.osmdroid.views.overlay.Marker


class CustomMarkerInfoWindow(
    mView: MapView,
    navController: NavController,
    onCyclamStationButtonClicked:  (Map<String, String>) -> Unit,
    maxBatteryPercent: Int,
    station: CyclamData
    ) : MarkerInfoWindow(R.layout.custom_info_window, mView) {
        val ocb = onCyclamStationButtonClicked
        val nav = navController
        val station = station
        val maxBatteryPercent = maxBatteryPercent

    override fun onOpen(item: Any?,
                        ) {
        val marker = item as Marker

        // Accéder aux éléments du layout personnalisé
        val titleTextView = mView.findViewById<TextView>(R.id.info_window_title)
        //val descriptionTextView = mView.findViewById<TextView>(R.id.info_window_description)
        val moreInfoButton = mView.findViewById<Button>(R.id.info_window_button)

        // Définir les valeurs dynamiques basées sur le marqueur
        titleTextView.text = marker.title
        titleTextView.textSize = 20f
        // titleTextView.setTextColor("0xFFFF0000".toInt()) : marche pas

        moreInfoButton.setText(marker.snippet)
        moreInfoButton.setBackgroundColor(couleurHexa(station.vehicules.total))
        moreInfoButton.setBackgroundColor(couleurHexa(batteryPercentInterval(maxBatteryPercent)))
        Log.d("couleur1", couleurHexa(station.vehicules.total).toString())
        moreInfoButton.setTextColor( "#ffffff".toColorInt())
        Log.d("couleur2", couleur(station.vehicules.total).toString())

        // Ajouter un comportement au clic sur le bouton
        moreInfoButton.setOnClickListener {
            // Exemple : afficher un Toast ou naviguer vers une autre activité
            // Toast.makeText(mMapView.context, "Plus d'infos sur ${marker.title}", Toast.LENGTH_SHORT).show()
            ocb(
                mapOf(
                    "stationName" to station.name,
                    "stationId" to station.stationId
                )
            )
            nav.navigate(MobilardennesScreen.CyclamStation.name)
            close() // Fermer la fenêtre après l'action
        }
    }

    override fun onClose() {
        // Actions à effectuer lors de la fermeture de l'InfoWindow (si nécessaire)
    }
}


class CustomMarkerTacInfoWindow(
    alllines: Boolean = false,
    mView: MapView,
    navController: NavController,
    onTacStationButtonClicked:  (Map<String, Int>) -> Unit,
    onTacStationNameButtonClicked: (Map<String, String>) -> Unit,
    station: Stops,
    lineColor: String?="ff0000",
    horaireOk: Int?=0
) : MarkerInfoWindow(R.layout.custom_info_window, mView) {
    val setTacStop = onTacStationButtonClicked
    val setTacStopName = onTacStationNameButtonClicked
    val nav = navController
    val station = station
    val lineColor = lineColor
    val alllines = alllines
    val horaire = horaireOk

    override fun onOpen(item: Any?,
    ) {
        val marker = item as Marker

        // Accéder aux éléments du layout personnalisé
        val titleTextView = mView.findViewById<TextView>(R.id.info_window_title)
        val descriptionTextView = mView.findViewById<TextView>(R.id.info_window_description)
        val moreInfoButton = mView.findViewById<Button>(R.id.info_window_button)

        // Définir les valeurs dynamiques basées sur le marqueur
        titleTextView.text = marker.title
        titleTextView.textSize = 20f

        if (alllines) {titleTextView.setTextColor(Color.BLACK)} else {
            titleTextView.setTextColor(Color.WHITE)
            titleTextView.setBackgroundColor(Color.parseColor("#" + lineColor))
            moreInfoButton.text = horairesTac(horaire)
        }
        descriptionTextView.text = marker.snippet

        // Ajouter un comportement au clic sur le bouton
        moreInfoButton.setOnClickListener{
            setTacStop(
                mapOf(
                    "stopId" to station.Id
                )
            )
            setTacStopName(
                mapOf(
                    "stopName" to station.Name
                )
            )
        Log.d("stop id : ", station.Id.toString())
            nav.navigate(MobilardennesScreen.TacStop.name)
            close()
        }
    }

    override fun onClose() {
        // Actions à effectuer lors de la fermeture de l'InfoWindow (si nécessaire)
    }
}
