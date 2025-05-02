package com.example.mobilardennes.model

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
//import androidx.compose.ui.graphics.Color
import android.graphics.Color
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import com.example.mobilardennes.MainActivity
import com.example.mobilardennes.MobilArdennesApp
import com.example.mobilardennes.MobilardennesScreen
import com.example.mobilardennes.R
import com.example.mobilardennes.backgroundColor
import com.example.mobilardennes.batteryPercentInterval
import com.example.mobilardennes.couleur
import com.example.mobilardennes.couleurHexa
import com.example.mobilardennes.horairesTac
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow


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
                        //onCyclamStationButtonClicked: (Map<String, String>) -> Unit,
                        //navController: NavController
                        ) {
        val marker = item as Marker


        // Accéder aux éléments du layout personnalisé
        val titleTextView = mView.findViewById<TextView>(R.id.info_window_title)
        //val descriptionTextView = mView.findViewById<TextView>(R.id.info_window_description)
        val moreInfoButton = mView.findViewById<Button>(R.id.info_window_button)
        val layoutView = mView.findViewById<LinearLayout>(R.id.layout_title)

        // Définir les valeurs dynamiques basées sur le marqueur
        titleTextView.text = marker.title
        titleTextView.textSize = 20f
        // titleTextView.setTextColor("0xFFFF0000".toInt()) : marche pas

        //descriptionTextView.text = marker.snippet
        //descriptionTextView.setBackgroundResource(backgroundColor(station.vehicules.total)) // R.drawable.color_blue.toDrawable() //

        //moreInfoButton.text = marker.snippet
        moreInfoButton.setText(marker.snippet)
        //moreInfoButton.setBackgroundResource(backgroundColor(station.vehicules.total))
        moreInfoButton.setBackgroundColor(couleurHexa(station.vehicules.total))
        moreInfoButton.setBackgroundColor(couleurHexa(batteryPercentInterval(maxBatteryPercent)))
        //moreInfoButton.setBackgroundColor(Color.BLUE)
        Log.d("couleur1", couleurHexa(station.vehicules.total).toString())
        moreInfoButton.setTextColor(if (station.vehicules.total in 1..3) "#000000".toColorInt() else "#ffffff".toColorInt())
        Log.d("couleur2", couleur(station.vehicules.total).toString())

        //layoutView.background = couleur(station.vehicules.total).toDrawable()//R.drawable.color_blue.toDrawable()

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
        //onCyclamStationButtonClicked: (Map<String, String>) -> Unit,
        //navController: NavController
    ) {
        val marker = item as Marker


        // Accéder aux éléments du layout personnalisé
        val titleTextView = mView.findViewById<TextView>(R.id.info_window_title)
        val descriptionTextView = mView.findViewById<TextView>(R.id.info_window_description)
        val moreInfoButton = mView.findViewById<Button>(R.id.info_window_button)
        val layoutView = mView.findViewById<LinearLayout>(R.id.layout_title)

        // Définir les valeurs dynamiques basées sur le marqueur
        titleTextView.text = marker.title
        titleTextView.textSize = 20f
        // titleTextView.setTextColor("0xFFFF0000".toInt()) : marche pas

        if (alllines) {titleTextView.setTextColor(Color.BLACK)} else {
            titleTextView.setTextColor(Color.WHITE)
            titleTextView.setBackgroundColor(Color.parseColor("#" + lineColor))
            moreInfoButton.text = horairesTac(horaire)

        }
        descriptionTextView.text = marker.snippet


        //descriptionTextView.setBackgroundResource(backgroundColor(station.vehicules.total)) // R.drawable.color_blue.toDrawable() //


        // moreInfoButton.text = marker.snippet
        //moreInfoButton.setBackgroundResource(backgroundColor(station.vehicules.total))
        //moreInfoButton.setBackgroundColor(couleurHexa(station.vehicules.total))
        //moreInfoButton.setBackgroundColor(Color.BLUE)
        //Log.d("couleur1", couleurHexa(station.vehicules.total).toString())
        //moreInfoButton.setTextColor(if (station.vehicules.total in 1..3) "#000000".toColorInt() else "#ffffff".toColorInt())
        //Log.d("couleur2", couleur(station.vehicules.total).toString())

        //layoutView.background = couleur(station.vehicules.total).toDrawable()//R.drawable.color_blue.toDrawable()

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
        /*moreInfoButton.setOnClickListener {
            // Exemple : afficher un Toast ou naviguer vers une autre activité
            // Toast.makeText(mMapView.context, "Plus d'infos sur ${marker.title}", Toast.LENGTH_SHORT).show()
            ocb(
                mapOf(
                    "stopId" to station.Id,
                    //"stationId" to station.stationId
                )
            )
            //nav.navigate(MobilardennesScreen.CyclamStation.name)
            close() // Fermer la fenêtre après l'action
        }*/
    }

    override fun onClose() {
        // Actions à effectuer lors de la fermeture de l'InfoWindow (si nécessaire)
    }
}
