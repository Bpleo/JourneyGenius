package com.example.journeygenius.plan

import android.util.Log
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.journeygenius.ui.theme.JourneyGeniusTheme

@Composable
fun PlanDetail(navController: NavController,viewModel: PlanViewModel){
    val planGroup= remember {
        viewModel.planGroup
    }
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
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = planGroup.value.plans[0].date,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
                Spacer(modifier = Modifier.height(25.dp))
                Text(text = "Destination: ",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = planGroup.value.plans[0].destination,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
                Spacer(modifier = Modifier.height(25.dp))
                Text(text = "Attractions: ",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize)
                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.fillMaxWidth().height(90.dp)){
                    LazyColumn(modifier = Modifier.fillMaxWidth())
                    {
                        items(planGroup.value.plans[0].attractions,key={it.name}){
                            Text(text = it.name,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))
                Text(text = "Budget: ",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = planGroup.value.plans[0].budget,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
                Spacer(modifier = Modifier.height(35.dp))
                Text(text = "Hotel: ",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = planGroup.value.plans[0].hotel.toString(),
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
                Spacer(modifier = Modifier.height(35.dp))
                Text(text = "Transport Mode: ",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = planGroup.value.plans[0].trans_mode,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
                Spacer(modifier = Modifier.height(75.dp))
            }


        }
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
            contentAlignment = Alignment.BottomCenter
        ){
            Button(onClick = { navController.navigate("Plan List") }, modifier = Modifier
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
    val viewModel=  PlanViewModel()
    PlanDetail(rememberNavController(),viewModel)
}
