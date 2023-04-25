package com.example.team14_turpakkeliste


import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.example.team14_turpakkeliste.EntityClass.AppDatabase
import com.example.team14_turpakkeliste.EntityClass.Pakkliste
import com.example.team14_turpakkeliste.ui.theme.Burgunder
import com.example.team14_turpakkeliste.ui.theme.Team14TurPakkeListeTheme
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SavedScreen(navController: NavController) {
    val context = LocalContext.current
    val appDB = AppDatabase.getDatabase(context)
    val pak : Pakkliste = Pakkliste(1, "Haakon", "Korslund")
    val pak2 : Pakkliste = Pakkliste(null, "Haakon2", "Korslund2")
    appDB.UserDao().insert(pak)
    appDB.UserDao().insert(pak2)
    val saved = appDB.UserDao().getAll()

    Column(Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text( modifier = Modifier
                .fillMaxSize()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .padding(20.dp),
                text = "Lagrede pakkelister:",
                fontSize = 30.sp
            )

        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = Burgunder)) {
        Text( modifier = Modifier
            .fillMaxSize()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(20.dp),
            text = "Lagrede pakkelister:",
            fontSize = 30.sp
        )
        Spacer(modifier = Modifier
            .height(50.dp)
            .fillMaxWidth())
    }
    LazyColumn(modifier = Modifier
        .height(200.dp)
        .width(200.dp)
        .wrapContentWidth(Alignment.CenterHorizontally)
        .background(color = Burgunder)) {
        for (s in saved){
            items(1){
                ListCard(s.firstName!!,s.lastName!!)
            }
        }

    }
    Column(modifier = Modifier
        .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ){
        Text(modifier = Modifier
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(20.dp),
            text = "Klikk på Map for å lage en ny pakkeliste!",
            fontSize = 18.sp
        )
        BottomNavBar(navController)
    }

}

@Composable
fun SavedButton(navController: NavController){
    ExtendedFloatingActionButton(
        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
        text = { Text("Hent siste pakkeliste") },
        onClick = {  navController.navigate("ListScreen")
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




@Preview(showBackground = true)
@Composable
fun SavedPreview() {
    Team14TurPakkeListeTheme {
        SavedScreen(rememberNavController())
    }
}

@Composable
fun ListCard(firstname: String, lastname: String){
    Card(
        Modifier
            .height(300.dp)
            .fillMaxWidth()){
        Box(modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.Center){
            Column(horizontalAlignment = Alignment.CenterHorizontally){
                Text(text = firstname, fontSize = 27.sp)

                Text(text = "$lastname", fontSize = 17.sp)
            }
        }
    }

}