package com.example.journeygenius.community


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.journeygenius.components.CustomCard
import com.example.journeygenius.data.models.Plan
import com.example.journeygenius.ui.theme.JourneyGeniusTheme

@Composable
fun CommunityScreen(communityViewModel: CommunityViewModel = viewModel()) {
    val plans = communityViewModel.plans

    JourneyGeniusTheme {
        Column {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 75.dp)
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