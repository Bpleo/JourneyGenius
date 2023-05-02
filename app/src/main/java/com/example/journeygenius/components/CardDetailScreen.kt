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
import com.example.journeygenius.R
import com.example.journeygenius.community.CommunityScreen

@Composable
fun CardDetailScreen(
    planId: String,
    cardDetailViewModel: CardDetailViewModel = viewModel(),
    navController: NavController) {
    val plan = cardDetailViewModel.plan.value

    val dummyTitle = "Exploring the Wonders of Tokyo"
    val dummyDescription = "Welcome to Tokyo, the city that never sleeps! Get ready for a non-stop adventure through the bustling streets and neon-lit nightlife of Japan's capital. Start your day off with a visit to the ancient Sensō-ji temple in the historic Asakusa district, and then indulge in some retail therapy at the trendy shopping district of Shibuya. Take a break from the city's fast-paced lifestyle with a visit to the peaceful Shinjuku Gyoen National Garden, where you can unwind and enjoy the beauty of nature.\n" +
            "\n" +
            "For a taste of Tokyo's unique culinary culture, head to the Tsukiji Fish Market, the largest fish market in the world. Savor fresh sushi, sashimi, and other seafood delicacies while enjoying the vibrant atmosphere of the market.\n" +
            "\n" +
            "When night falls, immerse yourself in the vibrant nightlife of Tokyo's many districts. Dance the night away at one of the city's world-famous nightclubs, or enjoy a drink with friends at one of the many izakaya pubs scattered throughout the city.\n" +
            "\n" +
            "Experience the magic of Tokyo's traditional culture with a visit to the sumo wrestling tournaments, or catch a performance of the ancient art of kabuki theater. With so much to see and do, you'll never run out of things to explore in this dynamic and exciting city."

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
                    text = plan?.title ?: dummyTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "User ID: 1",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray,
                    )
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
                    text = plan?.description ?: dummyDescription,
                    fontSize = 16.sp,
                )

            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp)
                .border(1.dp, Color.Black, MaterialTheme.shapes.small)
                .background(Color.White)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
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