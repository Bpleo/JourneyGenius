package com.example.journeygenius.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.journeygenius.data.models.Hotel

@Composable
fun HotelDetailCompose(
    data: Hotel
) {
    val fontSize = 16.sp

    Column() {
        PlaceDetailCompose(data = data.place, isHotel = true)
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "• Price: " + "$".repeat(data.priceLevel),
            fontSize = fontSize,
            color = MaterialTheme.colorScheme.secondary,
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}