package com.example.journeygenius.personal


import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController


@Composable
fun PersonalSettingScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PersonalTopBar(context = LocalContext.current.applicationContext, title = "Settings", navController = navController)
        Text(
            text = "Personal Settings",
            fontSize = MaterialTheme.typography.bodyLarge.fontSize
        )
    }

}


