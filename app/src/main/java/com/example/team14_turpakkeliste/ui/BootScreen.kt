package com.example.team14_turpakkeliste

import ForecastData
import android.os.Build
import android.view.animation.OvershootInterpolator
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.team14_turpakkeliste.data.Alert
import com.example.team14_turpakkeliste.ui.*
import com.example.team14_turpakkeliste.ui.theme.Orange

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetStateScreen(navController: NavHostController,viewModel: TurViewModel = viewModel()){

    when(val state = viewModel.turUiState){
        is TurpakklisteUiState.Booting -> SplashScreen(navController)
        is TurpakklisteUiState.Error -> ErrorScreen()
        is TurpakklisteUiState.Loading -> LoadingScreen()
        is TurpakklisteUiState.Success -> BootScreen(navController,state.alerts,state.forecastData, viewModel)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BootScreen(navController: NavHostController,alerts:List<Alert>, forecastData: ForecastData,viewModel: TurViewModel){
    NavHost(navController = navController, startDestination = "SavedScreen") {
        composable(Screen.HomeScreen.route) { HomeScreen(navController) }
        composable(Screen.MapScreen.route) { MapsComposeScreen(navController,viewModel) }
        composable(Screen.SavedScreen.route) { SavedScreen(navController) }
        composable(Screen.LoadingScreen.route) { LoadingScreen() }
        composable(Screen.ClothingScreen.route) { ClothingScreen(navController,forecastData,alerts,viewModel) }
    }

}



@Composable
fun ErrorScreen(){
    Text("zaza error!")

}
@Composable
fun LoadingScreen(){

    val image = painterResource(R.drawable.autumn_telt_1)
    Column(modifier = Modifier.fillMaxSize()
    ){
        val scale = remember {
            Animatable(0.0f)
        }

        LaunchedEffect(key1 = true) {
            scale.animateTo(
                targetValue = 0.7f,
                animationSpec = tween(700, easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                })
            )


        }
        Image(
            painter = image,
            contentDescription = "Telt",
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .scale(scale.value)

        )
    }

}
sealed class Screen(val route: String, val icon: ImageVector, val description : String) {
    object MapScreen : Screen("MapScreen", Icons.Default.Search, "Map")
    object HomeScreen : Screen("HomeScreen", Icons.Default.Home, "Home")
    object SavedScreen : Screen("SavedScreen", Icons.Default.Star, "Saved")
    object ClothingScreen : Screen("ClothingScreen", Icons.Default.Favorite, "Clothing")
    object LoadingScreen : Screen("LoadingScreen", Icons.Default.Refresh, "Loading")
}

@Composable
fun BottomNavBar(navController: NavController){
    val items = listOf(
        Screen.MapScreen,
        Screen.SavedScreen
    )


    NavigationBar(
        containerColor = Orange,
        modifier = Modifier
            .border(BorderStroke(2.dp, Color.Black))
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.route) },
                label = { Text(item.description) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = { if (currentDestination?.hierarchy?.any { it.route == item.route} == true) {
                    } else {
                        navController.navigate(item.route)
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
                    }
                }
            )
        }
    }
}

@Composable
fun MakeListButton(navController: NavController){
    ExtendedFloatingActionButton(
        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
        text = { Text("Make the list!") },
        onClick = {  navController.navigate("ClothingScreen")
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
        modifier = Modifier.fillMaxWidth()
    )
}