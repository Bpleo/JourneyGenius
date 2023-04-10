package com.example.journeygenius

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    val planViewModel: PlanViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = BottomBar.Plan.route
    ){
        composable(
            route = BottomBar.Plan.route
        ){
            PlanScreen(planViewModel)
        }
        composable(
            route = BottomBar.Community.route
        ){
            CommunityScreen()
        }
        composable(
            route = BottomBar.Personal.route
        ){
            PersonalScreen()
        }
    }
}