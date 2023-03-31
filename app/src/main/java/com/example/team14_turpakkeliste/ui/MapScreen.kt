package com.example.team14_turpakkeliste.ui

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.team14_turpakkeliste.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.team14_turpakkeliste.BottomNavBar
import com.example.team14_turpakkeliste.MakeListButton
import com.example.team14_turpakkeliste.pinpointLocation
import com.example.team14_turpakkeliste.ui.theme.ForestGreen
import com.example.team14_turpakkeliste.ui.theme.Orange
import com.google.android.gms.maps.GoogleMap
import java.io.IOException


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController, viewModel: TurViewModel) {
    val focusManager = LocalFocusManager.current
    val location = remember {
        mutableStateOf("")

    }
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = R.id.map_view
        }
    }
    val trailingIconView = @Composable {
        IconButton(
            onClick = {
                location.value = ""
            },
        ) {
            Icon(
                Icons.Default.Clear,
                contentDescription = "Clear",
                tint = Color.Black
            )
        }
    }

    Column {
        Row() {
            TextField(
                value = location.value,

                onValueChange = { location.value = it },

                placeholder = { Text(text = "Søk på område") },

                modifier = Modifier
                    .padding(3.dp)
                    .width(300.dp)
                    .height(60.dp),

                singleLine = true,
                trailingIcon = if (location.value.isNotBlank()) trailingIconView else null,
            )


            // on below line adding a button.
            Button(
                onClick = {
                          getLocation(location.toString(),context,mapView)
                        focusManager.clearFocus()
                },
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(ForestGreen),
                // on below line adding a modifier for our button.
                modifier = Modifier
                    .padding(3.dp)
                    .height(60.dp)
                    .fillMaxSize()
            )
            {
                Text("Søk")
            }
        }

        // Remember the GoogleMap instance
        val googleMap = remember {
            mutableStateOf<GoogleMap?>(null)
        }

        val clickedLatLng = remember {
            mutableStateOf<LatLng?>(null)
        }

        var locationSelected = location.value.isNotBlank() && clickedLatLng.value != null

        AndroidView({ mapView }) { view ->
            mapView.onCreate(null)
            mapView.onResume()
            // Get the GoogleMap instance if it's not already stored
            if (googleMap.value == null) {
                mapView.getMapAsync { map ->

                    val norway = LatLng(62.943669, 9.917546)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(norway, 5f))
                    map.setOnMapClickListener { latLng ->
                        clickedLatLng.value = latLng
                        // Call the API with the clicked LatLng here
                        locationSelected = location.value.isNotBlank() && clickedLatLng.value != null
                        map.clear()
                        viewModel.currentLatitude = latLng.latitude
                        viewModel.currentLongitude = latLng.latitude
                        moveToLocation(latLng.latitude, latLng.longitude, map)
                    }
                }
            }
        }
        if (locationSelected) {
            WeatherCard(viewModel)
            Log.d("", "A location has been selected")
        }
    }
    Column(modifier = Modifier
        .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ){
        //WeatherCard(viewModel)
        MakeListButton(navController)
        BottomNavBar(navController)
    }

}

//Moves camera and marker to chosen location
fun moveToLocation(lat: Double, lon: Double, map: GoogleMap) {
    val latLng = LatLng(lat, lon)
    map.addMarker(
        MarkerOptions().position(
            latLng
        ).title("Marker in $lat, $lon")
    )

    map.moveCamera(
        CameraUpdateFactory.newLatLngZoom(
            latLng, 10f
        )
    )
}




fun getLocation(location: String, context: Context, mapView: MapView){
    var addressList: List<Address>? = null
    mapView.getMapAsync { map->
        if (location != null || location == "") {
            val geocoder = Geocoder(context)
            try {
                //henter inn addresser
                addressList = geocoder.getFromLocationName(location, 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            //må null safety calle pga try catch.

            if (addressList!!.isNotEmpty()) {
                val address = addressList!![0]
                moveToLocation(address.latitude, address.longitude, map)
            }
        }
    }
}

@Composable
fun WeatherCard(viewModel: TurViewModel) {

    when (val uiState = viewModel.turUiState) {
        is TurpakklisteUiState.Success -> {
            val alerts = uiState.alerts
            val forecastData = uiState.forecastData

            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                // Display the alerts
                // ...

                // Display the forecast data
                Text(text = "Forecast: ${forecastData.properties.meta.units.air_temperature}")
                // Add more UI elements to display the forecast data as needed
            }
        }

        TurpakklisteUiState.Error -> {
            // Display an error message
            Text(text = "Error")
        }
        TurpakklisteUiState.Loading -> {
            // Display a loading indicator
            Text(text = "Loading...")
        }
        TurpakklisteUiState.Booting -> {
            // Display a booting indicator or placeholder content
            Text(text = "Booting...")
        }
    }
}








