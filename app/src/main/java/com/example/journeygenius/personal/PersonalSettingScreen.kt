package com.example.journeygenius.personal


import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.example.journeygenius.R


@Composable
fun PersonalSettingScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PersonalTopBar(context = LocalContext.current.applicationContext, title = stringResource(R.string.settings), navController = navController)
        Text(
            text = stringResource(R.string.personal_settings),
            fontSize = MaterialTheme.typography.bodyLarge.fontSize
        )
    }

}


