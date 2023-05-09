package com.example.journeygenius.personal

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.journeygenius.JourneyGeniusViewModel
import com.example.journeygenius.R
import com.example.journeygenius.components.CustomCard
import com.example.journeygenius.data.models.Plans
import com.example.journeygenius.rememberWindowSize
import com.example.journeygenius.ui.theme.JourneyGeniusTheme
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PersonalListScreen(
    viewModel: JourneyGeniusViewModel,
    category: String,
    navController: NavHostController
){
    LaunchedEffect(Unit) {
        if (category == "Personal") {
            viewModel.updatePlanGroupList(emptyMap())
            viewModel.signIn()
        } else if (category == "Liked") {
            viewModel.updateCommunityPlanList(emptyMap())
            viewModel.updateStartAtValue("")
            viewModel.fetchGroupDataAndPrint(limit = 10)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        var title = ""
        if (category == "Personal") {
            title = stringResource(R.string.my_plan_list)
        } else if (category == "Liked") {
            title = stringResource(R.string.liked_plan_list)
        }
        PersonalTopBar(context = LocalContext.current.applicationContext, title = title, navController = navController)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            JourneyGeniusTheme {
                Column {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(bottom = 75.dp)
                    ) {
                        if (category == "Personal") {
                            val plansList = viewModel.planGroupList.value.entries.toList()
                            items(plansList.size) { index ->
                                val (planId, plan) = plansList[index]
                                CustomCard(
                                    id = planId,
                                    data = plan,
                                    category = "Personal"
                                ) { id->
                                    // Navigate to the CardDetailScreen with the given plan ID
                                    navController.navigate("card_detail/$id")
                                }
                            }
                        } else if (category == "Liked") {
                            val plansList = viewModel.likedPlanList.value.toList()
                            items(plansList.size) { index ->
                                val plan = viewModel.getPlanById("Community", plansList[index])
                                Log.d("Data", plansList[index] + plan)
                                plan?.let {
                                    CustomCard(
                                        id = plansList[index],
                                        data = it,
                                        category = "Community"
                                    ) { id->
                                        navController.navigate("liked_card_detail/$id")
                                    }
                                }
                            }
                        }

                    }

                }
            }
        }
    }
}
