package com.example.journeygenius.personal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.journeygenius.JourneyGeniusViewModel
import com.example.journeygenius.WindowSize
import com.example.journeygenius.WindowType
import com.example.journeygenius.rememberWindowSize
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
@Composable
fun PersonalDetail(
    viewModel: JourneyGeniusViewModel
){

}

@Composable
fun PersonalAccountScreen(
    viewModel: JourneyGeniusViewModel,
    db: FirebaseFirestore,
    auth: FirebaseAuth,
    navController: NavHostController,
    windowSize: WindowSize
){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PersonalTopBar(context = LocalContext.current.applicationContext, title = "Details", navController = navController)
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

@Preview
@Composable
fun PreviewPersonalAccountScreen(){
    val auth = Firebase.auth
    val db = Firebase.firestore
    PersonalAccountScreen(
        JourneyGeniusViewModel(
            db,
            auth),
        db,
        auth,
        rememberNavController(),
        rememberWindowSize()
    )
}