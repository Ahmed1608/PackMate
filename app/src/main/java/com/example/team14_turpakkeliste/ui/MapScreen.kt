package com.example.team14_turpakkeliste.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.example.team14_turpakkeliste.BottomNavBar
import com.example.team14_turpakkeliste.R
import com.example.team14_turpakkeliste.ui.theme.ForestGreen
import com.example.team14_turpakkeliste.ui.theme.Team14TurPakkeListeTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


@Composable
fun MapScreen(navController: NavController) {

    Column {
        DisplayMap()
    }
    Column(modifier = Modifier
        .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ){
        BottomNavBar(navController)
    }

}

@Composable
fun DisplayMap() {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = R.id.map_view
        }
    }
    // Remember the GoogleMap instance
    val googleMap = remember {
        mutableStateOf<GoogleMap?>(null)
    }

    val clickedLatLng = remember {
        mutableStateOf<LatLng?>(null)
    }

    AndroidView({ mapView }) { view ->
        mapView.onCreate(null)
        mapView.onResume()
        // Get the GoogleMap instance if it's not already stored
        if (googleMap.value == null) {
            mapView.getMapAsync { map ->
                val norway = LatLng(62.943669, 9.917546)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(norway, 5f))
                map.addMarker(MarkerOptions().position(LatLng(59.297573, 10.420644)))
                map.setOnMapClickListener { latLng ->
                    clickedLatLng.value = latLng
                    // Call the API with the clicked LatLng here
                    Log.d("", clickedLatLng.value.toString())
                }
            }
        }
    }
}







