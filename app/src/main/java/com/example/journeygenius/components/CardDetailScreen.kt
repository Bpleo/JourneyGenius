package com.example.journeygenius.components

import android.util.TypedValue
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.journeygenius.JourneyGeniusViewModel
import com.example.journeygenius.PlacesapiKey
import com.example.journeygenius.data.models.Photo
import android.content.Context;
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp

@Composable
fun CardDetailScreen(
    planId: String,
    viewModel: JourneyGeniusViewModel,
    navController: NavController) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val singleImageHeight = multiplyDp(LocalContext.current, screenHeight, 0.4f)

    val userLikedPost = remember { mutableStateOf(viewModel.checkUserLikedPost(planId)) }
    val initialLikeStatus = remember { userLikedPost.value }

    val showDialog = remember { mutableStateOf(false) }
    val selectedImageUrl = remember { mutableStateOf("") }

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
            if (showMorePhoto.value) {
                item {
                    LazyRow {
                        items(photoUrls.size) { index ->
                            AsyncImage(
                                model = photoUrls[index],
                                contentDescription = "Photo",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .height(200.dp)
                                    .width(200.dp)
                                    .clickable {
                                        selectedImageUrl.value = photoUrls[index]
                                        showDialog.value = true
                                    }
                            )
                        }
                    }
                }
            }
            else {
                item {
                    AsyncImage(
                        model = firstImageUrl,
                        contentDescription = "Plan Image",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .height(singleImageHeight)
                            .fillMaxWidth()
                            .clickable {
                                selectedImageUrl.value = firstImageUrl
                                showDialog.value = true
                            }
                    )
                }
            }

            if (photoUrls.isNotEmpty() && photoUrls.size >= 1){
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { showMorePhoto.value = !showMorePhoto.value },
                            modifier = Modifier
                                .padding(top = 8.dp)
                        ) {
                            Text(text = if (showMorePhoto.value) "Hide Photos" else "More Photos")
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
                        text = formatLikesString(plan.likes),
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
                onClick = {
                    if (initialLikeStatus != userLikedPost.value) {
                        if (userLikedPost.value) {
                            viewModel.updateLikes(plan.likes + 1, planId, userLikedPost.value)
                        }
                        else {
                            viewModel.updateLikes(plan.likes - 1, planId, userLikedPost.value)
                        }
                    }
                    navController.popBackStack()
                },
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text(text = "Close")
            }

            OutlinedButton(
                onClick = { userLikedPost.value = !userLikedPost.value },
            ) {
                Text(text = if (userLikedPost.value) "Unlike" else "Like")
            }
        }

        if (showDialog.value) {
            Dialog(onDismissRequest = { showDialog.value = false }) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (selectedImageUrl.value.isNotEmpty()) {
                        AsyncImage(
                            model = selectedImageUrl.value,
                            contentDescription = "Large Photo",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDialog.value = false }
                        )
                    } else {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

fun dpToPx(context: Context, dp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    )
}

fun pxToDp(context: Context, px: Float): Float {
    return px / context.resources.displayMetrics.density
}

fun multiplyDp(context: Context, dp: Dp, multiplier: Float): Dp {
    val px = dpToPx(context, dp.value)
    val multipliedPx = px * multiplier
    return pxToDp(context, multipliedPx).dp
}