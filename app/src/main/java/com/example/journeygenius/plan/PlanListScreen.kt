package com.example.journeygenius.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.journeygenius.ui.theme.JourneyGeniusTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanList(viewModel: PlanListViewModel = viewModel(),navController: NavController) {
    JourneyGeniusTheme {
        val items = viewModel.items
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
//                Text(
//                    text = "Enter some text",
//                    style = MaterialTheme.typography.bodyLarge
//                )
                TextField(
                    value = viewModel.titleState.value,
                    onValueChange = { viewModel.titleState.value = it },
                    label = {
                        Text("User Name: ")
                    },
                    placeholder = {
                        Text(text = "Enter your User Name")
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                )
            }
            Column(
                modifier = Modifier.padding(30.dp,200.dp)
            ) {
//                Text(
//                    text = "Enter some text",
//                    style = MaterialTheme.typography.bodyLarge
//                )
                TextField(
                    value = viewModel.descriptionState.value,
                    onValueChange = { viewModel.descriptionState.value = it },
                    label = {
                        Text("User Name: ")
                    },
                    placeholder = {
                        Text(text = "Enter your User Name")
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                )
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp, 300.dp)
                    .clickable {
                        navController.navigate("Plan Detail")
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
                .padding(bottom = 90.dp),
                contentAlignment = Alignment.BottomCenter
            ){
                Button(onClick = { navController.navigate("Plan Menu")}, modifier = Modifier
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
    PlanList(navController = rememberNavController())
}
