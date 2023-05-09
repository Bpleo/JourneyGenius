package com.example.journeygenius.components

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.journeygenius.JourneyGeniusViewModel
import com.example.journeygenius.PlacesapiKey
import com.example.journeygenius.R
import com.example.journeygenius.data.models.SinglePlan
import com.example.journeygenius.plan.generateWaypoints

/**
 * Travel Detail Composable
 * A block used to display single travel detail
 * Reusable
 */
@Composable
fun TravelDetailCompose(
    data : SinglePlan,
    viewModel : JourneyGeniusViewModel
) {
    val fontSize = 16.sp

    val content= LocalContext.current
    val waypoints by remember { mutableStateOf(generateWaypoints(viewModel)) }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.date) + data.date,
            fontSize = fontSize,
            color = MaterialTheme.colorScheme.secondary,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.destionation2) + data.destination,
            fontSize = fontSize,
            color = MaterialTheme.colorScheme.secondary,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.price) + "$".repeat(data.priceLevel) + " " + data.priceLevelLabel,
            fontSize = fontSize,
            color = MaterialTheme.colorScheme.secondary,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.traveldetail_travel_type) + data.travelType,
            fontSize = fontSize,
            color = MaterialTheme.colorScheme.secondary,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Divider(
            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
            color = Color.Gray,
            thickness = 1.dp
        )

        PlaceDetailCompose(
            isStartNode = true,
            data = data.startAttraction)
        Spacer(modifier = Modifier.height(8.dp))

        Log.d("TravelDetail", data.attractionRoutes.size.toString())
        data.attractionRoutes.forEach { attractionRoute ->
            PlaceDetailCompose(
                data = attractionRoute)
            Spacer(modifier = Modifier.height(8.dp))
        }

        PlaceDetailCompose(
            isEndNode = true,
            data = data.endAttraction)
        Spacer(modifier = Modifier.height(8.dp))

        Divider(
            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
            color = Color.Gray,
            thickness = 1.dp
        )

        data.hotel.forEach {hotel ->
            HotelDetailCompose(data = hotel)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                .padding(8.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .clickable(onClick = {
                        val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=${data.startAttraction.location.lat},${data.startAttraction.location.lng}&destination=${data.endAttraction.location.lat},${data.endAttraction.location.lng}&travelmode=driving&waypoints=${waypoints}")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        content.startActivity(intent)
                    })
            ) {
                Image(
                    painter = rememberImagePainter(
                        data = "https://maps.googleapis.com/maps/api/staticmap?center=${data.startAttraction.location.lat},${data.startAttraction.location.lng}&markers=color:red%7Clabel:A%7C${data.startAttraction.location.lat},${data.startAttraction.location.lng}&markers=color:red%7Clabel:B%7C${data.endAttraction.location.lat},${data.endAttraction.location.lng}&zoom=13&size=300x300&key=$PlacesapiKey"
                    ),
                    contentDescription = stringResource(R.string.google_map_Thumbnail),
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.click_to_open_google_map),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    color = Color.Gray
                )
            }
        }
    }
}