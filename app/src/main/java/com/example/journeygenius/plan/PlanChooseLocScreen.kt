package com.example.journeygenius.plan

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
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
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.journeygenius.*
import com.example.journeygenius.R
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import com.example.journeygenius.data.models.*
import java.util.*

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
//@Composable
//fun MarkerContent(name:String,vicinity:String, rating:Double){
//    Box(
//        modifier = Modifier
//            .background(
//                color = MaterialTheme.colorScheme.onPrimary,
//                shape = RoundedCornerShape(35.dp, 35.dp, 35.dp, 35.dp)
//            )
//        ,
//    ) {
//
//        Column(
//            modifier = Modifier.padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
////            AsyncImage(model = getPhotoUrl("AZose0kKkd8V2iQJF3rZH9c73oQ6cWgAqR5Q4KbHbPRYhOl-EC17xQccIJ8dugpEiIgJ_QcgpRaDRc_6p5ZwLkUzzQunXbqIqDWtaeoUSMYbwp-ss735WfjNOZR8PUf9OLDVSvB4_8NauLlNamZYSXXw152PzA8nr2I1kvaDTb8RjaVImcWD",
////                PlacesapiKey), contentDescription = null,
////                modifier = Modifier
////                    .height(80.dp)
////                    .fillMaxWidth(), )
//            Image(
//                painter = painterResource(id = R.drawable.map),
//                contentDescription = null,
//                contentScale = ContentScale.Fit,
//                modifier = Modifier
//                    .height(80.dp)
//                    .fillMaxWidth(),
//
//                )
//            //.........................Spacer
////            Spacer(modifier = Modifier.height(10.dp))
//            //.........................Text: title
//            Text(
//                text = name,
//                textAlign = TextAlign.Center,
//                modifier = Modifier
//                    .padding(top = 10.dp)
//                    .fillMaxWidth(),
//                style = MaterialTheme.typography.headlineSmall,
//                color = MaterialTheme.colorScheme.primary,
//            )
////            Spacer(modifier = Modifier.height(5.dp))
//            //.........................Text : description
//            Text(
//                text = vicinity,
//                textAlign = TextAlign.Center,
//                modifier = Modifier
//                    .padding(top = 10.dp, start = 25.dp, end = 25.dp)
//                    .fillMaxWidth(),
//                style = MaterialTheme.typography.bodyLarge,
//                color = MaterialTheme.colorScheme.primary,
//            )
//            Spacer(modifier = Modifier.height(5.dp))
//            Text(
//                text = "Rating: $rating",
//                textAlign = TextAlign.Center,
//                modifier = Modifier
//                    .padding(top = 10.dp, start = 25.dp, end = 25.dp)
//                    .fillMaxWidth(),
//                style = MaterialTheme.typography.bodyLarge,
//                color = MaterialTheme.colorScheme.primary,
//            )
//
//
//        }
//
//    }
//
//}


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanChooseLocScreen(
    viewModel: JourneyGeniusViewModel,
    windowSize: WindowSize,
    navController: NavController
) {
    val countries = listOf("China", "Japan", "Korea", "US", "UK")
    val usStates = listOf(
        "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID",
        "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN"
    )
    val chnStates = listOf(
        "Guangdong", "Hainan", "Beijing", "Jiangsu", "Jiangxi", "Guangxi",
        "Sichuan", "Yunan", "Fujian"
    )
    val jpStates = listOf("Hokkaido", "Aomori", "Iwate", "Miyagi", "Akita", "Yamagata", "Fukushima")
    val krStates =
        listOf("Seoul", "Busan", "Daegu", "Incheon", "Gwangju", "Daejeon", "Ulsan", "Sejong")
    val ukStates = listOf(
        "England", "Scotland", "Wales", "Northern Ireland"
    )

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
    val seoulCities = listOf("Seoul")
    val stateToCityMap = mapOf(
        "England" to engCities,
        "Guangdong" to guangdongCities,
        "Hokkaido" to hokCities,
        "MA" to bosCities,
        "Seoul" to seoulCities,
    )

    val label1 = stringResource(R.string.bicycling)
    val label2 = stringResource(R.string.walking)
    val label3 = stringResource(R.string.Driving)
    val label4 = stringResource(R.string.transit)

    var startAttractionExpanded by remember { mutableStateOf(false) }
    var endAttractionExpanded by remember { mutableStateOf(false) }

    val departCountryExpanded by remember { mutableStateOf(false) }
    val departStateExpanded by remember {
        mutableStateOf(false)
    }
    val departCityExpanded by remember {
        mutableStateOf(false)
    }
    val destCountryExpanded by remember {
        mutableStateOf(false)
    }
    val destStateExpanded by remember {
        mutableStateOf(false)
    }
    val destCityExpanded by remember {
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
    val selectedCityLocation = remember { viewModel.selectedCityLocation }
    val selectedCityLatLng = remember {
        viewModel.selectedCityLatLng
    }
    val attractionsList = remember { viewModel.attractionsList }
    val selectedAttractionList = remember {
        viewModel.selectedAttractionList
    }

    var textFiledSize by remember {
        mutableStateOf(Size.Zero)
    }
    val selectedPlacesOnMap by remember {
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

    val boston = LatLng(42.36, -71.05)
    val cameraPositionState = CameraPositionState(
        position = CameraPosition.fromLatLngZoom(
            selectedCityLocation.value,
            10f
        )
    )
    viewModel.viewModelScope.launch {
        val location = viewModel.selectedCityLatLng.value
        viewModel.searchNearbyPlaces(Location(location[0], location[1]), apiKey = PlacesapiKey)
    }


    JourneyGeniusTheme {
        when (windowSize.height) {
            WindowType.Medium -> {
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
                                    text = stringResource(id = R.string.departure),
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Column {

                                    //Departure Sight
                                    OutlinedTextField(value = startAttraction.value.name,
                                        onValueChange = {

                                        },
                                        modifier = Modifier
                                            .width(170.dp)
                                            .height(70.dp),
                                        label = { Text(text = stringResource(R.string.choose)) },
                                        trailingIcon = {
                                            Icon(
                                                iconStartAttraction,
                                                contentDescription = "",
                                                Modifier.clickable {
                                                    startAttractionExpanded =
                                                        !startAttractionExpanded
                                                })
                                        })
                                    DropdownMenu(
                                        expanded = startAttractionExpanded,
                                        onDismissRequest = { startAttractionExpanded = false },
                                        modifier = Modifier.width(170.dp),
//                            modifier = Modifier.width(with(LocalDensity.current) { textFiledSize.width.toDp() })
                                    ) {
                                        selectedAttractionList.value.filter { it != endAttraction.value }
                                            .forEach { attraction ->
                                                DropdownMenuItem(
                                                    text = { Text(attraction.name) },
                                                    onClick = {
                                                        viewModel.updateStartAttraction(attraction)
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
                                    text = stringResource(id = R.string.destination),
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Column {

                                    //Destination Sight
                                    OutlinedTextField(value = endAttraction.value.name,
                                        onValueChange = {

                                        },
                                        modifier = Modifier
                                            .width(170.dp)
                                            .height(70.dp),
                                        label = { Text(text = stringResource(id = R.string.choose)) },
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
                                        selectedAttractionList.value.filter { it != startAttraction.value }
                                            .forEach { attraction ->
                                                DropdownMenuItem(
                                                    text = { Text(attraction.name) },
                                                    onClick = {
                                                        viewModel.updateEndAttraction(attraction)
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
                                text = stringResource(R.string.select_travel_mode),
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {

                            RadioButton(
                                selected = viewModel.travelModeOption.value == "bicycling",
                                onClick = { viewModel.onTravelModeChanged("bicycling") },
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(end = 12.dp),
                            )
                            Text(
                                text = label1,
                                modifier = Modifier.padding(end = 16.dp)
                            )

                            RadioButton(
                                selected = viewModel.travelModeOption.value == "walking",
                                onClick = { viewModel.onTravelModeChanged("walking") },
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(end = 12.dp),
                            )
                            Text(
                                text = label2,
                                modifier = Modifier.padding(end = 16.dp)
                            )

                            RadioButton(
                                selected = viewModel.travelModeOption.value == "driving",
                                onClick = { viewModel.onTravelModeChanged("driving") },
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(end = 12.dp),
                            )
                            Text(
                                text = label3,
                                modifier = Modifier.padding(end = 16.dp)
                            )

                            RadioButton(
                                selected = viewModel.travelModeOption.value == "transit",
                                onClick = { viewModel.onTravelModeChanged("transit") },
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
                            //google map title
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(), contentAlignment = Alignment.TopCenter
                            ) {
                                Text(
                                    text = stringResource(R.string.choose_your_interests),
                                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
                                )
                            }

                            //Google Map
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(320.dp)
                                    .padding(16.dp)
                                    .background(
                                        color = Color.White,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                            ) {
                                GoogleMap(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(320.dp),
                                    cameraPositionState = cameraPositionState,
                                    onMapLongClick = {
                                        viewModel.viewModelScope.launch {
                                            val places = viewModel.getNearbyPlaces(
                                                Location(
                                                    it.latitude,
                                                    it.longitude
                                                ), apiKey = PlacesapiKey
                                            )
                                            viewModel.addSelectedPlacesOnMap(
                                                Pair(
                                                    it.latitude,
                                                    it.longitude
                                                ), places
                                            )

                                        }
                                        Log.d("SelectedPlacesOnMap", selectedPlacesOnMap.toString())
                                    }

                                ) {

                                    Marker(
                                        state = MarkerState(position = selectedCityLocation.value),
                                        title = destCity.value,
                                        snippet = "Marker in ${destCity.value}"

                                    )
                                    if (selectedPlacesOnMap.isNotEmpty()) {
                                        selectedPlacesOnMap.forEach { place ->
                                            Marker(
                                                state = MarkerState(
                                                    position = LatLng(
                                                        place.key.first,
                                                        place.key.second
                                                    )
                                                ),
                                                snippet = "Marker in ($place.key.first},${place.key.second})",
                                                icon = bitmapDescriptorFromVector(
                                                    context,
                                                    R.drawable.pin2
                                                ),
                                                onClick = {
                                                    if (selectedPlacesOnMap.containsKey(place.key)) {
                                                        viewModel.delSelectedPlacesOnMap(place.key)
                                                    }
                                                    Log.d(
                                                        "selectedplacesOnMap",
                                                        selectedPlacesOnMap.toString()
                                                    )
                                                    return@Marker true
                                                }
                                            )
                                            place.value.forEach { attr ->
                                                Marker(
                                                    state = MarkerState(
                                                        position = LatLng(
                                                            attr.location.lat,
                                                            attr.location.lng
                                                        )
                                                    ),
                                                    title = attr.name,
                                                    snippet = attr.vicinity,
                                                    icon = bitmapDescriptorFromVector(
                                                        context, R.drawable.pin
                                                    ),
                                                    onInfoWindowClick = {
                                                        if (!selectedAttractionList.value.contains(
                                                                attr
                                                            )
                                                        ) {
                                                            viewModel.addSelectedAttraction(attr)
                                                        }
                                                        Log.d(
                                                            "selectedAttractionList: ",
                                                            viewModel.selectedAttractionList.value.toString()
                                                        )
                                                    }

                                                )
                                            }

                                        }
                                    }

                                    if (attractionsList.value.isNotEmpty()) {
                                        attractionsList.value.forEach { place ->
                                            Marker(
                                                state = MarkerState(
                                                    position = LatLng(
                                                        place.location.lat,
                                                        place.location.lng
                                                    ),
                                                ),
                                                title = place.name,
                                                snippet = place.vicinity,
                                                icon = bitmapDescriptorFromVector(
                                                    context, R.drawable.pin
                                                ),
                                                onInfoWindowClick = {
                                                    if (!selectedAttractionList.value.contains(place)) {
                                                        viewModel.addSelectedAttraction(place)
                                                    }
                                                    Log.d(
                                                        "selectedAttractionList: ",
                                                        viewModel.selectedAttractionList.value.toString()
                                                    )

                                                }
//                                    onClick = {
//                                        if(!selectedAttractionList.value.contains(place)){
//                                            viewModel.addSelectedAttraction(place)
//                                        }
//                                        Log.d("selectedAttractionList: ",viewModel.selectedAttractionList.value.toString())
//                                        return@Marker true
//                                    }

                                            )

                                        }
                                    }
                                }
                            }

                            //selected attractions
                            Spacer(modifier = Modifier.height(5.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                            ) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(selectedAttractionList.value, key = { it.name }) {
                                        Tag(title = it.name, onClose = {
                                            viewModel.delSelectedAttraction(it)
                                            Log.d(
                                                "selectedAttractionList: ",
                                                viewModel.selectedAttractionList.value.toString()
                                            )
                                        })
                                    }
                                }
                            }

                            //bottom button
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 85.dp),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.updateAttractionRoutes(selectedAttractionList.value)
                                        viewModel.delAttractionToRoutes(startAttraction.value)
                                        viewModel.delAttractionToRoutes(endAttraction.value)
                                        viewModel.updateSinglePlan(
                                            SinglePlan(
                                                "${viewModel.dateRange.value.lower} - ${viewModel.dateRange.value.upper}",
                                                "${destCountry.value}  ${destState.value}  ${destCity.value}",
                                                selectedAttractionList.value,
                                                startAttraction.value,
                                                endAttraction.value,
                                                viewModel.attractionRoutes.value,
                                                viewModel.sliderValue.value ?: 4,
                                                viewModel.sliderLabel.value ?: "extravagant",
                                                listOf(),
                                                viewModel.travelModeOption.value

                                            )
                                        )
//                            if (selectedAttractionList.value.isNotEmpty()) {
//                                viewModel.updateStartAttraction(selectedAttractionList.value[0])
//                                viewModel.updateEndAttraction(selectedAttractionList.value[selectedAttractionList.value.size - 1])
//                            }
//                                         viewModel.addSinglePlan(singlePlan)
//
//                                         viewModel.updatePlanGroup(Plans(viewModel.planGroup.value.title,viewModel.planGroup.value.description,planList))
                                        navController.navigate("Plan Hotel")
                                    }, modifier = Modifier
                                        .width(130.dp)
                                ) {
                                    Text(text = stringResource(R.string.generate))
                                }
                            }


                        }
                    }
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(100.dp, 100.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Row {
                        Column(
                            modifier = Modifier
                                .width(500.dp)
                        ){
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
                                        text = stringResource(id = R.string.departure),
                                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Column {

                                        //Departure Sight
                                        OutlinedTextField(value = startAttraction.value.name,
                                            onValueChange = {

                                            },
                                            modifier = Modifier
                                                .width(170.dp)
                                                .height(70.dp),
                                            label = { Text(text =stringResource(id = R.string.choose)) },
                                            trailingIcon = {
                                                Icon(
                                                    iconStartAttraction,
                                                    contentDescription = "",
                                                    Modifier.clickable {
                                                        startAttractionExpanded =
                                                            !startAttractionExpanded
                                                    })
                                            })
                                        DropdownMenu(
                                            expanded = startAttractionExpanded,
                                            onDismissRequest = { startAttractionExpanded = false },
                                            modifier = Modifier.width(170.dp),
//                            modifier = Modifier.width(with(LocalDensity.current) { textFiledSize.width.toDp() })
                                        ) {
                                            selectedAttractionList.value.filter { it != endAttraction.value }
                                                .forEach { attraction ->
                                                    DropdownMenuItem(
                                                        text = { Text(attraction.name) },
                                                        onClick = {
                                                            viewModel.updateStartAttraction(attraction)
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
                                        text = stringResource(id = R.string.destination),
                                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Column {

                                        //Destination Sight
                                        OutlinedTextField(value = endAttraction.value.name,
                                            onValueChange = {

                                            },
                                            modifier = Modifier
                                                .width(170.dp)
                                                .height(70.dp),
                                            label = { Text(text = stringResource(id = R.string.choose)) },
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
                                            selectedAttractionList.value.filter { it != startAttraction.value }
                                                .forEach { attraction ->
                                                    DropdownMenuItem(
                                                        text = { Text(attraction.name) },
                                                        onClick = {
                                                            viewModel.updateEndAttraction(attraction)
                                                            endAttractionExpanded = false
                                                        })
                                                }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(100.dp))
                            Box(
                                modifier = Modifier
                                    .width(400.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.select_travel_mode),
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {

                                RadioButton(
                                    selected = viewModel.travelModeOption.value == "bicycling",
                                    onClick = { viewModel.onTravelModeChanged("bicycling") },
                                    modifier = Modifier
                                        .size(20.dp)
                                        .padding(end = 12.dp),
                                )
                                Text(
                                    text = label1,
                                    modifier = Modifier.padding(end = 16.dp)
                                )

                                RadioButton(
                                    selected = viewModel.travelModeOption.value == "walking",
                                    onClick = { viewModel.onTravelModeChanged("walking") },
                                    modifier = Modifier
                                        .size(20.dp)
                                        .padding(end = 12.dp),
                                )
                                Text(
                                    text = label2,
                                    modifier = Modifier.padding(end = 16.dp)
                                )

                                RadioButton(
                                    selected = viewModel.travelModeOption.value == "driving",
                                    onClick = { viewModel.onTravelModeChanged("driving") },
                                    modifier = Modifier
                                        .size(20.dp)
                                        .padding(end = 12.dp),
                                )
                                Text(
                                    text = label3,
                                    modifier = Modifier.padding(end = 16.dp)
                                )

                                RadioButton(
                                    selected = viewModel.travelModeOption.value == "transit",
                                    onClick = { viewModel.onTravelModeChanged("transit") },
                                    modifier = Modifier
                                        .size(20.dp)
                                        .padding(end = 12.dp),
                                )
                                Text(
                                    text = label4,
                                    modifier = Modifier.padding(end = 16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(30.dp))
                        Column(
                            modifier = Modifier
                                .width(500.dp)
                        ) {
                            //google map title
                            Box(
                                modifier = Modifier
                                    .width(500.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.choose_your_interests),
                                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
                                )
                            }

                            //Google Map
                            Box(
                                modifier = Modifier
                                    .width(500.dp)
                                    .height(320.dp)
                                    .padding(16.dp)
                                    .background(
                                        color = Color.White,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                            ) {
                                GoogleMap(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(320.dp),
                                    cameraPositionState = cameraPositionState,
                                    onMapLongClick = {
                                        viewModel.viewModelScope.launch {
                                            val places = viewModel.getNearbyPlaces(
                                                Location(
                                                    it.latitude,
                                                    it.longitude
                                                ), apiKey = PlacesapiKey
                                            )
                                            viewModel.addSelectedPlacesOnMap(
                                                Pair(
                                                    it.latitude,
                                                    it.longitude
                                                ), places
                                            )

                                        }
                                        Log.d("SelectedPlacesOnMap", selectedPlacesOnMap.toString())
                                    }

                                ) {

                                    Marker(
                                        state = MarkerState(position = selectedCityLocation.value),
                                        title = destCity.value,
                                        snippet = "Marker in ${destCity.value}"

                                    )
                                    if (selectedPlacesOnMap.isNotEmpty()) {
                                        selectedPlacesOnMap.forEach { place ->
                                            Marker(
                                                state = MarkerState(
                                                    position = LatLng(
                                                        place.key.first,
                                                        place.key.second
                                                    )
                                                ),
                                                snippet = "Marker in ($place.key.first},${place.key.second})",
                                                icon = bitmapDescriptorFromVector(
                                                    context,
                                                    R.drawable.pin2
                                                ),
                                                onClick = {
                                                    if (selectedPlacesOnMap.containsKey(place.key)) {
                                                        viewModel.delSelectedPlacesOnMap(place.key)
                                                    }
                                                    Log.d(
                                                        "selectedplacesOnMap",
                                                        selectedPlacesOnMap.toString()
                                                    )
                                                    return@Marker true
                                                }
                                            )
                                            place.value.forEach { attr ->
                                                Marker(
                                                    state = MarkerState(
                                                        position = LatLng(
                                                            attr.location.lat,
                                                            attr.location.lng
                                                        )
                                                    ),
                                                    title = attr.name,
                                                    snippet = attr.vicinity,
                                                    icon = bitmapDescriptorFromVector(
                                                        context, R.drawable.pin
                                                    ),
                                                    onInfoWindowClick = {
                                                        if (!selectedAttractionList.value.contains(
                                                                attr
                                                            )
                                                        ) {
                                                            viewModel.addSelectedAttraction(attr)
                                                        }
                                                        Log.d(
                                                            "selectedAttractionList: ",
                                                            viewModel.selectedAttractionList.value.toString()
                                                        )
                                                    }

                                                )
                                            }

                                        }
                                    }

                                    if (attractionsList.value.isNotEmpty()) {
                                        attractionsList.value.forEach { place ->
                                            Marker(
                                                state = MarkerState(
                                                    position = LatLng(
                                                        place.location.lat,
                                                        place.location.lng
                                                    ),
                                                ),
                                                title = place.name,
                                                snippet = place.vicinity,
                                                icon = bitmapDescriptorFromVector(
                                                    context, R.drawable.pin
                                                ),
                                                onInfoWindowClick = {
                                                    if (!selectedAttractionList.value.contains(place)) {
                                                        viewModel.addSelectedAttraction(place)
                                                    }
                                                    Log.d(
                                                        "selectedAttractionList: ",
                                                        viewModel.selectedAttractionList.value.toString()
                                                    )

                                                }
//                                    onClick = {
//                                        if(!selectedAttractionList.value.contains(place)){
//                                            viewModel.addSelectedAttraction(place)
//                                        }
//                                        Log.d("selectedAttractionList: ",viewModel.selectedAttractionList.value.toString())
//                                        return@Marker true
//                                    }

                                            )

                                        }
                                    }
                                }
                            }

                            //selected attractions
                            Spacer(modifier = Modifier.height(5.dp))
                            Box(
                                modifier = Modifier
                                    .width(500.dp)
                                    .height(100.dp)
                            ) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(selectedAttractionList.value, key = { it.name }) {
                                        Tag(title = it.name, onClose = {
                                            viewModel.delSelectedAttraction(it)
                                            Log.d(
                                                "selectedAttractionList: ",
                                                viewModel.selectedAttractionList.value.toString()
                                            )
                                        })
                                    }
                                }
                            }

                            //bottom button
                            Box(
                                modifier = Modifier
                                    .width(500.dp)
                                    .padding(bottom = 85.dp),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Button(
                                    onClick = {

                                        viewModel.updateSinglePlan(
                                            SinglePlan(
                                                "${viewModel.dateRange.value.lower} - ${viewModel.dateRange.value.upper}",
                                                "${destCountry.value}  ${destState.value}  ${destCity.value}",
                                                selectedAttractionList.value,
                                                startAttraction.value,
                                                endAttraction.value,
                                                viewModel.attractionRoutes.value,
                                                viewModel.sliderValue.value ?: 4,
                                                viewModel.sliderLabel.value ?: "extravagant",
                                                listOf(),
                                                viewModel.travelModeOption.value

                                            )
                                        )
//                            if (selectedAttractionList.value.isNotEmpty()) {
//                                viewModel.updateStartAttraction(selectedAttractionList.value[0])
//                                viewModel.updateEndAttraction(selectedAttractionList.value[selectedAttractionList.value.size - 1])
//                            }
//                                         viewModel.addSinglePlan(singlePlan)
//
//                                         viewModel.updatePlanGroup(Plans(viewModel.planGroup.value.title,viewModel.planGroup.value.description,planList))
                                        navController.navigate("Plan Hotel")
                                    }, modifier = Modifier
                                        .width(300.dp)
                                ) {
                                    Text(text = stringResource(id = R.string.choose))
                                }
                            }


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
    //PlanChooseLocScreen(JourneyGeniusViewModel(Firebase.firestore, Firebase.auth, Firebase.database.reference), windowSize, rememberNavController())
//    dropDownMenu()
//    Tag(title = "Shenzhen", onClose = {})
}