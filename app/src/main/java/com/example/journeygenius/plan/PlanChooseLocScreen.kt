package com.example.journeygenius.plan
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.location.Address
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.example.journeygenius.ui.theme.JourneyGeniusTheme
import androidx.compose.material.*
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.journeygenius.JourneyGeniusViewModel
import com.example.journeygenius.R
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

import java.util.*
import kotlin.collections.HashMap

fun bitmapDescriptorFromVector(
    context: Context,
    vectorResId: Int
): BitmapDescriptor? {

    // retrieve the actual drawable
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bm = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    // draw it onto the bitmap
    val canvas = android.graphics.Canvas(bm)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}
@Composable
fun Tag(title: String, onClose: () -> Unit) {
    Row(
        Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(Color.LightGray, RoundedCornerShape(16.dp))
            .clickable { onClose() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, Modifier.padding(horizontal = 8.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_close),
            contentDescription = null,
            Modifier
                .padding(end = 8.dp)
                .clickable { onClose() }
        )
    }
}



@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanChooseLocScreen(viewModel: PlanViewModel,navController: NavController,journeyGeniusViewModel: JourneyGeniusViewModel) {
    val countries = listOf("China", "Japan", "Korea", "US", "UK")
    val usStates = listOf("AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID",
        "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN")
    val chnStates = listOf("Guangdong", "Hainan", "Beijing", "Jiangsu", "Jiangxi", "Guangxi",
        "Sichuan", "Yunan", "Fujian")
    val jpStates = listOf("Hokkaido", "Aomori", "Iwate", "Miyagi", "Akita", "Yamagata", "Fukushima")
    val krStates = listOf( "Seoul", "Busan", "Daegu", "Incheon","Gwangju", "Daejeon", "Ulsan", "Sejong",)
    val ukStates = listOf(
        "England", "Scotland", "Wales", "Northern Ireland")

    val countryToStateMap = mapOf(
        "China" to chnStates,
        "US" to usStates,
        "Japan" to jpStates,
        "Korea" to krStates,
        "UK" to ukStates
    )
    val engCities = listOf(
        "Bath",
        "Birmingham",
        "Bradford",
        "Brighton and Hove",
        "Bristol",
        "Cambridge",
        "Canterbury",
        "Carlisle",
        "Chester"
    )
    val guangdongCities = listOf("Shenzhen", "Guangzhou", "Zhuhai")
    val hokCities = listOf(
        "Sapporo", "Hakodate", "Asahikawa", "Obihiro", "Kushiro"
    )
    val bosCities = listOf(
        "Boston", "Worcester", "Springfield", "Lowell"
    )
    val seoulCities= listOf("Seoul")
    val stateToCityMap = mapOf(
        "England" to engCities,
        "Guangdong" to guangdongCities,
        "Hokkaido" to hokCities,
        "MA" to bosCities,
        "Seoul" to seoulCities,
    )

    val label1 = "Bicycle"
    val label2 = "Train/Bus"
    val label3 = "Car"
    val label4 = "All"

    var startAttractionExpanded by remember { mutableStateOf(false) }
    var endAttractionExpanded by remember { mutableStateOf(false) }

    var departCountryExpanded by remember { mutableStateOf(false) }
    var departStateExpanded by remember {
        mutableStateOf(false)
    }
    var departCityExpanded by remember {
        mutableStateOf(false)
    }
    var destCountryExpanded by remember {
        mutableStateOf(false)
    }
    var destStateExpanded by remember {
        mutableStateOf(false)
    }
    var destCityExpanded by remember {
        mutableStateOf(false)
    }
    val departCountry by remember {
        mutableStateOf(viewModel.departCountry)
    }
    val startAttraction by remember {
        mutableStateOf(viewModel.startAttraction)
    }

    val endAttraction by remember {
        mutableStateOf(viewModel.endAttraction)
    }
    val departSate by remember {
        mutableStateOf(viewModel.departState)
    }
    val departCity by remember {
        mutableStateOf(viewModel.departCity)
    }

    val destCountry by remember {
        mutableStateOf(viewModel.destCountry)
    }
    val destState by remember {
        mutableStateOf(viewModel.destState)
    }
    val destCity by remember {
        mutableStateOf(viewModel.destCity)
    }
    val selectedCityLocation = remember { viewModel.selectedCityLocation  }
    val selectedCityLatLng = remember {
        viewModel.selectedCityLatLng
    }
    val attractionsList = remember{viewModel.attractionsList }
    val selectedAttractionList = remember {
        viewModel.selectedAttractionList
    }
    val nameList: List<String> = selectedAttractionList.value.map { place -> place.name }

    var textFiledSize by remember {
        mutableStateOf(Size.Zero)
    }
    val selectedPlacesOnMap by remember{
        viewModel.selectedPlacesOnMap
    }
    val singlePlan by remember {
        viewModel.singlePlan
    }
    val planGroup by remember {
        viewModel.planGroup
    }
    val planList by remember {
        viewModel.planList
    }

    val iconStartAttraction = if (startAttractionExpanded) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }
    val iconEndAttraction = if (endAttractionExpanded) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }
    val iconDepartCountry = if (departCountryExpanded) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }

    val iconDepartState = if (departStateExpanded) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }
    val iconDepartCity = if (departCityExpanded) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }

    val iconDestinCountry = if (destCountryExpanded) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }
    val iconDestinState = if (destStateExpanded) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }
    val iconDestinCity = if (destCityExpanded) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }
    val context = LocalContext.current

    val boston=LatLng(42.36, -71.05)
    val cameraPositionState= CameraPositionState(position= CameraPosition.fromLatLngZoom(selectedCityLocation.value,10f))
    viewModel.viewModelScope.launch {
        val location = viewModel.selectedCityLatLng.value
        viewModel.searchNearbyPlaces(Location(location[0],location[1]), apiKey = PlacesapiKey)
    }


    JourneyGeniusTheme {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 15.dp)
                ) {
                    //Departure
                    Column(
                        modifier = Modifier.width(170.dp)
                    ) {
                        Text(
                            text = "Departure",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Column {

                            //Departure Sight
                            OutlinedTextField(value = startAttraction.value.name,
                                onValueChange = {
                                    val updatedPlace = Place(it, startAttraction.value.vicinity, startAttraction.value.location)
                                    viewModel.updateStartAttraction(updatedPlace)
                                },
                                modifier = Modifier.width(170.dp).height(70.dp),
                                label = { Text(text = "Choose") },
                                trailingIcon = {
                                    Icon(
                                        iconStartAttraction,
                                        contentDescription = "",
                                        Modifier.clickable {
                                            startAttractionExpanded = !startAttractionExpanded
                                        })
                                })
                            DropdownMenu(
                                expanded = startAttractionExpanded,
                                onDismissRequest = { startAttractionExpanded = false },
                                modifier = Modifier.width(170.dp),
//                            modifier = Modifier.width(with(LocalDensity.current) { textFiledSize.width.toDp() })
                            ) {
                                nameList.filter { it != endAttraction.value.name }.forEach { attraction ->
                                    DropdownMenuItem(text = { Text(attraction) }, onClick = {
                                        val updatedPlace = Place(attraction, startAttraction.value.vicinity, startAttraction.value.location)
                                        viewModel.updateStartAttraction(updatedPlace)
                                        startAttractionExpanded = false
                                    })

                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                    //Destination

                    Column(
                        modifier = Modifier.width(170.dp)
                    ) {
                        Text(
                            text = "Destination",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Column {

                            //Destination Sight
                            OutlinedTextField(value = endAttraction.value.name,
                                onValueChange = {
                                    val updatedPlace = Place(it, endAttraction.value.vicinity, endAttraction.value.location)
                                    viewModel.updateEndAttraction(updatedPlace)
                                },
                                modifier = Modifier.width(170.dp).height(70.dp),
                                label = { Text(text = "Choose") },
                                trailingIcon = {
                                    Icon(
                                        iconEndAttraction,
                                        contentDescription = "",
                                        Modifier.clickable {
                                            endAttractionExpanded = !endAttractionExpanded
                                        })
                                })
                            DropdownMenu(
                                expanded = endAttractionExpanded,
                                onDismissRequest = { endAttractionExpanded = false },
                                modifier = Modifier.width(170.dp),
//                            modifier = Modifier.width(with(LocalDensity.current) { textFiledSize.width.toDp() })
                            ) {
                                nameList.filter { it != startAttraction.value.name }.forEach { attraction ->
                                    DropdownMenuItem(text = { Text(attraction) }, onClick = {
                                        val updatedPlace = Place(attraction, endAttraction.value.vicinity, endAttraction.value.location)
                                        viewModel.updateEndAttraction(updatedPlace)
                                        endAttractionExpanded = false
                                    })
                                }
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(), contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = "Select your preferred travel mode:",
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    )
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)) {

                    RadioButton(
                        selected = viewModel.travelModeOption.value == "Bicycle",
                        onClick = { viewModel.onTravelModeChanged("Bicycle") },
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 12.dp),
                    )
                    Text(
                        text = label1,
                        modifier = Modifier.padding(end = 16.dp)
                    )

                    RadioButton(
                        selected = viewModel.travelModeOption.value == "Train/Bus",
                        onClick = { viewModel.onTravelModeChanged("Train/Bus") },
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 12.dp),
                    )
                    Text(
                        text = label2,
                        modifier = Modifier.padding(end = 16.dp)
                    )

                    RadioButton(
                        selected = viewModel.travelModeOption.value == "Car",
                        onClick = { viewModel.onTravelModeChanged("Car") },
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 12.dp),
                    )
                    Text(
                        text = label3,
                        modifier = Modifier.padding(end = 16.dp)
                    )

                    RadioButton(
                        selected = viewModel.travelModeOption.value == "All",
                        onClick = { viewModel.onTravelModeChanged("All") },
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 12.dp),
                    )
                    Text(
                        text = label4,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(), contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = "Choose Your Interests",
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize
                        )
                    }

                    GoogleMap(modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                        cameraPositionState = cameraPositionState,
                        onMapLongClick = {
                            viewModel.viewModelScope.launch {
                                val places= viewModel.getNearbyPlaces(Location(it.latitude,it.longitude), apiKey = PlacesapiKey)
                                viewModel.addSelectedPlacesOnMap(Pair(it.latitude,it.longitude),places)

                            }
                            Log.d("SelectedPlacesOnMap",selectedPlacesOnMap.toString())
                        }

                    ){

                        Marker(
                            state = MarkerState(position = selectedCityLocation.value),
                            title = destCity.value,
                            snippet = "Marker in ${destCity.value}"

                        )
                        if(selectedPlacesOnMap.isNotEmpty()){
                            selectedPlacesOnMap.forEach{place->
                                Marker(
                                    state=MarkerState(position=LatLng(place.key.first,place.key.second)),
                                    snippet = "Marker in ($place.key.first},${place.key.second})",
                                    icon=bitmapDescriptorFromVector(context, R.drawable.pin2),
                                    onClick = {
                                        if(selectedPlacesOnMap.containsKey(place.key)){
                                            viewModel.delSelectedPlacesOnMap(place.key)
                                        }
                                        Log.d("selectedplacesOnMap", selectedPlacesOnMap.toString())
                                        return@Marker true
                                    }
                                )
                                place.value.forEach {attr->
                                    Marker(
                                        state = MarkerState(position = LatLng(attr.location.lat,attr.location.lng)),
                                        title = attr.name,
                                        snippet = "Marker in ${attr.name}",
                                        icon = bitmapDescriptorFromVector(
                                            context, R.drawable.pin
                                        ),
                                        onClick = {
                                            if(!selectedAttractionList.value.contains(attr)){
                                                viewModel.addSelectedAttraction(attr)
                                            }
                                            Log.d("selectedAttractionList: ",viewModel.selectedAttractionList.value.toString())
                                            return@Marker true
                                        }

                                    )
                                }

                            }
                        }

                        if( attractionsList.value.isNotEmpty()){
                            attractionsList.value.forEach{place ->
                                Marker(
                                    state = MarkerState(position = LatLng(place.location.lat,place.location.lng),),
                                    title = place.name,
                                    snippet = "Marker in ${place.name}",
                                    icon = bitmapDescriptorFromVector(
                                        context, R.drawable.pin
                                    ),
                                    onClick = {
                                        if(!selectedAttractionList.value.contains(place)){
                                            viewModel.addSelectedAttraction(place)
                                        }
                                        Log.d("selectedAttractionList: ",viewModel.selectedAttractionList.value.toString())
                                        return@Marker true
                                    }

                                )
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
                            items(selectedAttractionList.value,key={it.name}){
                                Tag(title=it.name, onClose = {
                                    viewModel.delSelectedAttraction(it)
                                    Log.d("selectedAttractionList: ",viewModel.selectedAttractionList.value.toString())
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

                            viewModel.updateSinglePlan(singlePlan(
                                "${journeyGeniusViewModel.dateRange.value.lower} - ${journeyGeniusViewModel.dateRange.value.upper}",
                                "${destCountry.value}  ${destState.value}  ${destCity.value}",
                                selectedAttractionList.value,
                                journeyGeniusViewModel.budget.value.text,
                                listOf(),
                                journeyGeniusViewModel.travelType.value

                            ))
                            if (selectedAttractionList.value.isNotEmpty()) {
                                viewModel.updateStartAttraction(selectedAttractionList.value[0])
                                viewModel.updateEndAttraction(selectedAttractionList.value[selectedAttractionList.value.size - 1])
                            }
//                                         viewModel.addSinglePlan(singlePlan)
//
//                                         viewModel.updatePlanGroup(Plans(viewModel.planGroup.value.title,viewModel.planGroup.value.description,planList))
                            navController.navigate("Plan Hotel")
                            Log.d("Plan",planGroup.toString())
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
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun dropDownMenu() {

    var expanded by remember { mutableStateOf(false) }
    val suggestions = listOf("Kotlin", "Java", "Dart", "Python")
    var selectedText by remember { mutableStateOf("") }

    var textfieldSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (expanded) Icons.Filled.KeyboardArrowUp
    else Icons.Filled.KeyboardArrowDown


    Column(Modifier.padding(20.dp)) {
        OutlinedTextField(value = selectedText,
            onValueChange = { selectedText = it },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    //This value is used to assign to the DropDown the same width
                    textfieldSize = coordinates.size.toSize()
                },
            label = { Text("Label") },
            trailingIcon = {
                Icon(icon, "contentDescription", Modifier.clickable { expanded = !expanded })
            })
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(with(LocalDensity.current) { textfieldSize.width.toDp() })
        ) {
            suggestions.forEach { label ->
                DropdownMenuItem(onClick = {
                    selectedText = label
                    expanded = false
                }, text = ({ Text(label) })
                )
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun PlanChooseLocScreenPreview() {
    PlanChooseLocScreen(PlanViewModel(), rememberNavController(), JourneyGeniusViewModel(Firebase.firestore, Firebase.auth))
//    dropDownMenu()
//    Tag(title = "Shenzhen", onClose = {})
}