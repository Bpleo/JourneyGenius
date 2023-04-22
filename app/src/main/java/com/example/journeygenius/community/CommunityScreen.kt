package com.example.journeygenius.community

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.journeygenius.data.models.Plan
import com.example.journeygenius.ui.theme.JourneyGeniusTheme

@Composable
fun CommunityScreen(communityViewModel: CommunityViewModel = viewModel()) {
    val plans = communityViewModel.plans

    JourneyGeniusTheme {
        LazyColumn(contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 75.dp)
        ) {
            items(plans, key = { it.planId }) {
                CustomCard(data = it)
            }
            item {
                Text(
                    text = "--- No More Content ---",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CustomCard(data: Plan) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable { /* handle click on the card */ },
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = data.title,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
//                    style = MaterialTheme.typography.h6
                )
                IconButton(onClick = { /* handle like button click */ }) {
                    Icon(
                        imageVector = Icons.Outlined.Favorite,
                        contentDescription = "Like",
                        tint = Color.Red
                    )
                }
                Text(
                    text = formatLikesString(data.likes),
//                    style = MaterialTheme.typography.subtitle1
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "User ID: ${data.userId}",
//                style = MaterialTheme.typography.subtitle2
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = data.description,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
//                style = MaterialTheme.typography.body1
            )
        }
    }
}

fun formatLikesString(likes: Int): String {
    return when {
        likes >= 1000 -> {
            val thousands = likes / 1000
            val decimal = (likes % 1000) / 100
            "$thousands.$decimal" + "K"
        }
        else -> {
            likes.toString()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommunityScreenPreview(){
    CommunityScreen()
}