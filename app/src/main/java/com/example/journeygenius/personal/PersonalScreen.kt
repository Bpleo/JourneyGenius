package com.example.journeygenius.personal

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.journeygenius.JourneyGeniusViewModel
import com.example.journeygenius.rememberWindowSize
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PersonalScreen(
    db: FirebaseFirestore,
    viewModel: JourneyGeniusViewModel,
    navController: NavHostController,
    auth: FirebaseAuth
) {
    val personalNavController = rememberNavController()
    NavHost(
        navController = personalNavController,
        startDestination = "Personal Menu"
    ) {
        composable("Personal Menu") {
            PersonalMenu(LocalContext.current, viewModel, navController, personalNavController)
        }
        composable("Personal Settings") {
            PersonalSettingScreen(navController = personalNavController)
        }
        composable("Personal Account") {
            PersonalAccountScreen(
                viewModel = viewModel,
                db = db,
                auth = auth,
                navController = personalNavController,
                windowSize = rememberWindowSize()
            )
        }
        composable("Personal Plan List"){
            PersonalListScreen(
                viewModel = viewModel,
                db = db,
                navController = personalNavController)
        }
    }
}

private val optionsList: ArrayList<OptionsData> = ArrayList()

@Composable
fun PersonalMenu(
    context: Context,
    viewModel: JourneyGeniusViewModel,
    navController: NavHostController,
    personalNavController: NavHostController
) {
    var listPrepared by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            optionsList.clear()
            prepareOptionsData()
            listPrepared = true
        }
    }
    if (listPrepared) {
        Column {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    UserDetails(
                        context = context,
                        name = viewModel.userName.value.text,
                        email = viewModel.email.value.text
                    )
                }
                // Show the options
                items(optionsList) { item ->
                    OptionsItemStyle(
                        item = item,
                        context = context,
                        navController = personalNavController
                    )
                }
                item {
                    OptionsItemStyle(
                        item = OptionsData(
                            icon = Icons.Default.Logout,
                            title = "Log out",
                            subTitle = ""
                        ), context = context, navController = navController
                    )
                }
            }
        }
    }
}


@Composable
private fun UserDetails(context: Context, name: String, email: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User's name and email
            Column(
                modifier = Modifier
                    .weight(weight = 3f, fill = false)
                    .padding(start = 16.dp)
            ) {
                // User's name
                Text(
                    text = name,
                    style = TextStyle(fontSize = 22.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                // User's email
                Text(
                    text = email,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray,
                        letterSpacing = (0.8).sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Edit button
            IconButton(
                modifier = Modifier
                    .weight(weight = 1f, fill = false),
                onClick = {
                    Toast.makeText(context, "Edit Button", Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit Details",
                )
            }

        }
    }
}

@Composable
private fun OptionsItemStyle(
    item: OptionsData,
    context: Context,
    navController: NavHostController
) {
    val logout = true
    Row(
        modifier = when (item.title) {
            "Log out" -> {
                Modifier
                    .fillMaxWidth()
                    .clickable(enabled = true) {
                        navController.navigate("Login/${logout}") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                    .padding(all = 16.dp)
            }
            else -> {
                Modifier
                    .fillMaxWidth()
                    .clickable(enabled = true) {
                        navController.navigate("Personal " + item.title) {}
                    }
                    .padding(all = 16.dp)
            }
        },
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Icon
        Icon(
            modifier = Modifier
                .size(32.dp),
            imageVector = item.icon,
            contentDescription = item.title,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(weight = 3f, fill = false)
                    .padding(start = 16.dp)
            ) {

                // Title
                Text(
                    text = item.title,
                    style = TextStyle(fontSize = 18.sp)
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Sub title
                Text(
                    text = item.subTitle,
                    style = TextStyle(
                        fontSize = 14.sp,
                        letterSpacing = (0.8).sp,
                        color = Color.Gray
                    )
                )

            }

            // Right arrow icon
            Icon(
                modifier = Modifier
                    .weight(weight = 1f, fill = false),
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = item.title,
                tint = Color.Black.copy(alpha = 0.70f)
            )
        }

    }
}

data class OptionsData(val icon: ImageVector, val title: String, val subTitle: String)

private fun prepareOptionsData() {
    val appIcons = Icons.Outlined
    optionsList.add(
        OptionsData(
            icon = appIcons.Person,
            title = "Account",
            subTitle = "Manage your account"
        )
    )
    optionsList.add(
        OptionsData(
            icon = appIcons.FavoriteBorder,
            title = "Plan List",
            subTitle = "My plans"
        )
    )
    optionsList.add(
        OptionsData(
            icon = appIcons.Settings,
            title = "Settings",
            subTitle = "App notification settings"
        )
    )
}


@Preview(showBackground = true)
@Composable
fun PersonalScreenPreview() {
    PersonalMenu(
        LocalContext.current,
        JourneyGeniusViewModel(Firebase.firestore, Firebase.auth),
        rememberNavController(),
        rememberNavController()
    )
}