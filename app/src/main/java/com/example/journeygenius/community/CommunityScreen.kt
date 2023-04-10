package com.example.journeygenius

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.journeygenius.ui.theme.JourneyGeniusTheme

@Composable
fun CommunityScreen() {
    JourneyGeniusTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "Community",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommunityScreenPreview(){
    CommunityScreen()
}