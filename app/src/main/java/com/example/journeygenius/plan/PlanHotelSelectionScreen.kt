package com.example.journeygenius.plan
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.journeygenius.R
import com.example.journeygenius.ui.theme.JourneyGeniusTheme
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanHotelSelectionScreen(viewModel: PlanViewModel,navController: NavController) {
    val attractionToHotels=remember{
        viewModel.attractionToHotels
    }
    val selectedHotelList= remember {
        viewModel.selectedHotelList
    }
    val selectedAttractionList =  remember{
        viewModel.selectedAttractionList
    }
    val startAttraction = remember{
        viewModel.startAttraction
    }
    val sliderValue = remember{
        viewModel.sliderValue
    }
    val singlePlan = remember {
        viewModel.singlePlan
    }
    val planList = remember {
        viewModel.planList
    }
    val planGroup= remember {
        viewModel.planGroup
    }
    val cameraPositionState= CameraPositionState(position= CameraPosition.fromLatLngZoom(LatLng(startAttraction.value.location.lat,startAttraction.value.location.lng),10f))
    LaunchedEffect(Unit) {
        selectedAttractionList.value.forEach {
            val hotelList=viewModel.searchNearbyHotels(it.location, apiKey = PlacesapiKey, maxPriceLevel = sliderValue.value?:4)
            viewModel.addAttractionToHotel(it,hotelList)
        }
    }
    println(attractionToHotels)

    JourneyGeniusTheme{
        Box(
            modifier = Modifier.fillMaxSize(),
        ){
            Column() {
                Box(contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 10.dp)){
                    Text(text = "Choose your hotels",
                        fontSize = MaterialTheme.typography.headlineLarge.fontSize)
                }
                GoogleMap(modifier = Modifier
                    .fillMaxWidth()
                    .height(550.dp),
                    cameraPositionState = cameraPositionState,
                    onMapLongClick = {
                        Log.d("selectedHotelOnMap",selectedHotelList.value.toString())
                        },
                    onMapClick = {}
                ){
                    if(selectedAttractionList.value.isNotEmpty()){
                        selectedAttractionList.value.forEach {
                            Marker(
                                state= MarkerState(position=LatLng(it.location.lat,it.location.lng)),
                                snippet = "Marker in ${it.name}",
                            )
                            if(attractionToHotels.value.containsKey(it) && !attractionToHotels.value[it].isNullOrEmpty()){
                                attractionToHotels.value[it]!!.forEach {hotel->
                                    Marker(
                                        state= MarkerState(position=LatLng(hotel.place.location.lat,hotel.place.location.lng)),
                                        snippet = "Marker in ${hotel.place.name}",
                                        icon = BitmapDescriptorFactory.defaultMarker(
                                            BitmapDescriptorFactory.HUE_GREEN),
                                        onClick={
                                            if(!selectedHotelList.value.contains(hotel)){
                                                viewModel.addSelectedHotel(hotel)
                                            }
                                            Log.d("selectedHotel",selectedHotelList.value.toString())
                                            return@Marker true

                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(selectedHotelList.value,key={it.place.name}){
                            Tag(title=it.place.name, onClose = {
                                viewModel.delSelectedHotel(it)
                                Log.d("selectedHotel",selectedHotelList.value.toString())
                            })
                        }
                    }
                }
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 85.dp),
                    contentAlignment = Alignment.BottomCenter
                ){
                    Button(onClick = {
                        viewModel.addHotelsToSinglePlan(selectedHotelList.value)
                        viewModel.addSinglePlan(singlePlan.value)
                        viewModel.updatePlanGroup(Plans(viewModel.planGroup.value.title,viewModel.planGroup.value.description,planList.value))
                        navController.navigate("Plan List")
                        Log.d("Plan",planGroup.value.toString())
                    }, modifier = Modifier
                        .width(130.dp)
                    ) {
                        Text(text = "Generate")
                    }
                }

            }
    }
    }
}

@Preview(showBackground = true)
@Composable
fun PreViewScreen(){
    PlanHotelSelectionScreen(viewModel = PlanViewModel(), navController = rememberNavController())
}