package com.example.journeygenius.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.journeygenius.JourneyGeniusViewModel
import com.example.journeygenius.R
import com.example.journeygenius.community.CommunityScreen
import com.example.journeygenius.data.models.Plans

@Composable
fun CardDetailScreen(
    planId: String,
    viewModel: JourneyGeniusViewModel,
    navController: NavController) {

    val plan = viewModel.getPlanById(planId)

    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 130.dp),
            verticalArrangement = Arrangement.Top
        ){
            Image(
                painter = painterResource(id = R.drawable.demo_image_horizontal),
                contentDescription = "Plan Image",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = plan?.title ?: "",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Favorite,
                        contentDescription = "Like",
                        tint = Color.Red,
                        modifier = Modifier
                            .size(15.dp)
                    )
                    Text(
                        text = formatLikesString(30),
                        fontSize = 15.sp,
                        color = Color.LightGray,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = plan?.description ?: "",
                    fontSize = 16.sp,
                )

            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(
                onClick = {navController.popBackStack()},
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text(text = "Close")
            }

            OutlinedButton(
                onClick = {}
            ) {
                Text(text = "Like")
            }
        }
    }
}