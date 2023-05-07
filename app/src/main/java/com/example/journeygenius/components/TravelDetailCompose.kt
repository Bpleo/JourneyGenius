package com.example.journeygenius.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.journeygenius.data.models.SinglePlan

@Composable
fun TravelDetailCompose(
    data : SinglePlan,
) {
    val fontSize = 16.sp

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Date: " + data.date,
            fontSize = fontSize,
            color = MaterialTheme.colorScheme.secondary,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Destination: " + data.destination,
            fontSize = fontSize,
            color = MaterialTheme.colorScheme.secondary,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Price: " + "$".repeat(data.priceLevel) + " " + data.priceLevelLabel,
            fontSize = fontSize,
            color = MaterialTheme.colorScheme.secondary,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Travel Type: " + data.travelType,
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
    }
}