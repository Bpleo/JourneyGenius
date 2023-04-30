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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.journeygenius.JourneyGeniusViewModel
import com.example.journeygenius.R
import com.example.journeygenius.ui.theme.JourneyGeniusTheme
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import java.util.ArrayList

fun getURL(from : LatLng, to : LatLng, apiKey:String, waypoints:List<LatLng>,travelModeOption:String) : String {
    val origin = "origin=" + from.latitude + "," + from.longitude
    val dest = "destination=" + to.latitude + "," + to.longitude
    val Key= "key=$apiKey"
    var waypointString= ""
    if(waypoints.isNotEmpty()){
        for (i in 0 until waypoints.size-1){
            waypointString+="${waypoints[i].latitude}%2C${waypoints[i].longitude}%7C"
        }
        waypointString+="${waypoints[waypoints.size-1].latitude}%2C${waypoints[waypoints.size-1].longitude}"
    }
    val params = "$origin&$dest&$Key&waypoints=$waypointString&mode=$travelModeOption"
    return "https://maps.googleapis.com/maps/api/directions/json?$params"
}

fun decodePoly(encoded: String): List<LatLng> {
    val poly = ArrayList<LatLng>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].toInt() - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0
        do {
            b = encoded[index++].toInt() - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        val p = LatLng(lat.toDouble() / 1E5,
            lng.toDouble() / 1E5)
        poly.add(p)
    }

    return poly
}

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
    val endAttraction = remember {
        viewModel.endAttraction
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
    val travelModeOption = remember{
        viewModel.travelModeOption
    }
    val cameraPositionState= CameraPositionState(position= CameraPosition.fromLatLngZoom(LatLng(startAttraction.value.location.lat,startAttraction.value.location.lng),15f))
    var polylinePoints by remember { mutableStateOf(emptyList<List<LatLng>>()) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        selectedAttractionList.value.forEach {
            val hotelList=viewModel.searchNearbyHotels(it.location, apiKey = PlacesapiKey, maxPriceLevel = sliderValue.value?:4)
            viewModel.addAttractionToHotel(it,hotelList)
        }
    }
    val from =LatLng(startAttraction.value.location.lat,startAttraction.value.location.lng)
    val to=LatLng(endAttraction.value.location.lat,endAttraction.value.location.lng)
    val waypoints= selectedAttractionList.value.toMutableList()
    println(waypoints.toString())
    if (waypoints.isNotEmpty()){
        val first=waypoints[0]
        val last=waypoints[waypoints.size-1]
        waypoints.remove(first)
        waypoints.remove(last)
    }
    println(waypoints.toString())
    val waypointsLatLng:MutableList<LatLng> = mutableListOf();
    waypoints.forEach{
        waypointsLatLng.add(LatLng(it.location.lat,it.location.lng))
    }

    LaunchedEffect(key1 = from, key2 =to ) {
        val points = viewModel.getRoutes(from, to, PlacesapiKey,waypointsLatLng,travelModeOption.value)
        coroutineScope.launch {
            polylinePoints = points
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
                    if (polylinePoints.isNotEmpty()){
                        for (i in polylinePoints.indices){
                            if(i==0){
                                Polyline(points = polylinePoints.get(0),
                                    color = Color.Blue,
                                )
                            }else{
                                Polyline(points = polylinePoints.get(i),
                                    color = Color.Cyan,
                                )
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

//@Preview(showBackground = true)
//@Composable
//fun PreViewScreen(){
//    PlanHotelSelectionScreen(viewModel = PlanViewModel(), navController = rememberNavController(), journeyGeniusViewModel = JourneyGeniusViewModel())
//}