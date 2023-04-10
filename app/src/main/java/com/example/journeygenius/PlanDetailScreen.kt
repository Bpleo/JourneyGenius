package com.example.journeygenius

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.journeygenius.ui.theme.JourneyGeniusTheme

@Composable
fun PlanDetail(){
JourneyGeniusTheme {
    Box(
        modifier = Modifier.fillMaxSize(),
    ){
        Box(contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 20.dp)){
            Text(text = "Detail",
                fontSize = MaterialTheme.typography.headlineLarge.fontSize)
        }
        Column(
            modifier = Modifier
                .padding(25.dp, 70.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ){
            Column(modifier = Modifier.padding(15.dp,20.dp)) {
                Text(text = "Date: ",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                )
                Spacer(modifier = Modifier.height(75.dp))
                Text(text = "Destination: ",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize)
                Spacer(modifier = Modifier.height(75.dp))
                Text(text = "Interests: ",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize)
                Spacer(modifier = Modifier.height(75.dp))
                Text(text = "Budget: ",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize)
                Spacer(modifier = Modifier.height(75.dp))
                Text(text = "Hotel: ",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize)
                Spacer(modifier = Modifier.height(75.dp))
                Text(text = "Transport Mode: ",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize)
                Spacer(modifier = Modifier.height(75.dp))
            }


        }
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 40.dp),
            contentAlignment = Alignment.BottomCenter
        ){
            Button(onClick = { /*TODO*/ }, modifier = Modifier
                .width(100.dp)
            ) {
                Text(text = "Back")
            }
        }


    }
}
}
@Preview(showBackground = true)
@Composable
fun PlanDetailPreview(){
    PlanDetail()
}
