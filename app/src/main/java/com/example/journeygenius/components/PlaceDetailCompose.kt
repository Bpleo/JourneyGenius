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
import androidx.compose.ui.res.stringResource
import com.example.journeygenius.R
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle

/**
 * Place Detail Composable
 * A block used to display Place information
 * Reusable
 */
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
                stringResource(R.string.start_attraction) + data.name
            } else if (isEndNode) {
                stringResource(R.string.end_attraction)  + data.name
            } else if (isHotel) {
                stringResource(R.string.hotel) + data.name
            } else { // is middle attraction node
                stringResource(R.string.middle_attraction) + data.name
                   },
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.place_detail_vicinity) + data.vicinity,
            fontSize = fontSize,
            color = MaterialTheme.colorScheme.secondary,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
        ) {
            Text(
                text = stringResource(R.string.place_detail_rating),
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