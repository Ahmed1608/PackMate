package com.example.team14_turpakkeliste.ui

import com.example.team14_turpakkeliste.data.ForecastData
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.team14_turpakkeliste.EntityClass.AppDatabase
import com.example.team14_turpakkeliste.EntityClass.WeatherInfo
import com.example.team14_turpakkeliste.TurViewModel
import com.example.team14_turpakkeliste.data.Alert
import com.example.team14_turpakkeliste.data.getweatherIcon
import com.example.team14_turpakkeliste.data.sortClothing
import com.example.team14_turpakkeliste.data.getWeather
import com.example.team14_turpakkeliste.ui.theme.ForestGreen
import com.example.team14_turpakkeliste.ui.theme.WhiteYellow
import com.example.team14_turpakkeliste.ui.theme.Yellow
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
//legg inn for å sortere vær her
fun ListScreen(navController: NavController, viewModel: TurViewModel, forecastData: ForecastData, alerts: List<Alert>){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteYellow)
    ) {
        Text(
            text = "Hva trenger jeg?",
            fontSize = 24.sp,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .padding(20.dp)
        )
        var days = DropdownMenu(viewModel)
        for(i in 0..days-1){ExtendedFloatingActionButton(
            containerColor = ForestGreen,
            contentColor = Color.White,
            icon = { Icon(Icons.Filled.Email, contentDescription = null) },
            text = { Text("Dag ${i+1}") },
                //send med beskjed her om for å sortere klær som skal til clothingscreen henter ut infor om riktig dag
                onClick = { viewModel.chosenDay = i
                    viewModel.outerLayerList = sortClothing(forecastData, viewModel.chosenDay, "outer")
                    viewModel.innerLayerList = sortClothing(forecastData, viewModel.chosenDay, "inner")
                    viewModel.weatherInfo = getWeather(forecastData, viewModel.chosenDay)
                    viewModel.weatherImg = getweatherIcon(forecastData, viewModel.chosenDay)
                    viewModel.alertList = alerts
                    navController.navigate("ClothingScreen")
                {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }

                    // Avoid multiple copies of the same destination when
                    // reselecting the same item
                    launchSingleTop = true
                    // Restore state when reselecting a previously selected item
                    restoreState = true
                }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .padding(5.dp)
            )
        }

    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom) {
        SaveButton(viewModel = viewModel, forecastData = forecastData )
        BottomNavBar(navController)
    }
}

private lateinit var appDB : AppDatabase
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun SaveButton(viewModel: TurViewModel, forecastData: ForecastData){
    var buttonState by remember { mutableStateOf(true) }
    val context: Context = LocalContext.current
    Button(
        enabled = buttonState,
        colors = ButtonDefaults.buttonColors(
            containerColor = Yellow,
            contentColor = Color.Black),
        onClick = {
            appDB = AppDatabase.getDatabase(context)
            GlobalScope.launch(Dispatchers.IO) {
                //gå gjennom variabelnavn her og tenk engelsk og riktig
                val date = forecastData.properties.timeseries.get(0).time
                for(i in 0..viewModel.numberOfDays){
                    val weather = getWeather(forecastData, i)
                    val img = getweatherIcon(forecastData, i)
                    val tempList = WeatherInfo(date, i, weather.temp, weather.windspeed,weather.watermm, img)
                    appDB.UserDao().insert(tempList)
                }
            }
            buttonState = false
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Filled.Favorite, contentDescription = "Save list")
        Text("Save list")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenu(viewModel: TurViewModel): Int{


    var expanded by remember { mutableStateOf(false) }
    val districts = listOf("Dag 1", "Dag 2", "Dag 3")
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }

    ) {

        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = districts[viewModel.numberOfDays-1],
            onValueChange = { },
            label = { Text("Velg district") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            districts.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {


                        when (selectionOption) {
                            "Dag 1" -> {

                                viewModel.numberOfDays = 1

                            }
                            "Dag 2" -> {
                                viewModel.numberOfDays = 2

                            }
                            "Dag 3" -> {
                                viewModel.numberOfDays = 3
                            }
                        }
                        expanded = false
                    })

            }
        }
    }

    return  viewModel.numberOfDays
}