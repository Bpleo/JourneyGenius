package com.example.journeygenius
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.material.*
import android.location.Geocoder
import androidx.compose.ui.platform.LocalContext
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanChooseLocScreen(viewModel: PlanViewModel) {
    val countries = listOf("China", "Japan", "Korea", "US", "UK")
    val usStates = listOf(
        "AL",
        "AK",
        "AZ",
        "AR",
        "CA",
        "CO",
        "CT",
        "DE",
        "FL",
        "GA",
        "HI",
        "ID",
        "IL",
        "IN",
        "IA",
        "KS",
        "KY",
        "LA",
        "ME",
        "MD",
        "MA",
        "MI",
        "MN"
    )
    val chnStates = listOf(
        "Guangdong",
        "Hainan",
        "Beijing",
        "Jiangsu",
        "Jiangxi",
        "Guangxi",
        "Sichuan",
        "Yunan",
        "Fujian"
    )
    val jpStates = listOf("Hokkaido", "Aomori", "Iwate", "Miyagi", "Akita", "Yamagata", "Fukushima")
    val krStates = listOf(
        "Seoul", "Busan",
        "Daegu",
        "Incheon",
        "Gwangju",
        "Daejeon",
        "Ulsan",
        "Sejong",
    )
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
    val stateToCityMap = mapOf(
        "England" to engCities,
        "Guangdong" to guangdongCities,
        "Hokkaido" to hokCities,
        "Boston" to bosCities,
    )


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
    var departCountry by remember {
        mutableStateOf(viewModel.departCountry)
    }
    var departSate by remember {
        mutableStateOf(viewModel.departState)
    }
    var departCity by remember {
        mutableStateOf(viewModel.departCity)
    }

    var destCountry by remember {
        mutableStateOf(viewModel.destCountry)
    }
    var destState by remember {
        mutableStateOf(viewModel.destState)
    }
    var destCity by remember {
        mutableStateOf(viewModel.destCity)
    }
    val selectedCityLocation = remember { viewModel.selectedCityLocation }
    var textFiledSize by remember {
        mutableStateOf(Size.Zero)
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
    fun getCityLocation(cityName: String){
        val geocoder = Geocoder(context, Locale.getDefault())
        val results = geocoder.getFromLocationName(cityName, 1)
        if (results != null) {
                val latitude = results[0].latitude
                val longitude = results[0].longitude
                viewModel.updateSelectedCityLocation(Pair(latitude, longitude))
            } else {
            viewModel.updateSelectedCityLocation(null)
            }
        }



    val singapore=LatLng(1.35, 103.87)
    val cameraPositionState= rememberCameraPositionState{
        position= CameraPosition.fromLatLngZoom(singapore,10f)
    }

    JourneyGeniusTheme {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter
        ) {
            Column() {
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
                        Column() {

                            //Departure Country
                            OutlinedTextField(value = departCountry.value,
                                onValueChange = { viewModel.updateDepartCountry(it) },

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
                                modifier = Modifier.width(170.dp),
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
                                modifier = Modifier.width(170.dp),
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
                                modifier = Modifier.width(170.dp),
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
                        Column() {
                            Box() {


                                //Destination Country
                                OutlinedTextField(value = destCountry.value,
                                    onValueChange = { viewModel.updateDestCountry(it) },
                                    modifier = Modifier.width(200.dp),
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
                                    modifier = Modifier.width(170.dp),
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
                                modifier = Modifier.width(200.dp),
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
                                modifier = Modifier.width(170.dp),
//                            modifier = Modifier.width(with(LocalDensity.current) { textFiledSize.width.toDp() })
                            ) {
                                countryToStateMap[destCountry.value]?.forEach { country ->
                                    DropdownMenuItem(text = { Text(country) }, onClick = {
                                        viewModel.updateDestState(country)
                                        destStateExpanded = false
                                    })
                                }
                            }
                            //Destination City
                            OutlinedTextField(value = destCity.value,
                                onValueChange = { viewModel.updateDestCity(it) },
                                modifier = Modifier.width(200.dp),

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
                                modifier = Modifier.width(170.dp),
//                            modifier = Modifier.width(with(LocalDensity.current) { textFiledSize.width.toDp() })
                            ) {
                                stateToCityMap[destState.value]?.forEach { country ->
                                    DropdownMenuItem(text = { Text(country) }, onClick = {
                                        viewModel.updateDestCity(country)
                                        destCityExpanded = false
                                    })
                                }
                            }


                        }


                    }

                }
                Spacer(modifier = Modifier.height(40.dp))
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
                    Spacer(modifier = Modifier.height(5.dp))
                    GoogleMap(modifier = Modifier.fillMaxWidth().height(300.dp),
                        cameraPositionState = cameraPositionState){
                        Marker(
                            state = MarkerState(position = singapore),
                            title = "Boston",
                            snippet = "Marker in Boston"
                        )

                    }
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 20.dp),
                        contentAlignment = Alignment.BottomCenter
                    ){
                        Button(onClick = { /*TODO*/ }, modifier = Modifier
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
    PlanChooseLocScreen(PlanViewModel())
    //dropDownMenu()
}