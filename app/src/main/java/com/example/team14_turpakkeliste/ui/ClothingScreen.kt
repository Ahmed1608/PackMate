package com.example.team14_turpakkeliste

import ForecastData
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.team14_turpakkeliste.data.*
import com.example.team14_turpakkeliste.ui.TurViewModel


@Composable
fun ClothingScreen(navController: NavController, forecastData: ForecastData, alerts: List<Alert>,viewModel: TurViewModel){
    viewModel.getForecast()
    val recommendedList = sortClothing(forecastData)
    for(alert in alerts){
        if(pinpointLocation(viewModel.currentLatitude,viewModel.currentLongitude,alert.areaPolygon!!)){
            println(alert.headline)
            println(alert.severity)
        }
    }

    Column() {
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 30.dp)
        ) {
            items(recommendedList) { recommendedList ->
                val title = "${recommendedList.material}${recommendedList.type}"
                val description =
                    "Varme: ${recommendedList.warmth}\nVindtetthet: ${recommendedList.windproof} \nVanntetthet: ${recommendedList.waterproof}"
                val image = recommendedList.image
                ExpandableCard(title = title, description = description, img = image)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        Spacer(modifier = Modifier.height(100.dp))
        val descriptionWeather = getWeather(forecastData)
        ExpandableCard(title = "Vis Været", description = descriptionWeather, img = forecastData.properties.timeseries.get(0).data.next_1_hours.summary.symbol_code)
        Column(modifier = Modifier
            .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ){
            SaveButton()
            BottomNavBar(navController)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableCard(
    title: String,
    titleFontSize: TextUnit = MaterialTheme.typography.titleMedium.fontSize,
    titleFontWeight: FontWeight = FontWeight.Bold,
    description: String,
    descriptionFontSize: TextUnit = MaterialTheme.typography.bodyMedium.fontSize,
    descriptionFontWeight: FontWeight = FontWeight.Normal,
    descriptionMaxLines: Int = 4,
    padding: Dp = 12.dp,
    img: String
) {
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),

        onClick = {
            expandedState = !expandedState
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(6f),
                    text = title,
                    fontSize = titleFontSize,
                    fontWeight = titleFontWeight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(
                    modifier = Modifier
                        .weight(1f)
                        .rotate(rotationState),
                    onClick = {
                        expandedState = !expandedState
                    }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Drop-Down Arrow"
                    )
                }
            }
            if (expandedState) {
                val image = getImg(desc = img)
                Image(painter = image, contentDescription = "picture of clothing-piece")
                Text(
                    text = description,
                    fontSize = descriptionFontSize,
                    fontWeight = descriptionFontWeight,
                    maxLines = descriptionMaxLines,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun getImg(desc: String): Painter{
    val painter: Painter = when(desc){
        "SoftJO"->painterResource(id = R.drawable.softshelljakke)
        "SoftPO"->painterResource(id = R.drawable.softshellbukse)
        "DownJO"-> painterResource(id = R.drawable.dunjakke)
        "ShellJO"-> painterResource(id = R.drawable.skalljakke)
        //"ShellPO" -> painterResource(id = R.drawable.skallbukse)
        "clearsky_day" -> painterResource(id = R.drawable.clearsky_day)
        "clearsky_night" -> painterResource(id = R.drawable.clearsky_night)
        //"clearsky_polartwilight" -> painterResource(id = R.drawable.clear)
        "cloudy" -> painterResource(id = R.drawable.cloudy)
        "fair_day" -> painterResource(id = R.drawable.fair_day)
        "fair_night" -> painterResource(id = R.drawable.fair_night)
        "fair_polartwilight" -> painterResource(id = R.drawable.fair_polartwilight)
        "fog" -> painterResource(id = R.drawable.fog)
        "heavyrain" -> painterResource(id = R.drawable.heavyrain)
        "heavyrainandthunder" -> painterResource(id = R.drawable.heavyrainandthunder)
        "heavyrainshowers_day" -> painterResource(id = R.drawable.heavyrainshowers_day)
        "heavyrainshowers_night" -> painterResource(id = R.drawable.heavyrainshowers_night)
        "heavyrainshowers_polartwilight" -> painterResource(id = R.drawable.heavyrainshowers_polartwilight)
        "heavyrainshowersandthunder_day" -> painterResource(id = R.drawable.heavyrainshowersandthunder_day)
        "heavyrainshowersandthunder_night" -> painterResource(id = R.drawable.heavyrainshowersandthunder_night)
        "heavyrainshowersandthunder_polartwilight" -> painterResource(id = R.drawable.heavyrainshowersandthunder_polartwilight)
        "heavysleet" -> painterResource(id = R.drawable.heavysleet)
        "heavysleetandthunder" -> painterResource(id = R.drawable.heavysleetandthunder)
        "heavysleetshowers_day"-> painterResource(id = R.drawable.heavysleetshowers_day)
        "heavysleetshowers_night" -> painterResource(id = R.drawable.heavysleetshowers_night)
        "heavysleetshowers_polartwilight" -> painterResource(id = R.drawable.heavysleetshowers_polartwilight)
        "heavysleetshowersandthunder_day" -> painterResource(id = R.drawable.heavysleetshowersandthunder_day)
        "heavysleetshowersandthunder_night" -> painterResource(id = R.drawable.heavysleetshowersandthunder_night)
        "heavysleetshowersandthunder_polartwilight" -> painterResource(id = R.drawable.heavysleetshowersandthunder_polartwilight)
        "heavysnow" -> painterResource(id = R.drawable.heavysnow)
        "heavysnowandthunder" -> painterResource(id = R.drawable.heavysnowandthunder)
        "heavysnowshowers_day"-> painterResource(id = R.drawable.heavysnowshowers_day)
        "heavysnowshowers_night" -> painterResource(id = R.drawable.heavysnowshowers_night)
        "heavysnowshowers_polartwilight" -> painterResource(id = R.drawable.heavysnowshowers_polartwilight)
        "heavysnowshowersandthunder_day" -> painterResource(id = R.drawable.heavysnowshowersandthunder_day)
        "heavysnowshowersandthunder_night" -> painterResource(id = R.drawable.heavysnowshowersandthunder_night)
        "heavysnowshowersandthunder_polartwilight" -> painterResource(id = R.drawable.heavysnowshowersandthunder_polartwilight)
        "lightrain" -> painterResource(id = R.drawable.lightrain)
        "lightrainandthunder" -> painterResource(id = R.drawable.lightrainandthunder)
        "lightrainshowers_day" -> painterResource(id = R.drawable.lightrainshowers_day)
        "lightrainshowers_night" -> painterResource(id = R.drawable.lightrainshowers_night)
        "lightrainshowers_polartwilight" -> painterResource(id = R.drawable.lightrainshowers_polartwilight)
        "lightrainshowersandthunder_day" -> painterResource(id = R.drawable.lightrainshowersandthunder_day)
        "lightrainshowersandthunder_night" -> painterResource(id = R.drawable.lightrainshowersandthunder_night)
        "lightrainshowersandthunder-polartwilight" -> painterResource(id = R.drawable.lightrainshowersandthunder_polartwilight)
        "lightsleet" -> painterResource(id = R.drawable.lightsleet)
        "lightsleetandthunder" -> painterResource(id = R.drawable.lightsleetandthunder)
        "lightsleetshowers_day"-> painterResource(id = R.drawable.lightsleetshowers_day)
        "lightsleetshowers_night" -> painterResource(id = R.drawable.lightsleetshowers_night)
        "lightsleetshowers_polartwilight" -> painterResource(id = R.drawable.lightsleetshowers_polartwilight)
        "lightssleetshowersandthunder_day" -> painterResource(id = R.drawable.lightssleetshowersandthunder_day)
        "lightssleetshowersandthunder_night" -> painterResource(id = R.drawable.lightssleetshowersandthunder_night)
        "lightssleetshowersandthunder_polartwilight" -> painterResource(id = R.drawable.lightssleetshowersandthunder_polartwilight)
        "lightsnow" -> painterResource(id = R.drawable.lightsnow)
        "lightsnowandthunder" -> painterResource(id = R.drawable.lightsnowandthunder)
        "lightsnowshowers_day"-> painterResource(id = R.drawable.lightsnowshowers_day)
        "lightsnowshowers_night" -> painterResource(id = R.drawable.lightsnowshowers_night)
        "lightsnowshowers_polartwilight" -> painterResource(id = R.drawable.lightsnowshowers_polartwilight)
        "lightssnowshowersandthunder_day" -> painterResource(id = R.drawable.lightssnowshowersandthunder_day)
        "lightsnowshowersandthunder_night" -> painterResource(id = R.drawable.lightssnowshowersandthunder_night)
        "lightsnowshowersandthunder_polartwilight" -> painterResource(id = R.drawable.lightssnowshowersandthunder_polartwilight)
        "partlycloudy_day" -> painterResource(id = R.drawable.partlycloudy_day)
        "partlycloudy_night" -> painterResource(id = R.drawable.partlycloudy_night)
        "partlycloudy_polartwilight" -> painterResource(id = R.drawable.partlycloudy_polartwilight)
        "rain" -> painterResource(id = R.drawable.rain)
        "rainandthunder" -> painterResource(id = R.drawable.rainandthunder)
        "rainshowers_day" -> painterResource(id = R.drawable.rainshowers_day)
        "rainshowers_night" -> painterResource(id = R.drawable.rainshowers_night)
        "rainshowers_polartwilight" -> painterResource(id = R.drawable.rainshowers_polartwilight)
        "rainshowersandthunder_day" -> painterResource(id = R.drawable.rainshowersandthunder_day)
        "rainshowersandthunder_night" -> painterResource(id = R.drawable.rainshowersandthunder_night)
        "rainshowersandthunder-polartwilight" -> painterResource(id = R.drawable.rainshowersandthunder_polartwilight)
        "sleet" -> painterResource(id = R.drawable.sleet)
        "sleetandthunder" -> painterResource(id = R.drawable.sleetandthunder)
        "sleetshowers_day"-> painterResource(id = R.drawable.sleetshowers_day)
        "sleetshowers_night" -> painterResource(id = R.drawable.sleetshowers_night)
        "sleetshowers_polartwilight" -> painterResource(id = R.drawable.sleetshowers_polartwilight)
        "sleetshowersandthunder_day" -> painterResource(id = R.drawable.sleetshowersandthunder_day)
        "sleetshowersandthunder_night" -> painterResource(id = R.drawable.sleetshowersandthunder_night)
        "sleetshowersandthunder_polartwilight" -> painterResource(id = R.drawable.sleetshowersandthunder_polartwilight)
        "snow" -> painterResource(id = R.drawable.snow)
        "snowandthunder" -> painterResource(id = R.drawable.snowandthunder)
        "snowshowers_day"-> painterResource(id = R.drawable.snowshowers_day)
        "snowshowers_night" -> painterResource(id = R.drawable.snowshowers_night)
        "snowshowers_polartwilight" -> painterResource(id = R.drawable.snowshowers_polartwilight)
        "snowshowersandthunder_day" -> painterResource(id = R.drawable.snowshowersandthunder_day)
        "snowshowersandthunder_night" -> painterResource(id = R.drawable.snowshowersandthunder_night)
        "snowshowersandthunder_polartwilight" -> painterResource(id = R.drawable.snowshowersandthunder_polartwilight)
        else -> {
            painterResource(id = R.drawable.ic_launcher_foreground)
        }
    }
    return painter
}

@Composable
fun SaveButton(){
    ExtendedFloatingActionButton(
        icon = { Icon(Icons.Filled.Favorite, contentDescription = "Save list") },
        text = { Text("Save list") },
        onClick = {

        },
        modifier = Modifier.fillMaxWidth()
    )
}