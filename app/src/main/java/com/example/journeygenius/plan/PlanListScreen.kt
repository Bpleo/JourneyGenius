package com.example.journeygenius.plan

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.journeygenius.ui.theme.JourneyGeniusTheme
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.journeygenius.JourneyGeniusViewModel
import com.example.journeygenius.R
import com.example.journeygenius.components.SwipeBackground
import com.example.journeygenius.data.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanList(
    navController: NavController,
    planViewModel: JourneyGeniusViewModel
) {
    val planTitle by remember {
        planViewModel.planTitle
    }
    val planDescription by remember {
        planViewModel.planDescription
    }
    val planList by remember {
        planViewModel.planList
    }
    val planGroup by remember {
        planViewModel.planGroup
    }
    val isPublic by remember {
        planViewModel.isPublic
    }
    val context = LocalContext.current
    val word =stringResource(R.string.save_successfully)

    JourneyGeniusTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp)
            ) {
                Text(
                    text = stringResource(R.string.plan_list),
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize
                )
            }
            Column(
                modifier = Modifier.padding(30.dp, 100.dp)
            ) {
                TextField(
                    value = planTitle,
                    onValueChange = {
                        planViewModel.updatePlanTitle(it)
                    },
                    label = {
                        Text(stringResource(R.string.title))
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                )
//                Text(
//                    text = planViewModel.planGroup.value.title,
//                    style = MaterialTheme.typography.bodyLarge,
//                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
//                )

            }
            Column(
                modifier = Modifier.padding(30.dp, 170.dp)
            ) {
//                Text(
//                    text = planViewModel.planGroup.value.description,
//                    style = MaterialTheme.typography.bodyLarge,
//                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
//                )
                TextField(
                    value = planDescription,
                    onValueChange = { planViewModel.updatePlanDescription(it) },
                    label = {
                        Text(stringResource(R.string.description))
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                )

            }
            Box(
                modifier = Modifier
                    .padding(30.dp, 250.dp)
                    .fillMaxSize()
            ) {
                if (planList.isNotEmpty()) {
                    LazyColumn(
                        state= rememberLazyListState(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        itemsIndexed(items=planList,
                        key = {index: Int, item: SinglePlan ->item.hashCode()  }
                            ) { index, item ->
                            val dismissState = rememberDismissState(
                                confirmValueChange = {
                                    // Done: call remove plan list func
                                    // TODO: Need Test
                                    planViewModel.delSinglePlan(item)
                                    true
                                },
                                positionalThreshold = {
                                    100.dp.toPx()
                                }
                            )
                            SwipeToDismiss(state = dismissState,
                                background = {
                                    SwipeBackground(dismissState = dismissState)
                                },
                                directions = setOf(DismissDirection.EndToStart),
                                dismissContent = {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                MaterialTheme.colorScheme.primaryContainer,
                                                RoundedCornerShape(16.dp)
                                            )
                                            .padding(24.dp)
                                            .clickable {
                                                planViewModel.updatePlanOnDetail(item)
                                                navController.navigate("Plan Detail")
                                            },
                                        text = "Trip to  ${item.destination}",
                                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                        fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                                    )
                                },


                            )
                        }
                    }
                } else {
                    Text(
                        color = Color.DarkGray, text = stringResource(R.string.empty_plan_list)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 90.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = planViewModel.isPublic.value,
                        onCheckedChange = { planViewModel.onPublicSwitched(it) },
                        modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                    )
                    Text(
                        text = if (planViewModel.isPublic.value) stringResource(R.string.Public) else stringResource(
                                                    R.string.Private),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    Button(
                        onClick = {
                            navController.navigate("Plan Menu")
                            planViewModel.updateSelectedAttractionList(listOf())
                            planViewModel.updateSelectedHotelList(listOf())
                            planViewModel.updateStartAttraction(
                                Place(
                                    "",
                                    "",
                                    Location(0.0, 0.0),
                                    0.0,
                                    "",
                                    emptyList()
                                )
                            )
                            planViewModel.updateEndAttraction(
                                Place(
                                    "",
                                    "",
                                    Location(0.0, 0.0),
                                    0.0,
                                    "",
                                    emptyList()
                                )
                            )
                            planViewModel.updateDepartCountry(planViewModel.destCountry.value)
                            planViewModel.updateDepartState(planViewModel.destState.value)
                            planViewModel.updateDepartCity(planViewModel.destCity.value)
                            planViewModel.updateDestCountry("")
                            planViewModel.updateDestState("")
                            planViewModel.updateDestCity("")
                            planViewModel.updateSelectedPlacesOnMap(hashMapOf())
                        },
                        modifier = Modifier.width(100.dp)
                    ) {
                        Text(text = stringResource(R.string.add))
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Button(
                        onClick = {
                            navController.navigate("Plan Menu")


                            planViewModel.addPlanGroupToList(planGroup)
                            //update to firestore

                            planViewModel.updateSelectedAttractionList(listOf())
                            planViewModel.updateSelectedHotelList(listOf())
                            planViewModel.updatePlanList(emptyList())
                            planViewModel.updateSinglePlan(
                                SinglePlan(
                                    "", "", listOf(),
                                    Place("", "", Location(0.0, 0.0), 0.0, "", listOf()),
                                    Place("", "", Location(0.0, 0.0), 0.0, "", listOf()),
                                    listOf(), 4, "extravagant", listOf(), ""
                                )
                            )
                            planViewModel.updatePlanGroup(Plans("", "", isPublic, listOf(),0,""))
                            planViewModel.updateStartAttraction(
                                Place(
                                    "",
                                    "",
                                    Location(0.0, 0.0),
                                    0.0,
                                    "",
                                    emptyList()
                                )
                            )
                            planViewModel.updateEndAttraction(
                                Place(
                                    "",
                                    "",
                                    Location(0.0, 0.0),
                                    0.0,
                                    "",
                                    emptyList()
                                )
                            )
                            planViewModel.updatePlanDescription("")
                            planViewModel.updatePlanTitle("")
                            planViewModel.onSliderValueChanged(0)
                            planViewModel.updateDestCountry("")
                            planViewModel.updateDestState("")
                            planViewModel.updateDestCity("")
                            planViewModel.updateDepartCountry("")
                            planViewModel.updateDepartState("")
                            planViewModel.updateDepartCity("")
                            planViewModel.updateSelectedPlacesOnMap(hashMapOf())

                            Toast.makeText(
                                context,
                                word,
                                Toast.LENGTH_SHORT
                            ).show()
                        }, modifier = Modifier
                            .width(125.dp)
                    ) {
                        Text(text = stringResource(R.string.complete))
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PlanListPreview() {
    PlanList(
        navController = rememberNavController(), planViewModel = JourneyGeniusViewModel(
            FirebaseFirestore.getInstance(),
            FirebaseAuth.getInstance(),
            FirebaseDatabase.getInstance().reference
        )
    )
}
