package com.example.journeygenius.community


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.journeygenius.JourneyGeniusViewModel
import com.example.journeygenius.components.CardDetailScreen
import com.example.journeygenius.components.CustomCard
import com.example.journeygenius.data.models.Plan
import com.example.journeygenius.ui.theme.JourneyGeniusTheme
import kotlinx.coroutines.launch
import java.time.format.TextStyle

@Composable
fun CommunityScreen(viewModel: JourneyGeniusViewModel) {
    val nestedNavController = rememberNavController()

    LaunchedEffect(Unit) {
        viewModel.fetchGroupDataAndPrint(limit = 10)
    }

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
                    navController = nestedNavController
                )
            }
        }
    }
}

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
                text = "Community",
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                modifier = Modifier
                    .padding(start = 32.dp, top = 16.dp)
                    .weight(1f)
            )
            IconButton(
                onClick = {
                    viewModel.refreshCommunity()
                    coroutineScope.launch {
                        scrollState.animateScrollToItem(0)
                    }
                },
                modifier = Modifier
                    .background(
                        color = Color.LightGray,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = Color.White
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
                    onCardClick = { id ->
                        navController.navigate("card_detail/$id")
                    }
                )
            }
        }
    }
}

