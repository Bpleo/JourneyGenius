package com.example.journeygenius

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Login Screen
 * Handles Main Menu
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    viewModel: JourneyGeniusViewModel,
    navController: NavHostController
) {
    val bottomController = rememberNavController()
    viewModel.signIn()
    Scaffold(
        bottomBar = { BottomBar(navController = bottomController)}
    ) {
        SetupNavGraph(
            navController = navController,
            bottomController = bottomController,
            db = db,
            viewModel = viewModel,
            auth = auth,
        )
    }
}

@Composable
fun BottomBar(navController: NavHostController){
    val screens = listOf(
        BottomBar.Plan,
        BottomBar.Community,
        BottomBar.Personal
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        screens.forEach{ screen ->
            AddItem(screen = screen, currentDestination = currentDestination, navController = navController)
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBar,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    NavigationBarItem(
        label = {
            Text(text = stringResource(id = screen.title))
        },
        icon = {
            Icon(
                imageVector = screen.icon,
                contentDescription = screen.icon.name
            )
        },
        selected = currentDestination?.hierarchy?.any{
            it.route == screen.route
        } == true,
        onClick = {
            navController.navigate(screen.route){
                popUpTo(0)
                launchSingleTop = true
            }
        }
    )
}

@Preview
@Composable
fun MainScreenPreview(){
    MainScreen(
        Firebase.auth,
        Firebase.firestore,
        JourneyGeniusViewModel(Firebase.firestore, Firebase.auth, Firebase.database.reference),
        rememberNavController()
    )
}