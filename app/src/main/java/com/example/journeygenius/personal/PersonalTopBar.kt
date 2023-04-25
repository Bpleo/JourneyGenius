package com.example.journeygenius.personal

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalTopBar(context: Context, title: String, navController: NavHostController) {
    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                navController.navigate("Personal Menu")
            }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Go back",
                )
            }
        }
    )
}