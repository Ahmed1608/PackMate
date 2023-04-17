package com.example.team14_turpakkeliste.ui

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.material.ExperimentalMaterialApi
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsComposeScreen(navController: NavController, viewModel: TurViewModel){

    val focusManager = LocalFocusManager.current

    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    val Norway = LatLng(59.911491, 10.757933)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(Norway, 6f)
    }

    val clickedLatLng = remember {
        mutableStateOf<LatLng?>(null)
    }

    val markerState = clickedLatLng.value?.let { rememberMarkerState(position = it) }

    //Relatert til bottomSheet
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val markerClick: (Marker) -> Boolean = {
        scope.launch {
            sheetState.show()
        }
        false
    }

    Box(Modifier.fillMaxSize()
        .padding(bottom = 70.dp)) {

        GoogleMap(
            modifier = Modifier.matchParentSize(),
            properties = properties,
            cameraPositionState = cameraPositionState,
            onMapClick = {latLng ->

                if(markerState == null){
                    clickedLatLng.value = latLng
                    viewModel.currentLatitude = latLng.latitude
                    viewModel.currentLongitude = latLng.longitude
                    Log.d("Oppdatert", "${viewModel.currentLatitude}, ${viewModel.currentLongitude}")
                    println()
                    scope.launch {
                        sheetState.show()
                    }
                }
                else if(markerState != null){
                    clickedLatLng.value = null
                    //potensiell bug er at man ikke fjerner de gamle lat long verdiene fra view.
                    //Men de oppdateres for hver gang man plasserer en ny.
                }
            },
        )
        {
            if (markerState != null) {
                Marker(state = markerState, onClick = markerClick)
            }
        }

        bottomSheet(coordinates = clickedLatLng.value.toString(), sheetState = sheetState, scope = scope, navController = navController)
    }
    Column(
        modifier = Modifier
        .fillMaxSize(),
    verticalArrangement = Arrangement.Bottom) {
        BottomNavBar(navController)
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun bottomSheet(coordinates: String, sheetState: SheetState, scope : CoroutineScope, navController: NavController){

    if (sheetState.isVisible){
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                scope.launch{
                    sheetState.hide()
                }
            },
        ) {

            Text("Valgt lokasjon - ${coordinates}")
            MakeListButton(navController)
            DateRangePickerScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateRangePickerScreen() {

    val dateTime = LocalDateTime.now()

    val dateRangePickerState = remember {
        DateRangePickerState(
            initialSelectedStartDateMillis = dateTime.toMillis(),
            initialDisplayedMonthMillis = null,
            initialSelectedEndDateMillis = dateTime.plusDays(1).toMillis(),
            initialDisplayMode = DisplayMode.Input,
            yearRange = (2023..2023),
        )
    }

    DateRangePicker(state = dateRangePickerState, title = { "Choose timeframe for your journey!" })

}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.toMillis() = this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

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
                          getLocation(location.toString(),context,mapView, viewModel)
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
                        map.clear()
                        // TA VARE PÅ DETTE!!!
                        viewModel.currentLatitude = latLng.latitude
                        viewModel.currentLongitude = latLng.longitude
                        Log.d("Latitude: ", viewModel.currentLatitude.toString())
                        Log.d("Longitude: ", viewModel.currentLongitude.toString())
                        moveToLocation(latLng.latitude, latLng.longitude, map)
                    }
                }
            }
        }

    }
    Column(modifier = Modifier
        .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ){
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
            latLng, 7f
        )
    )
}




fun getLocation(location: String, context: Context, mapView: MapView, viewModel: TurViewModel){
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
                //DETTE MÅ TAS VARE PÅ
                moveToLocation(address.latitude, address.longitude, map)
                viewModel.currentLatitude = address.latitude
                viewModel.currentLongitude = address.longitude
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

            Box(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()) {
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
        else -> {}
    }
}








