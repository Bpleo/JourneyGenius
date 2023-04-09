package com.example.journeygenius

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBar(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Plan: BottomBar(
        route = "plan",
        title = "Create Plan",
        icon = Icons.Default.LibraryBooks
    )
    object Community: BottomBar(
        route = "community",
        title = "Community",
        icon = Icons.Default.Groups
    )
    object Personal: BottomBar(
        route = "personal",
        title = "Personal Setting",
        icon = Icons.Default.Person
    )
}