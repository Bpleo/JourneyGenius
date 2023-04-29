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
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.nio.charset.Charset
import java.nio.file.Path
import java.time.LocalDate
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.sin

const val PlacesapiKey = "AIzaSyCNcLRKVJXQ8TL3WRiSujLRVD_qTLMxj8E"
data class Place(
    val name: String,
    val vicinity: String,
    val location: Location
)

// TODO: ADD MORE HOTEL DETAILS
data class Hotel(
    val place: Place,
    val priceLevel:Int,
    val rating:Double,

)
data class Location(
    val lat: Double,
    val lng: Double
)

data class Response(
    val results: List<Result>
)
data class HotelResponse(
    val results: List<HotelResult>
)
data class Result(
    val name: String,
    val vicinity: String,
    val geometry: Geometry
)

data class HotelResult(
    val name: String,
    val vicinity: String,
    val geometry: Geometry,
    val place_id:String,
    val price_level:Int,
    val rating: Double
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

data class singlePlan(
    val date:String,
    val destination:String,
    val attractions:List<Place>,
    val budget:String,
    var hotel: List<Hotel>,
    val trans_mode: String

)

data class Plans(
    val title:String,
    val description:String,
    val plans: List<singlePlan>
)

@SuppressLint("SuspiciousIndentation")
class PlanViewModel : ViewModel() {
    private var _dateRange = mutableStateOf(Range(LocalDate.now().minusDays(3), LocalDate.now()))
    val dateRange: MutableState<Range<LocalDate>> = _dateRange

    fun updateRange(start: LocalDate, end: LocalDate) {
        _dateRange.value = Range(start, end)
    }

    private var _budget = mutableStateOf(TextFieldValue())
    val budget: MutableState<TextFieldValue> = _budget

    fun updateBudget(value: TextFieldValue) {
        _budget.value = value
    }

    private var _departCountry = mutableStateOf("US")
    val departCountry: MutableState<String> = _departCountry
    fun updateDepartCountry(value: String) {
        _departCountry.value = value
    }

    private var _departState = mutableStateOf("MA")
    val departState: MutableState<String> = _departState
    fun updateDepartState(value: String) {
        _departState.value = value
    }

    private var _departCity = mutableStateOf("Boston")
    val departCity: MutableState<String> = _departCity
    fun updateDepartCity(value: String) {
        _departCity.value = value
    }

    private var _destCountry = mutableStateOf("")
    val destCountry: MutableState<String> = _destCountry
    fun updateDestCountry(value: String) {
        _destCountry.value = value
    }

    private var _destState = mutableStateOf("")
    val destState: MutableState<String> = _destState
    fun updateDestState(value: String) {
        _destState.value = value
    }

    private var _destCity = mutableStateOf("")
    val destCity: MutableState<String> = _destCity
    fun updateDestCity(value: String) {
        _destCity.value = value
    }

    private var _selectedCityLocation = mutableStateOf(LatLng(42.36, -71.05))
    val selectedCityLocation: MutableState<LatLng> = _selectedCityLocation
    fun updateSelectedCityLocation(value: LatLng) {
        _selectedCityLocation.value = value

    }

    //used to store the latitude and the longitude of the selected city
    private var _selectedCityLatLng = mutableStateOf(listOf(42.36, -71.05))
    val selectedCityLatLng: MutableState<List<Double>> = _selectedCityLatLng
    fun updateSelectedCityLatLng(value: List<Double>) {
        _selectedCityLatLng.value = value
    }

//    private var _attractionsList = mutableStateOf<List<Place>>(listOf())
//    val attractionsList: MutableState<List<Place>> = _attractionsList
//    fun updateAttractionsList(value: List<Place>) {
//        _attractionsList.value = value
//    }
    @SuppressLint("MutableCollectionMutableState")
    private var _attractionsList = mutableStateOf(CopyOnWriteArrayList<Place>())
    val attractionsList:
            MutableState<CopyOnWriteArrayList<Place>> = _attractionsList

    fun updateAttractionsList(value: List<Place>) {
        _attractionsList.value.apply {
            clear()
            addAll(value)
        }
    }
    fun addAttractionsList(value: List<Place>) {
        _attractionsList.value.addAll(value)
    }

    init {
        viewModelScope.launch {
            val location = Location(42.36, -71.05)
            searchNearbyPlaces(location, apiKey = PlacesapiKey)
        }
    }

    private var _selectedAttractionList = mutableStateOf<List<Place>>(listOf())
    val selectedAttractionList: MutableState<List<Place>> = _selectedAttractionList
    fun updateSelectedAttractionList(value: List<Place>) {
        _selectedAttractionList.value = value
    }

    fun addSelectedAttraction(value: Place) {
        val updatedSelectedAttractionsList = _selectedAttractionList.value.toMutableList()
        updatedSelectedAttractionsList.add(value)
        updateSelectedAttractionList(updatedSelectedAttractionsList)
    }

    fun delSelectedAttraction(value: Place) {
        val updatedSelectedAttractionsList = _selectedAttractionList.value.toMutableList()
        if (updatedSelectedAttractionsList.contains(value)) {
            updatedSelectedAttractionsList.remove(value)
            updateSelectedAttractionList(updatedSelectedAttractionsList)
        }

    }

    private var _selectedPlacesOnMap = mutableStateOf<HashMap<Pair<Double, Double>, List<Place>>>(
        HashMap()
    )
    val selectedPlacesOnMap: MutableState<HashMap<Pair<Double, Double>, List<Place>>> =
        _selectedPlacesOnMap

    fun updateSelectedPlacesOnMap(value: HashMap<Pair<Double, Double>, List<Place>>) {
        _selectedPlacesOnMap.value = value;
    }

    fun addSelectedPlacesOnMap(latlng: Pair<Double, Double>, places: List<Place>) {
        val updatedSelectedPlacesOnMap = _selectedPlacesOnMap.value.toMutableMap()
        if (!updatedSelectedPlacesOnMap.containsKey(latlng)) {
            updatedSelectedPlacesOnMap[latlng] = places
            updateSelectedPlacesOnMap(updatedSelectedPlacesOnMap as HashMap<Pair<Double, Double>, List<Place>>)
        }

    }

    fun delSelectedPlacesOnMap(latlng: Pair<Double, Double>) {
        val updatedSelectedPlacesOnMap = _selectedPlacesOnMap.value.toMutableMap()
        if (updatedSelectedPlacesOnMap.containsKey(latlng)) {
            updatedSelectedPlacesOnMap.remove(latlng)
            updateSelectedPlacesOnMap(updatedSelectedPlacesOnMap as HashMap<Pair<Double, Double>, List<Place>>)
        }

    }

    private var _singlePlan = mutableStateOf(singlePlan("", "", listOf(), "", listOf(), ""))
    val singlePlan: MutableState<singlePlan> = _singlePlan
    fun updateSinglePlan(value: singlePlan) {
        _singlePlan.value = value
    }
    fun addHotelsToSinglePlan(value: List<Hotel>){
        singlePlan.value.hotel=value
    }

    private var _planList = mutableStateOf(listOf<singlePlan>())
    val planList: MutableState<List<singlePlan>> = _planList
    fun updatePlanList(value: List<singlePlan>) {
        _planList.value = value
    }

    fun addSinglePlan(value: singlePlan) {
        val updatedPlanList = _planList.value.toMutableList()
        if (!updatedPlanList.contains(value)) {
            updatedPlanList.add(value)
            updatePlanList(updatedPlanList)
        }
    }

    fun delSinglePlan(value: singlePlan) {
        val updatedPlanList = _planList.value.toMutableList()
        if (updatedPlanList.contains(value)) {
            updatedPlanList.remove(value)
            updatePlanList(updatedPlanList)
        }
    }

    private var _planGroup = mutableStateOf(Plans("", "", listOf()))
    val planGroup: MutableState<Plans> = _planGroup
    fun updatePlanGroup(value: Plans) {
        _planGroup.value = value
    }

    private var _planTitle = mutableStateOf("")
    val planTitle: MutableState<String> = _planTitle
    fun updatePlanTitle(value: String) {
        _planTitle.value = value
        updatePlanTitleToPlanGroup()
    }

    private fun updatePlanTitleToPlanGroup() {
        val description = planGroup.value.description
        val planList = planGroup.value.plans
        updatePlanGroup(Plans(planTitle.value, description, planList))
        Log.d("updatePlanGroup", planGroup.value.toString())
    }

    private var _planDescription = mutableStateOf("")
    val planDescription: MutableState<String> = _planDescription
    fun updatePlanDescription(value: String) {
        _planDescription.value = value
        updatePlanDescripToPlanGroup()
    }

    private fun updatePlanDescripToPlanGroup() {
        val title = planGroup.value.title
        val planList = planGroup.value.plans
        updatePlanGroup(Plans(title, planDescription.value, planList))
        Log.d("updatePlanGroup", planGroup.value.toString())
    }

    private var _planOnDetail = mutableStateOf(singlePlan.value)
    val planOnDetail: MutableState<singlePlan> = _planOnDetail
    fun updatePlanOnDetail(value: singlePlan) {
        _planOnDetail.value = value
    }


    //slider
    private val _sliderValue = mutableStateOf(0)
    val sliderValue: MutableState<Int> = _sliderValue

    private val _sliderLabel = MutableLiveData("cheap")
    val sliderLabel: LiveData<String> = _sliderLabel

    fun onSliderValueChanged(newValue: Int) {
        _sliderValue.value = newValue
        _sliderLabel.value = when (newValue) {
            0 -> "cheap"
            1 -> "medium"
            2 -> "expensive"
            3 -> "luxury"
            else -> ""
        }
    }

    private var _travelType = mutableStateOf("driving")
    val travelType: MutableState<String> = _travelType
    fun updateTravelType(value: String) {
        _travelType.value = value
    }

    private val countryList = listOf<String>("Afghanistan", "Albania")
    fun getCountryList(): List<String> {
        return countryList;
    }

    private var _StateList = mutableStateOf(listOf<String>())
    val StateList: MutableState<List<String>> = _StateList;
    fun updateStateList(value: List<String>) {
        _StateList.value = value;
    }

    private var _CityList = mutableStateOf(listOf<String>())
    val CityList: MutableState<List<String>> = _CityList
    fun updateCityList(value: List<String>) {
        _CityList.value = value;
    }

    private var _startAttraction = mutableStateOf(Place("","", Location(0.0,0.0)))
    val startAttraction : MutableState<Place> = _startAttraction
    fun updateStartAttraction(value: Place){
        _startAttraction.value=value
    }

    private var _endAttraction = mutableStateOf(Place("","",Location(0.0,0.0)))
    val endAttraction : MutableState<Place> = _endAttraction
    fun updateEndAttraction(value: Place){
        _endAttraction.value=value
    }

    private var _selectedHotelList = mutableStateOf(listOf<Hotel>())
    val selectedHotelList: MutableState<List<Hotel>> = _selectedHotelList
    fun updateSelectedHotelList(value: List<Hotel>) {
        _selectedHotelList.value = value
    }

    fun addSelectedHotel(hotel: Hotel) {
        val updatedSelectedHotel = _selectedHotelList.value.toMutableList()
        if (!updatedSelectedHotel.contains(hotel)) {
            updatedSelectedHotel.add(hotel)
        }
        updateSelectedHotelList(updatedSelectedHotel)
    }

    fun delSelectedHotel(hotel: Hotel) {
        val updatedSelectedHotel = _selectedHotelList.value.toMutableList()
        if (updatedSelectedHotel.contains(hotel)) {
            updatedSelectedHotel.remove(hotel)
        }
        updateSelectedHotelList(updatedSelectedHotel)
    }


    private var _attractionToHotels = mutableStateOf(mapOf<Place,List<Hotel>>())
    val attractionToHotels: MutableState<Map<Place,List<Hotel>>> = _attractionToHotels
    fun updateAttractionToHotel(value: Map<Place,List<Hotel>>){
        _attractionToHotels.value=value;
    }
    fun addAttractionToHotel(key:Place,value:List<Hotel>){
        val updateMap= _attractionToHotels.value.toMutableMap()
        if(!updateMap.containsKey(key)){
            updateMap[key] = value
        }
        updateAttractionToHotel(updateMap)
    }


    suspend fun getLatLng(city: String, apiKey: String): Location? = withContext(Dispatchers.IO) {
        val url = URL("https://maps.googleapis.com/maps/api/geocode/json?address=$city&key=$apiKey")
        val json = url.readText(Charset.defaultCharset())
        val gson = Gson()
        val response = gson.fromJson(json, GeocodeResponse::class.java)
        response.results.firstOrNull()?.geometry?.location
    }

    suspend fun searchNearbyPlaces(location: Location, radius: Int = 5000000, apiKey: String) {
        withContext(Dispatchers.IO) {
            val url =
                URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${location.lat},${location.lng}&radius=$radius&key=$apiKey&type=tourist_attraction&language=en")
            val json = url.readText(Charset.defaultCharset())
            val gson = Gson()
            val response = gson.fromJson(json, Response::class.java)
            addAttractionsList(response.results.map { result ->
                Place(
                    name = result.name,
                    vicinity = result.vicinity,
                    location = Location(result.geometry.location.lat, result.geometry.location.lng)
                )
            })
            Log.d("attractionlist", attractionsList.toString())
        }
    }

    suspend fun getNearbyPlaces(
        location: Location,
        radius: Int = 5000000,
        apiKey: String
    ): List<Place> {
        return withContext(Dispatchers.IO) {
            val url =
                URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${location.lat},${location.lng}&radius=$radius&key=$apiKey&type=tourist_attraction&language=en")
            val json = url.readText(Charset.defaultCharset())
            val gson = Gson()
            val response = gson.fromJson(json, Response::class.java)
            return@withContext response.results.map { result ->
                Place(
                    name = result.name,
                    vicinity = result.vicinity,
                    location = Location(result.geometry.location.lat, result.geometry.location.lng)
                )
            }
        }
    }

    suspend fun searchNearbyHotels(location: Location, radius: Int = 2000, apiKey: String, maxPriceLevel: Int): List<Hotel> = withContext(Dispatchers.IO) {
        /*
        if no price level information provided, return all the hotels nearby
        if no price level matched, return all the hotels nearby
        return hotels with price level lower or equal to maxPriceLevel
         */
        //if flag==false, no price level information found
        var priceLevelFound = false
        var priceLevelMatched = false
        val url =
            URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${location.lat},${location.lng}&radius=$radius&type=lodging&key=$apiKey&language=en")
        val json = url.readText(Charset.defaultCharset())
        //println(json)
        val gson = Gson()
        val response = gson.fromJson(json, HotelResponse::class.java)

        val mutableHotelList = mutableListOf<HotelResult>()
        for (i in 0 until response.results.size) {
            if (response.results[i].price_level != 0) {
                priceLevelFound = true
            }
            if (response.results[i].price_level <= maxPriceLevel) {
                mutableHotelList.add(response.results[i])
                priceLevelMatched = true
            }
        }
        println("mutableHotelSize: ${mutableHotelList.size}")

        if (!priceLevelFound || (priceLevelFound && !priceLevelMatched)) {
            response.results.map { result ->
                Hotel(
                    place = Place(
                        name = result.name,
                        vicinity = result.vicinity,
                        location = Location(result.geometry.location.lat, result.geometry.location.lng)
                    ),
                    priceLevel = result.price_level,
                    rating = result.rating
                )
            }
        } else {
            mutableHotelList.map { result ->
                Hotel(
                    place = Place(
                        name = result.name,
                        vicinity = result.vicinity,
                        location = Location(result.geometry.location.lat, result.geometry.location.lng)
                    ),
                    priceLevel = result.price_level,
                    rating = result.rating
                )
            }
        }
    }


}