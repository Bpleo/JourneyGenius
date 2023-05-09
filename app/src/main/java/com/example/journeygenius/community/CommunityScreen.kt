package com.example.journeygenius.community


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.journeygenius.JourneyGeniusViewModel
import com.example.journeygenius.R
import com.example.journeygenius.components.CardDetailScreen
import com.example.journeygenius.components.CustomCard
import com.example.journeygenius.ui.theme.JourneyGeniusTheme
import kotlinx.coroutines.launch

/**
 * Community Screen Composable
 * Include all functionalities within the community compose
 */
@Composable
fun CommunityScreen(viewModel: JourneyGeniusViewModel) {
    // own nav controller
    val nestedNavController = rememberNavController()

    // get data here
    LaunchedEffect(Unit) {
        viewModel.updateCommunityPlanList(emptyMap())
        viewModel.updateStartAtValue("")
        viewModel.fetchGroupDataAndPrint(limit = 10)
    }

    // define path of nav controller
    JourneyGeniusTheme {
        NavHost(
            navController = nestedNavController,
            startDestination = "community_list"
        ) {
            composable("community_list") {
                CommunityList(viewModel, nestedNavController)
            }
            composable("card_detail/{planId}") { backStackEntry ->
                CardDetailScreen(
                    planId = backStackEntry.arguments?.getString("planId") ?: "",
                    viewModel = viewModel,
                    navController = nestedNavController,
                    category = "Community"
                )
            }
        }
    }
}

/**
 * Community List composable
 * Maintain community list and items
 */
@Composable
fun CommunityList(
    viewModel: JourneyGeniusViewModel,
    navController: NavController
) {
    val scrollState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, end = 16.dp)
                .wrapContentWidth(align = Alignment.End)
        ) {
            Text(
                text = stringResource(R.string.community),
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                modifier = Modifier
                    .padding(start = 32.dp, top = 16.dp)
                    .weight(1f)
            )
            Button(
                onClick = {
                    viewModel.fetchGroupDataAndPrint(limit = 10)
                    coroutineScope.launch {
                        scrollState.animateScrollToItem(0)
                    }
                },
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text(
                    text="More",
                    color = Color.White
                )
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 75.dp),
            state = scrollState
        ) {
            val plansList = viewModel.communityPlanList.value.entries
                .filter { (_, plan) -> plan.isPublic }
                .toList()

            items(plansList.size) { index ->
                val (planId, plan) = plansList[index]

                CustomCard(
                    id = planId,
                    data = plan,
                    category = "Community"
                ) { id ->
                    navController.navigate("card_detail/$id")
                }
            }
        }
    }
}

