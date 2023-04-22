package com.example.journeygenius

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.journeygenius.community.CommunityScreen
import com.example.journeygenius.personal.PersonalScreen
import com.example.journeygenius.plan.PlanScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    bottomController: NavHostController,
    windowSize: WindowSize,
    db: FirebaseFirestore,
    viewModel: JourneyGeniusViewModel
) {
    NavHost(
        navController = bottomController,
        startDestination = BottomBar.Plan.route
    ){
        composable(
            route = BottomBar.Plan.route
        ){
            PlanScreen(
                viewModel,
                windowSize
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
                navController = navController
            )
        }
    }
}