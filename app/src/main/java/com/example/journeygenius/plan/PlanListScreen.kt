package com.example.journeygenius.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.journeygenius.ui.theme.JourneyGeniusTheme
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.journeygenius.JourneyGeniusViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanList(navController: NavController, planViewModel: JourneyGeniusViewModel) {
    val planTitle =remember{
        planViewModel.planTitle
    }
    val planDescription=remember{
        planViewModel.planDescription
    }
    val planList = remember{
        planViewModel.planList
    }
    val planGroup=remember{
        planViewModel.planGroup
    }
    JourneyGeniusTheme {
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
            Column(
                modifier = Modifier.padding(30.dp,100.dp)
            ) {
                TextField(
                    value = planTitle.value,
                    onValueChange = {
                        planViewModel.updatePlanTitle(it)
                    },
                    label = {
                        Text("Title: ")
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                )
//                Text(
//                    text = planViewModel.planGroup.value.title,
//                    style = MaterialTheme.typography.bodyLarge,
//                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
//                )

            }
            Column(
                modifier = Modifier.padding(30.dp,200.dp)
            ) {
//                Text(
//                    text = planViewModel.planGroup.value.description,
//                    style = MaterialTheme.typography.bodyLarge,
//                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
//                )
                TextField(
                    value = planDescription.value,
                    onValueChange = {
                        planViewModel.updatePlanDescription(it)
                    },
                    label = {
                        Text("Description: ")
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                )
            }
            Box(modifier = Modifier
                .padding(30.dp, 300.dp)
                .fillMaxSize()){
                if(planList.value.isNotEmpty()) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        itemsIndexed(planList.value) { index, plan ->
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(24.dp)
                                    .clickable {
                                        planViewModel.updatePlanOnDetail(plan)
                                        navController.navigate("Plan Detail")
                                    },
                                text = "Trip to  ${plan.destination}",
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                            )
                        }


                    }
                }else{
                    Text(color = Color.DarkGray
                        ,text = "Empty. Waiting to explore the world"  )
                }
            }

            Box(modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp),
                contentAlignment = Alignment.BottomCenter
            ){
                Row() {
                    Button(onClick = { navController.navigate("Plan Menu")
                        planViewModel.updateSelectedAttractionList(listOf())
                        planViewModel.updateSelectedHotelList(listOf())
                    }, modifier = Modifier
                        .width(100.dp)
                    ) {
                        Text(text = "ADD")
                    }
                    Spacer(modifier = Modifier.width(50.dp))
                    Button(onClick = { navController.navigate("Plan Menu")
                        planViewModel.updateSelectedAttractionList(listOf())
                        planViewModel.updateSelectedHotelList(listOf())
                    }, modifier = Modifier
                        .width(170.dp)
                    ) {
                        Text(text = "COMPLETE")
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PlanListPreview(){
    PlanList(navController = rememberNavController(), planViewModel = JourneyGeniusViewModel(
        FirebaseFirestore.getInstance(), FirebaseAuth.getInstance())
    )
}
