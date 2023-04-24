package com.example.journeygenius.plan

import android.annotation.SuppressLint
import android.util.Log
import android.util.Range
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.nio.charset.Charset
import java.nio.file.Path
import java.time.LocalDate
const val PlacesapiKey = "AIzaSyCNcLRKVJXQ8TL3WRiSujLRVD_qTLMxj8E"
const val geoCodingApiKey="AIzaSyBTThEhexAEYjjbUteaUa6P0a-W57fYrx4"
data class Place(
    val name: String,
    val vicinity: String,
    val location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)

data class Response(
    val results: List<Result>
)

data class Result(
    val name: String,
    val vicinity: String,
    val geometry: Geometry
)

data class Geometry(
    val location: Location
)


data class GeocodeResponse(
    val results: List<GeocodeResult>
)

data class GeocodeResult(
    val geometry: Geometry
)


@SuppressLint("SuspiciousIndentation")
class PlanViewModel : ViewModel() {
    private var _dateRange = mutableStateOf(Range(LocalDate.now().minusDays(3), LocalDate.now()))
    val dateRange: MutableState<Range<LocalDate>> = _dateRange

    fun updateRange(start: LocalDate, end: LocalDate){
        _dateRange.value = Range(start, end)
    }

    private var _budget = mutableStateOf(TextFieldValue())
    val budget: MutableState<TextFieldValue> = _budget

    fun updateBudget(value: TextFieldValue){
        _budget.value = value
    }

    private var _departCountry= mutableStateOf("US")
    val departCountry:MutableState<String> = _departCountry
    fun updateDepartCountry(value: String){
        _departCountry.value=value
    }

    private var _departState= mutableStateOf("MA")
    val departState:MutableState<String> =_departState
    fun updateDepartState(value:String){
        _departState.value=value
    }

    private var _departCity= mutableStateOf("Boston")
    val departCity:MutableState<String> =_departCity
    fun updateDepartCity(value:String){
        _departCity.value=value
    }

    private var _destCountry= mutableStateOf("")
    val destCountry:MutableState<String> = _destCountry
    fun updateDestCountry(value: String){
        _destCountry.value=value
    }

    private var _destState= mutableStateOf("")
    val destState:MutableState<String> =_destState
    fun updateDestState(value:String){
        _destState.value=value
    }

    private var _destCity= mutableStateOf("")
    val destCity:MutableState<String> =_destCity
    fun updateDestCity(value:String){
        _destCity.value=value
//        viewModelScope.launch {
//            val location = getLatLng(value, geoCodingApiKey)
//            if (location!=null){
//                updateSelectedCityLatLng(listOf(location.lat,location.lng))
//                updateSelectedCityLocation(LatLng(location.lat,location.lng))
//                //searchNearbyPlaces(location, apiKey = PlacesapiKey)
//            }
//        }
    }

    private var _selectedCityLocation= mutableStateOf(LatLng(42.36, -71.05))
    val selectedCityLocation: MutableState<LatLng> = _selectedCityLocation
    fun updateSelectedCityLocation(value: LatLng){
            _selectedCityLocation.value=value

    }

    //used to store the latitude and the longitude of the selected city
    private var _selectedCityLatLng= mutableStateOf(listOf(42.36, -71.05))
    val selectedCityLatLng:MutableState<List<Double>> = _selectedCityLatLng
    fun updateSelectedCityLatLng(value:List<Double>){
        _selectedCityLatLng.value=value
    }


    private var _attractionsList = mutableStateOf<List<Place>>(listOf())
    val attractionsList:MutableState<List<Place>> = _attractionsList
    fun updateAttractionsList(value:List<Place>){
        _attractionsList.value=value
    }
    init {
        viewModelScope.launch {
            val location =Location(42.36, -71.05)
            searchNearbyPlaces(location, apiKey = PlacesapiKey)
        }
    }

    private var _selectedAttractionList = mutableStateOf<List<Place>>(listOf())
    val selectedAttractionList:MutableState<List<Place>> = _selectedAttractionList
    fun updateSelectedAttractionList(value:List<Place>){
        _selectedAttractionList.value=value
    }
    fun addSelectedAttraction(value:Place){
        val updatedSelectedAttractionsList=_selectedAttractionList.value.toMutableList()
        updatedSelectedAttractionsList.add(value)
        updateSelectedAttractionList(updatedSelectedAttractionsList)
    }

    suspend fun getLatLng(city: String, apiKey: String): Location? = withContext(Dispatchers.IO) {
    val url = URL("https://maps.googleapis.com/maps/api/geocode/json?address=$city&key=$apiKey")
    val json = url.readText(Charset.defaultCharset())
    val gson = Gson()
    val response = gson.fromJson(json, GeocodeResponse::class.java)
    response.results.firstOrNull()?.geometry?.location
}
    suspend fun searchNearbyPlaces(location: Location, radius: Int = 100000, apiKey: String) {
    withContext(Dispatchers.IO) {
        val url = URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${location.lat},${location.lng}&radius=$radius&key=$apiKey&type=tourist_attraction&language=en")
        val json = url.readText(Charset.defaultCharset())
        val gson = Gson()
        val response = gson.fromJson(json, Response::class.java)
        updateAttractionsList(response.results.map { result ->
            Place(
                name = result.name,
                vicinity = result.vicinity,
                location = Location(result.geometry.location.lat, result.geometry.location.lng)
            )
        })
        Log.d("attractionlist",attractionsList.toString())
    }
}

//    init {
//        viewModelScope.launch {
//            searchNearbyPlaces(Location(selectedCityLatLng.value[0], selectedCityLatLng.value[1]), apiKey = PlacesapiKey)
//        }
//    }

}