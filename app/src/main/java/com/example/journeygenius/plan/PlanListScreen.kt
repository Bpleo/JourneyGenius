package com.example.journeygenius.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.journeygenius.ui.theme.JourneyGeniusTheme

@Composable
fun PlanList(){
    JourneyGeniusTheme {
        var items by remember { mutableStateOf(listOf("Plan 1", "Plan 2", "Plan 3")) }
        Box(
            modifier = Modifier.fillMaxSize(),
        ){
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp)){
                Text(text = "Plan List",
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize)
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp, 80.dp)
                    .clickable {
                        /*TODO*/
                        //navController.navigate(Screen.PlanDetailScreen(plan.id))
                    }
                ) {
                items(items = items, key = { it }) { it ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(24.dp),
                        text = "$it",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                    )
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
fun PlanListPreview(){
    PlanList()
}
