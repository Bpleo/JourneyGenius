package com.example.journeygenius.plan

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.journeygenius.JourneyGeniusViewModel
import com.example.journeygenius.PlacesapiKey
import com.example.journeygenius.ui.theme.JourneyGeniusTheme
import com.google.android.gms.maps.model.LatLng

fun generateWaypoints(viewModel: JourneyGeniusViewModel):String{
    val sb=StringBuilder()
    for(i in 0 until  viewModel.attractionRoutes.value.size-1){
        sb.append("${viewModel.attractionRoutes.value[i].location.lat}%2C${viewModel.attractionRoutes.value[i].location.lng}%7C")
    }
    sb.append("${viewModel.attractionRoutes.value[viewModel.attractionRoutes.value.size-1].location.lat}%2C${viewModel.attractionRoutes.value[viewModel.attractionRoutes.value.size-1].location.lng}")
    Log.d("waypoints",sb.toString())
    return sb.toString()
}

@Composable
fun PlanDetail(navController: NavController, viewModel: JourneyGeniusViewModel){
    val planOnDetail= remember{
        viewModel.planOnDetail
    }
    val content= LocalContext.current
    val waypoints by remember { mutableStateOf(generateWaypoints(viewModel)) }

    JourneyGeniusTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
        ){

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(15.dp, 20.dp)) {
                        Box(contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 20.dp)){
                            Text(text = "Summary",
                                fontSize = MaterialTheme.typography.headlineLarge.fontSize)
                        }
                        Text(
                            text = "Date: ",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = planOnDetail.value.date,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize
                            )
                        }
                        Spacer(modifier = Modifier.height(25.dp))
                        Text(
                            text = "Destination: ",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = planOnDetail.value.destination,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize
                            )
                        }
                        Spacer(modifier = Modifier.height(25.dp))
                        Text(
                            text = "Attractions: ",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            LazyColumn(modifier = Modifier.fillMaxWidth())
                            {
                                items(planOnDetail.value.attractions, key = { it.name }) {
                                    Text(
                                        text = it.name,
                                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            text = "Price Level: ",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "${planOnDetail.value.priceLevel}: ${planOnDetail.value.priceLevelLabel}",
                                fontSize = MaterialTheme.typography.titleLarge.fontSize
                            )
                        }
                        Spacer(modifier = Modifier.height(35.dp))
                        Text(
                            text = "Hotel: ",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            LazyColumn(modifier = Modifier.fillMaxWidth())
                            {
                                items(planOnDetail.value.hotel, key = { it.place.name }) {
                                    Text(
                                        text = it.place.name,
                                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            text = "Transport Mode: ",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = planOnDetail.value.travelType,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize
                            )
                        }
                        Spacer(modifier = Modifier.height(75.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 80.dp)
                                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Column(
                                modifier = Modifier
                                    .clickable(onClick = {
                                        val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=${planOnDetail.value.startAttraction.location.lat},${planOnDetail.value.startAttraction.location.lng}&destination=${planOnDetail.value.endAttraction.location.lat},${planOnDetail.value.endAttraction.location.lng}&travelmode=driving&waypoints=${waypoints}")
                                        val intent = Intent(Intent.ACTION_VIEW, uri)
                                        content.startActivity(intent)
                                    })
                            ) {
                                Image(
                                    painter = rememberImagePainter(
                                        data = "https://maps.googleapis.com/maps/api/staticmap?center=${planOnDetail.value.startAttraction.location.lat},${planOnDetail.value.startAttraction.location.lng}&markers=color:red%7Clabel:A%7C${planOnDetail.value.startAttraction.location.lat},${planOnDetail.value.startAttraction.location.lng}&markers=color:red%7Clabel:B%7C${planOnDetail.value.endAttraction.location.lat},${planOnDetail.value.endAttraction.location.lng}&zoom=13&size=300x300&key=$PlacesapiKey"
                                    ),
                                    contentDescription = "Google Map Thumbnail",
                                    modifier = Modifier
                                        .height(200.dp)
                                        .fillMaxWidth(),
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    text = "Click on the map to open in Google Maps",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = { navController.navigate("Plan List") },
                                    modifier = Modifier
                                        .width(100.dp)
                                        .align(Alignment.CenterHorizontally)
                                ) {
                                    Text(text = "Back")
                                }

                            }
                        }
                    }


                }

            }

        }
    }
}
//@Preview(showBackground = true)
//@Composable
//fun PlanDetailPreview(){
//    val viewModel=  PlanViewModel()
//    PlanDetail(rememberNavController(),viewModel)
//}
