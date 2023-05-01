package com.example.journeygenius

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.journeygenius.community.CommunityScreen
import com.example.journeygenius.personal.PersonalScreen
import com.example.journeygenius.plan.PlanScreenGraph
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    bottomController: NavHostController,
    db: FirebaseFirestore,
    viewModel: JourneyGeniusViewModel,
    auth: FirebaseAuth
) {
    val windowSize = rememberWindowSize()
    NavHost(
        navController = bottomController,
        startDestination = BottomBar.Plan.route
    ){
        composable(
            route = BottomBar.Plan.route
        ){
            PlanScreenGraph(
                viewModel = viewModel,
                windowSize = windowSize
            )
        }
        composable(
            route = BottomBar.Community.route
        ){
            CommunityScreen()
        }
        composable(
            route = BottomBar.Personal.route
        ){
            PersonalScreen(
                db = db,
                viewModel = viewModel,
                navController = navController,
                auth = auth
            )
        }
    }
}