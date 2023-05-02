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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.journeygenius.components.CustomCard
import com.example.journeygenius.ui.theme.JourneyGeniusTheme
import kotlinx.coroutines.launch

@Composable
fun CommunityScreen(communityViewModel: CommunityViewModel = viewModel()) {
    val plans = communityViewModel.plans
    val scrollState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    JourneyGeniusTheme {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, end = 16.dp)
                    .wrapContentWidth(align = Alignment.End)
            ) {
                IconButton(
                    onClick = {
                        communityViewModel.testRefresh()
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
                items(plans.size) { index ->
                    CustomCard(
                        data = plans[index],
                    )
                }
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun CommunityScreenPreview(){
    CommunityScreen()
}