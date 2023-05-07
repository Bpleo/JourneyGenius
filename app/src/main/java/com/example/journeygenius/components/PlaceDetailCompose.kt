package com.example.journeygenius.components

import android.util.Log
import android.widget.RatingBar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.journeygenius.data.models.Place
import androidx.compose.runtime.*
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle

@Composable
fun PlaceDetailCompose(
    modifier: Modifier = Modifier,
    isStartNode: Boolean = false,
    isEndNode : Boolean = false,
    isHotel : Boolean = false,
    data : Place,
) {
    val fontSize = 16.sp

    Column(
        modifier = modifier
    ) {
        Text(
            text = if (isStartNode) {
                "Start Attraction: " + data.name
            } else if (isEndNode) {
                "End Attraction: "  + data.name
            } else if (isHotel) {
                "Hotel: " + data.name
            } else { // is middle attraction node
                "Middle Attraction: " + data.name
                   },
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "• Vicinity: " + data.vicinity,
            fontSize = fontSize,
            color = MaterialTheme.colorScheme.secondary,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
        ) {
            Text(
                text = "• Rating: ",
                fontSize = fontSize,
                color = MaterialTheme.colorScheme.secondary,
            )

            RatingBar(
                value = data.rating.toFloat(),
                config = RatingBarConfig()
                    .style(RatingBarStyle.HighLighted),
                onValueChange = {},
                onRatingChanged = {})

            Text(
                text = " " + data.rating,
                fontSize = fontSize,
                color = MaterialTheme.colorScheme.secondary,
            )
        }

    }
}