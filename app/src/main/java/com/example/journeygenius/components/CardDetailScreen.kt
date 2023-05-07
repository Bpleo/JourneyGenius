package com.example.journeygenius.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.journeygenius.JourneyGeniusViewModel
import com.example.journeygenius.PlacesapiKey
import com.example.journeygenius.R
import com.example.journeygenius.community.CommunityScreen
import com.example.journeygenius.data.models.Photo
import com.example.journeygenius.data.models.Plans

@Composable
fun CardDetailScreen(
    planId: String,
    viewModel: JourneyGeniusViewModel,
    navController: NavController) {

    val plan = viewModel.getPlanById(planId)

    val allPhotos: List<Photo> = plan!!.plans.flatMap { singlePlan ->
        singlePlan.attractions.flatMap { place ->
            place.photos ?: emptyList()
        }
    }

    val photoUrls: List<String> = allPhotos.mapNotNull { photo ->
        if (photo.photo_reference != null) {
            getPhotoUrl(photo.photo_reference, PlacesapiKey)
        } else {
            null
        }
    }

    val firstImageUrl = photoUrls.firstOrNull() ?: ""

    val showMorePhoto = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 150.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            item {
                AsyncImage(
                    model = firstImageUrl,
                    contentDescription = "Plan Image",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                )
            }

            item {
                if (!showMorePhoto.value){
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { showMorePhoto.value = true },
                            modifier = Modifier
                                .padding(top = 8.dp)
                        ) {
                            Text(text = "More Photos")
                        }
                    }
                }
            }

            if (showMorePhoto.value) {
                items(photoUrls.size) { index ->
                    if (index != 0 && index < 5) {
                        AsyncImage(
                            model = photoUrls[index],
                            contentDescription = "Photo",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .height(200.dp)
                                .fillMaxWidth()
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { showMorePhoto.value = false },
                            modifier = Modifier
                                .padding(top = 8.dp)
                        ) {
                            Text(text = "Close Photos")
                        }
                    }
                }
            }

            item {
                Text(
                    text = plan?.title ?: "",
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
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
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                Text(
                    text = plan?.description ?: "",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

            plan?.plans?.let {
                items(it.size) { index ->
                    TravelExpandableCard(data = it[index], viewModel)
                    Spacer(modifier = Modifier.height(8.dp))
                }
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