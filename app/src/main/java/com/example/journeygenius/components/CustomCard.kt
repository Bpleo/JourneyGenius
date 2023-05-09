package com.example.journeygenius.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.journeygenius.PlacesapiKey
import com.example.journeygenius.R
import com.example.journeygenius.data.models.Photo
import com.example.journeygenius.data.models.Plans

/**
 * CustomCard Composable
 * Defines how each item is presented in CommunityList
 * Reusable
 */
@Composable
fun CustomCard(
    id: String,
    data: Plans,
    category: String,
    onCardClick: (String) -> Unit,
) {

    val allPhotos: List<Photo> = data.plans.flatMap { singlePlan ->
        singlePlan.attractions.flatMap { place ->
            place.photos ?: emptyList()
        }
    }

    val randomPhoto: Photo? = allPhotos.randomOrNull()

    val photoUrl = if (randomPhoto?.photo_reference != null) {
        getPhotoUrl(randomPhoto.photo_reference, PlacesapiKey)
    } else {
        R.drawable.demo_image_horizontal
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .clickable { onCardClick(id) }
                .width(200.dp)
                .height(270.dp),
            shape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            ),
        ) {
            Column() {
                if (photoUrl is String) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Image",
                        modifier = Modifier
                            .height(150.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop,
                    )
                } else if (photoUrl is Int) {
                    Image(
                        painter = painterResource(id = photoUrl),
                        contentDescription = "Image",
                        modifier = Modifier
                            .height(150.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop,
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(top = 10.dp, start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = data.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.primary,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = data.description,
                        fontSize = 16.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (category == "Personal" && !data.isPublic) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = "Private",
                                tint = Color.LightGray,
                                modifier = Modifier
                                    .size(16.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.Outlined.Favorite,
                            contentDescription = "Like",
                            tint = Color.Red,
                            modifier = Modifier
                                .size(16.dp)
                        )
                        Text(
                            text = formatLikesString(data.likes),
                            fontSize = 16.sp,
                            color = Color.LightGray,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

// Helper function to properly format like number
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

// Helper function to get photo url from Place API
fun getPhotoUrl(photoReference: String, apiKey: String): String {
    return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=5000&photoreference=$photoReference&key=$apiKey"
}