package com.example.journeygenius.personal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.journeygenius.JourneyGeniusViewModel
import com.example.journeygenius.components.CustomCard
import com.example.journeygenius.data.models.Plans
import com.example.journeygenius.rememberWindowSize
import com.example.journeygenius.ui.theme.JourneyGeniusTheme
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PersonalListScreen(
    viewModel: JourneyGeniusViewModel,
    db: FirebaseFirestore,
    navController: NavHostController
){
    val windowSize = rememberWindowSize()

    val dummyPlans = mutableListOf<Plans>()
//    TODO: get from db or viewModel
//    val plans: List<Plan>

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        PersonalTopBar(context = LocalContext.current.applicationContext, title = "My Plan List", navController = navController)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            JourneyGeniusTheme {
                Column {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(bottom = 75.dp)
                    ) {
                        items(dummyPlans.size) { index ->
                            CustomCard(
                                id = "0",
                                data = dummyPlans[index],
                                onCardClick = { planId ->
                                    // Navigate to the CardDetailScreen with the given plan ID
                                    navController.navigate("card_detail/$planId")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}