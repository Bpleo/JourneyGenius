package com.example.journeygenius

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@Composable
fun Intro(
    auth: FirebaseAuth,
    mainActivity: MainActivity,
    db: FirebaseFirestore,
    realtime: DatabaseReference
){
    val navController = rememberNavController()
    var showSplash by remember { mutableStateOf(true) }
    LaunchedEffect(key1 = true) {
        delay(500)
        showSplash = false
    }
    NavHost(
        navController = navController,
        startDestination = when(showSplash){
            true -> {
                "Intro"
            }
            else -> {
                "App"
            }
        }
    ) {
        composable("Intro"){
            IntroScreen()
        }
        composable("App"){
            JourneyGenius(auth = auth, mainActivity = mainActivity, db = db, realtime = realtime)
        }
    }
}

@Composable
fun IntroScreen(){
    val imagePainter = painterResource(id = R.drawable.logo)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Journey Genius",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Image(
                painter = imagePainter,
                contentDescription = "Journey Genius Logo",
                modifier = Modifier.size(200.dp)
            )
            Text(
                text = "Simplifying Travel",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = "One Plan at a Time!",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
