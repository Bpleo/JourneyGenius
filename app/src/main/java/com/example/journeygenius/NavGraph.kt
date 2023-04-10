package com.example.journeygenius

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.journeygenius.community.CommunityScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = BottomBar.Plan.route
    ){
        composable(
            route = BottomBar.Plan.route
        ){
            PlanScreen()
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