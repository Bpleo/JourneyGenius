package com.example.journeygenius

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBar(
    val route: String,
    @StringRes val title: Int,
    val icon: ImageVector
) {
    object Plan: BottomBar(
        route = "plan",
        title = R.string.create_plan,
        icon = Icons.Default.LibraryBooks
    )
    object Community: BottomBar(
        route = "community",
        title = R.string.community,
        icon = Icons.Default.Groups
    )
    object Personal: BottomBar(
        route = "personal",
        title = R.string.personal_settings,
        icon = Icons.Default.Person
    )
}