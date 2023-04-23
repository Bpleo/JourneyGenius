package com.example.journeygenius.personal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.example.journeygenius.JourneyGeniusViewModel
import com.example.journeygenius.WindowType
import com.example.journeygenius.rememberWindowSize
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PersonalListScreen(
    viewModel: JourneyGeniusViewModel,
    db: FirebaseFirestore,
    navController: NavHostController
){
    val windowSize = rememberWindowSize()
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        PersonalTopBar(context = LocalContext.current.applicationContext, title = "Plan List", navController = navController)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (windowSize.height) {
                WindowType.Medium -> {
                    Column(

                    ) {
                    }
                }
                else -> {

                }
            }
        }
    }
}