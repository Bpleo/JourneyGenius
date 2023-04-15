package com.example.journeygenius

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.journeygenius.community.CommunityScreen
import com.example.journeygenius.personal.PersonalScreen
import com.example.journeygenius.plan.PlanScreen
import com.example.journeygenius.plan.PlanViewModel

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    windowSize: WindowSize
) {
    val planViewModel: PlanViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = BottomBar.Plan.route
    ){
        composable(
            route = BottomBar.Plan.route
        ){
            PlanScreen(planViewModel, windowSize)
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