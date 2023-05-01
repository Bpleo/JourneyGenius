package com.example.journeygenius.plan

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import android.util.Range
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.journeygenius.WindowSize
import com.example.journeygenius.WindowType
import com.example.journeygenius.JourneyGeniusViewModel
import com.example.journeygenius.rememberWindowSize
import com.example.journeygenius.ui.theme.JourneyGeniusTheme
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap

@Composable
fun PlanScreenGraph(
    viewModel: JourneyGeniusViewModel,
    windowSize: WindowSize,
    auth: FirebaseAuth,
    firestore: FirebaseFirestore,
    realtime: DatabaseReference
){
    val planNavController= rememberNavController()
    NavHost(navController = planNavController,
        startDestination = "Plan Menu"){
        composable("Plan Menu"){
            PlanScreen(viewModel,windowSize,planNavController)
        }
        composable("Plan Map"){
            PlanChooseLocScreen(viewModel = viewModel,planNavController)
        }
        composable("Plan Hotel"){
            PlanHotelSelectionScreen(viewModel = viewModel,planNavController)
        }
        composable("Plan List"){
            PlanList(
                navController = planNavController,
                planViewModel = viewModel,
            )
        }
        composable("Plan Detail"){
            PlanDetail(planNavController,viewModel)
        }
    }
}

@Composable
fun TravelDateComponent(
    selectedDateRange: MutableState<MutableState<Range<LocalDate>>>,
    calenderState: UseCaseState,
    windowSize: WindowSize
) {
    val sdf = DateTimeFormatter.ofPattern("MM-dd-yyyy")
    Column {
        when(windowSize.height){
            WindowType.Medium -> {
                Text(
                    text = "Travel Date: ",
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize
                )
            }
            else -> {
                Text(
                    text = "Travel Date: ",
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row {
            Column {
                when(windowSize.height){
                    WindowType.Medium -> {
                        Text(
                            text = "Start Date: ",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "End Date: ",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                    }
                    else -> {
                        Text(
                            text = "Start Date: ",
                            fontSize = MaterialTheme.typography.titleSmall.fontSize
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "End Date: ",
                            fontSize = MaterialTheme.typography.titleSmall.fontSize
                        )
                    }
                }

            }
            Column {
                Row(
                    modifier = Modifier
                        .background(Color.LightGray)
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface))
                )
                {
                    when(windowSize.height) {
                        WindowType.Medium -> {
                            ClickableText(
                                text = AnnotatedString(
                                    selectedDateRange.component1().component1().lower.format(
                                        sdf
                                    ), spanStyle = SpanStyle(
                                        fontSize = 28.sp,
                                        color = Color.DarkGray,
                                        background = Color.White,
                                        textDecoration = TextDecoration.None
                                    )
                                ),
                                onClick = {
                                    calenderState.show()
                                }
                            )
                        }
                        else -> {
                            ClickableText(
                                text = AnnotatedString(
                                    selectedDateRange.component1().component1().lower.format(
                                        sdf
                                    ), spanStyle = SpanStyle(fontSize = 17.sp)
                                ),
                                onClick = {
                                    calenderState.show()
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(13.dp))
                Row(
                    modifier = Modifier
                        .background(Color.LightGray)
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface))
                ) {
                    when(windowSize.height) {
                        WindowType.Medium -> {
                            ClickableText(
                                text = AnnotatedString(
                                    selectedDateRange.component1().component1().upper.format(
                                        sdf
                                    ), spanStyle = SpanStyle(
                                        fontSize = 28.sp,
                                        color = Color.DarkGray,
                                        background = Color.White,
                                        textDecoration = TextDecoration.None
                                    )
                                ),
                                onClick = {
                                    calenderState.show()
                                }
                            )
                        }
                        else -> {
                            ClickableText(
                                text = AnnotatedString(
                                    selectedDateRange.component1().component1().upper.format(
                                        sdf
                                    ), spanStyle = SpanStyle(fontSize = 17.sp)
                                ),
                                onClick = {
                                    calenderState.show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun BudgetComponent(viewModel: JourneyGeniusViewModel){
    Column {
        Text(
            text = "What is your budget: ",
            fontSize = MaterialTheme.typography.headlineLarge.fontSize
        )
        Spacer(modifier = Modifier.height(15.dp))
        val sliderValue: Int by viewModel.sliderValue.observeAsState(0)
        val sliderLabel: String by viewModel.sliderLabel.observeAsState("cheap")

        Row(verticalAlignment = Alignment.CenterVertically) {
            Slider(
                value = sliderValue.toFloat(),
                onValueChange = { viewModel.onSliderValueChanged(it.toInt()) },
                valueRange = 0f..4f,
                steps = 3,
                modifier = Modifier
                    .height(50.dp)
                    .width(220.dp)
                    .padding(end = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .background(Color.LightGray, RoundedCornerShape(16.dp))
            ) {
                Text(
                    text = sliderLabel,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseDropdownMenu(viewModel: JourneyGeniusViewModel) {
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
    val departCountry by remember {
        mutableStateOf(viewModel.departCountry)
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
    fun findLocOnMap(maxResult: Int, destCityName:String, context: Context){
        val geocoder = Geocoder(context, Locale.getDefault())
        val geocodeListener = @RequiresApi(33) object : Geocoder.GeocodeListener {
            override fun onGeocode(results: List<Address>){
                // do something with the addresses list
                val latitude = results[0].latitude
                val longitude = results[0].longitude
                viewModel.updateSelectedCityLocation(LatLng(latitude,longitude))
                viewModel.updateSelectedCityLatLng(listOf(latitude,longitude))
                Log.d("lat,long", viewModel.selectedCityLatLng.value.toString())
            }
        }

        if (Build.VERSION.SDK_INT >= 33) {
            // declare here the geocodeListener, as it requires Android API 33
            geocoder.getFromLocationName(destCityName,maxResult,geocodeListener)

        } else {
            // For Android SDK < 33, the addresses list will be still obtained from the getFromLocation() method
            val addresses = geocoder.getFromLocationName(destCityName,maxResult)
            if(addresses!=null){
                val latitude =  addresses[0].latitude
                val longitude =  addresses[0].longitude
                viewModel.updateSelectedCityLocation(LatLng(latitude,longitude))
                viewModel.updateSelectedCityLatLng(listOf(latitude,longitude))
                Log.d("lat,long", "$latitude $longitude")
                Pair(latitude, longitude)
            }
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 15.dp)
    ) {
        //Departure
        Column(
            modifier = Modifier.width(150.dp)
        ) {
            Text(
                text = "Departure",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Column {

                //Departure Country
                OutlinedTextField(value = departCountry.value,
                    onValueChange = { viewModel.updateDepartCountry(it) },
                    modifier = Modifier.width(150.dp).height(70.dp),
                    label = { Text(text = "Country") },
                    trailingIcon = {
                        Icon(
                            iconDepartCountry,
                            contentDescription = "",
                            Modifier.clickable {
                                departCountryExpanded = !departCountryExpanded
                            })
                    })
                DropdownMenu(
                    expanded = departCountryExpanded,
                    onDismissRequest = { departCountryExpanded = false },
                    modifier = Modifier.width(150.dp),
//                            modifier = Modifier.width(with(LocalDensity.current) { textFiledSize.width.toDp() })
                ) {
                    countries.forEach { country ->
                        DropdownMenuItem(text = { Text(country) }, onClick = {
                            viewModel.updateDepartCountry(country)
                            viewModel.updateDepartState("")
                            viewModel.updateDepartCity("")
                            departCountryExpanded = false
                        })

                    }
                }

                //Departure State
                OutlinedTextField(value = departSate.value,
                    onValueChange = { viewModel.updateDepartState(it) },
                    modifier = Modifier.width(150.dp).height(70.dp),
                    label = { Text(text = "State") },
                    trailingIcon = {
                        Icon(
                            iconDepartState,
                            contentDescription = "",
                            Modifier.clickable {
                                departStateExpanded = !departStateExpanded
                            })
                    })
                DropdownMenu(
                    expanded = departStateExpanded,
                    onDismissRequest = { departStateExpanded = false },
                    modifier = Modifier.width(150.dp),
//                            modifier = Modifier.width(with(LocalDensity.current) { textFiledSize.width.toDp() })
                ) {
                    countryToStateMap[departCountry.value]?.forEach { country ->
                        DropdownMenuItem(text = { Text(country) }, onClick = {
                            viewModel.updateDepartState(country)
                            departStateExpanded = false
                        })
                    }
                }
                //Departure City
                OutlinedTextField(value = departCity.value,
                    onValueChange = { viewModel.updateDepartCity(it) },
                    modifier = Modifier.width(150.dp).height(70.dp),
                    label = { Text(text = "City") },
                    trailingIcon = {
                        Icon(
                            iconDepartCity,
                            contentDescription = "",
                            Modifier.clickable {
                                departCityExpanded = !departCityExpanded
                            })
                    })
                DropdownMenu(
                    expanded = departCityExpanded,
                    onDismissRequest = { departCityExpanded = false },
                    modifier = Modifier.width(150.dp),
//                            modifier = Modifier.width(with(LocalDensity.current) { textFiledSize.width.toDp() })
                ) {
                    stateToCityMap[departSate.value]?.forEach { country ->
                        DropdownMenuItem(text = { Text(country) }, onClick = {
                            viewModel.updateDepartCity(country)
                            departCityExpanded = false
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
                Box {


                    //Destination Country
                    OutlinedTextField(value = destCountry.value,
                        onValueChange = { viewModel.updateDestCountry(it) },
                        modifier = Modifier.width(150.dp).height(70.dp),
                        label = { Text(text = "Country") },
                        trailingIcon = {
                            Icon(
                                iconDestinCountry,
                                contentDescription = "",
                                Modifier.clickable {
                                    destCountryExpanded = !destCountryExpanded
                                })
                        })
                    DropdownMenu(
                        expanded = destCountryExpanded,
                        onDismissRequest = { destCountryExpanded = false },
                        modifier = Modifier.width(150.dp),
//                            modifier = Modifier.width(with(LocalDensity.current) { textFiledSize.width.toDp() })
                    ) {
                        countries.forEach { country ->
                            DropdownMenuItem(text = { Text(country) }, onClick = {
                                viewModel.updateDestCountry(country)
                                viewModel.updateDestState("")
                                viewModel.updateDestCity("")
                                destCountryExpanded = false
                            })

                        }
                    }
                }

                //Destination State
                OutlinedTextField(value = destState.value,
                    onValueChange = { viewModel.updateDestState(it) },
                    modifier = Modifier.width(150.dp).height(70.dp),
                    label = { Text(text = "State") },
                    trailingIcon = {
                        Icon(
                            iconDestinState,
                            contentDescription = "",
                            Modifier.clickable {
                                destStateExpanded = !destStateExpanded
                            })
                    })
                DropdownMenu(
                    expanded = destStateExpanded,
                    onDismissRequest = { destStateExpanded = false },
                    modifier = Modifier.width(150.dp),
//                            modifier = Modifier.width(with(LocalDensity.current) { textFiledSize.width.toDp() })
                ) {
                    countryToStateMap[destCountry.value]?.forEach { country ->
                        DropdownMenuItem(text = { Text(country) }, onClick = {
                            viewModel.updateDestState(country)
                            viewModel.updateDestCity("")
                            destStateExpanded = false
                        })
                    }
                }
                //Destination City
                OutlinedTextField(value = destCity.value,
                    onValueChange = { viewModel.updateDestCity(it) },
                    modifier = Modifier.width(150.dp).height(70.dp),

                    label = { Text(text = "City") },
                    trailingIcon = {
                        Icon(
                            iconDestinCity,
                            contentDescription = "",
                            Modifier.clickable {
                                destCityExpanded = !destCityExpanded
                            })
                    })
                DropdownMenu(
                    expanded = destCityExpanded,
                    onDismissRequest = { destCityExpanded = false },
                    modifier = Modifier.width(150.dp),
//                            modifier = Modifier.width(with(LocalDensity.current) { textFiledSize.width.toDp() })
                ) {
                    stateToCityMap[destState.value]?.forEach { country ->
                        DropdownMenuItem(text = { Text(country) }, onClick = {
                            viewModel.updateDestCity(country)
                            destCityExpanded = false
//                                        Log.d("marker",selectedCityLocation.value.toString())
                            findLocOnMap(1,country, context)
                            viewModel.updateSelectedAttractionList(listOf())
                            viewModel.updateAttractionsList(listOf())
                            viewModel.updateSelectedPlacesOnMap(HashMap())
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun DestinationButton(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            onClick = {
                navController.navigate("Plan Map")
            }, modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "CHOOSE YOUR ROUTE")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    viewModel: JourneyGeniusViewModel,
    windowSize: WindowSize,
    navController: NavController
) {

    val selectedDateRange = remember {
        val value = viewModel.dateRange
        mutableStateOf(value)
    }
    val budget by remember { mutableStateOf(viewModel.budget) }
    val travelType by remember {
        mutableStateOf(viewModel.travelType)
    }
    val calenderState = rememberUseCaseState()
    CalendarDialog(
        state = calenderState,
        config = CalendarConfig(
            yearSelection = true,
            monthSelection = true,
            style = CalendarStyle.MONTH,
        ),
        selection = CalendarSelection.Period(
            selectedRange = selectedDateRange.component1().value
        ) { startDate, endDate ->
            viewModel.updateRange(startDate, endDate)
        }
    )
    JourneyGeniusTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when(windowSize.height){
                WindowType.Medium -> {
                    Column(
                        modifier = Modifier.padding(32.dp,64.dp)
                    ) {
                        TravelDateComponent(selectedDateRange, calenderState, windowSize)
                        Spacer(modifier = Modifier.height(20.dp))
                        BudgetComponent(viewModel = viewModel)
                        Spacer(modifier = Modifier.height(20.dp))
                        ChooseDropdownMenu(viewModel = viewModel)
                        Spacer(modifier = Modifier.height(20.dp))
                        DestinationButton(navController)
                    }
                }
                else -> {
                    Row(
                        modifier = Modifier.padding(64.dp, 32.dp)
                    ){
                        Column {
                            TravelDateComponent(selectedDateRange, calenderState, windowSize)
                        }
                        Spacer(modifier = Modifier.width(60.dp))
                        Column{
                            BudgetComponent(viewModel = viewModel)
                            Spacer(modifier = Modifier.height(60.dp))
                            DestinationButton(navController)
                        }
                    }
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlanScreenPreview() {
    PlanScreen(JourneyGeniusViewModel(Firebase.firestore, Firebase.auth),rememberWindowSize(),
        rememberNavController())
}

@Preview@Preview(showBackground = true, device = Devices.AUTOMOTIVE_1024p, heightDp = 320)
@Composable
fun PlanScreenLandscapePreview() {
    val viewModel = JourneyGeniusViewModel(Firebase.firestore, Firebase.auth)
    val selectedDateRange = remember {
        val value = viewModel.dateRange
        mutableStateOf(value)
    }
    val budget by remember { mutableStateOf(viewModel.budget) }
    val calenderState = rememberUseCaseState()
    JourneyGeniusTheme {
        Box {
            Row(
                modifier = Modifier.padding(64.dp, 32.dp)
            ) {
                Column {
                    TravelDateComponent(selectedDateRange, calenderState, rememberWindowSize())
                }
                Spacer(modifier = Modifier.width(60.dp))
                Column{
                    BudgetComponent(viewModel = viewModel)
                    Spacer(modifier = Modifier.height(60.dp))
                    ChooseDropdownMenu(viewModel)
                    Spacer(modifier = Modifier.height(60.dp))
                    DestinationButton(rememberNavController())
                }
            }
        }
    }
}