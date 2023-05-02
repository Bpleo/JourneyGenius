package com.example.journeygenius

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.util.Range
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.journeygenius.data.models.*
import com.example.journeygenius.plan.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.nio.charset.Charset
import java.time.LocalDate
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

const val PlacesapiKey = "AIzaSyCNcLRKVJXQ8TL3WRiSujLRVD_qTLMxj8E"

class JourneyGeniusViewModel(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val realtime: DatabaseReference
) : ViewModel() {

    fun uploadList(shareable: Boolean) {
        if (shareable) {
            val plans = Plans(
                title = planTitle.value,
                description = planDescription.value,
                isPublic = isPublic.value,
                plans = planList.value
            )
            val planId = UUID.randomUUID()
            realtime.child("planList").child(planId.toString()).setValue(plans)
        }
        val user = auth.currentUser
        if (user != null) {
            Log.d("PLAN", "Upload ${planTitle.value} to firestore")
            db.collection("users").document(user.uid).update("Plan_List", planGroupList.value)
        }
    }

    fun signOut() {
        Firebase.auth.signOut()
        updateUserName(TextFieldValue(""))
        updateEmail(TextFieldValue(""))
        updatePwd("")
        updateVerifyPwd("")
    }

    //pull plan list from firestore and add to local vm
    fun signIn() {
        val user = auth.currentUser
        if (user != null) {
            Log.d("USER", user.email.toString())
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot != null) {
                        val data = documentSnapshot.data
                        if (data != null) {
                            updateUserName(TextFieldValue(data["name"].toString()))
                            updateEmail(TextFieldValue(data["email"].toString()))
                            updatePwd(data["password"].toString())
                            updateOldEmail(TextFieldValue(data["email"].toString()))
                            // get a list of plans
                            val groupListData =
                                documentSnapshot.get("Plan_List") as? List<Map<String, Any>>
                            val groupList: List<Plans> = groupListData?.mapNotNull { planListData ->
                                val description = planListData["description"] as? String
                                val public = planListData["public"] as? Boolean
                                val title = planListData["title"] as? String
                                // get a list of single plans
                                val plansData = planListData["plans"] as? List<Map<String, Any>>
                                val plans: List<SinglePlan> = plansData?.mapNotNull { planData ->
                                    val date = planData["date"] as? String
                                    val destination = planData["destination"] as? String
                                    val priceLevel = planData["priceLevel"] as? Int
                                    val priceLevelLabel = planData["priceLevelLabel"] as? String
                                    val travelType = planData["travelType"] as? String
                                    // get a list of attractions
                                    val attractionsData =
                                        planData["attractions"] as? List<Map<String, Any>>
                                    val attractions: List<Place> =
                                        attractionsData?.mapNotNull { attraction ->
                                            val name = attraction["name"] as? String
                                            val vicinity = attraction["vicinity"] as? String
                                            // get a location object
                                            val locationData =
                                                attraction["location"] as? Map<String, Any>
                                            val location = locationData?.let {
                                                Location(
                                                    lat = it["lat"] as? Double ?: 0.0,
                                                    lng = it["lng"] as? Double ?: 0.0
                                                )
                                            }
                                            val rating = attraction["rating"] as? Double
                                            val place_id = attraction["place_id"] as? String
                                            // get a list of photos
                                            val photosData =
                                                attraction["photos"] as? List<Map<String, Any>>
                                            val photos: List<Photo>? =
                                                photosData?.mapNotNull { photo ->
                                                    val height = photo["height"] as? Int
                                                    val width = photo["width"] as? Int
                                                    val photo_reference =
                                                        photo["photo_reference"] as? String
                                                    val html_attributions =
                                                        photo["html_attributions"] as? List<String>
                                                    if (height != null && width != null && photo_reference != null && html_attributions != null) {
                                                        Log.d("DATA", "attraction photo pulled")
                                                        Photo(
                                                            height,
                                                            html_attributions,
                                                            photo_reference,
                                                            width
                                                        )
                                                    } else
                                                        null//get a photo object
                                                }
                                            if (name != null && vicinity != null && location != null && rating != null && place_id != null && photos != null) {
                                                Log.d("DATA", "attraction pulled")
                                                Place(
                                                    name,
                                                    vicinity,
                                                    location,
                                                    rating,
                                                    place_id,
                                                    photos
                                                )
                                            } else
                                                null // get a Place Object
                                        } ?: emptyList()
                                    // get a list of hotels
                                    val hotelsData = planData["hotel"] as? List<Map<String, Any>>
                                    val hotel: List<Hotel> = hotelsData?.mapNotNull { hotelData ->
                                        val priceLevel = hotelData["priceLevel"] as? Int
                                        val placeData = hotelData["place"] as? Map<String, Any>
                                        val place: Place? = placeData?.let {
                                            val name = it["name"] as? String
                                            val vicinity = it["vicinity"] as? String
                                            // get a location object
                                            val locationData = it["location"] as? Map<String, Any>
                                            val location = locationData?.let {
                                                Location(
                                                    lat = it["lat"] as? Double ?: 0.0,
                                                    lng = it["lng"] as? Double ?: 0.0
                                                )
                                            }
                                            val rating = it["rating"] as? Double
                                            val place_id = it["place_id"] as? String
                                            // get a list of photos
                                            val photosData = it["photos"] as? List<Map<String, Any>>
                                            val photos: List<Photo>? =
                                                photosData?.mapNotNull { photo ->
                                                    val height = photo["height"] as? Int
                                                    val width = photo["width"] as? Int
                                                    val photo_reference =
                                                        photo["photo_reference"] as? String
                                                    val html_attributions =
                                                        photo["html_attributions"] as? List<String>
                                                    if (height != null && width != null && photo_reference != null && html_attributions != null) {
                                                        Log.d("DATA", "hotel photo pulled")
                                                        Photo(
                                                            height,
                                                            html_attributions,
                                                            photo_reference,
                                                            width
                                                        )
                                                    } else
                                                        null//get a photo object
                                                }
                                            if (name != null && vicinity != null && location != null && rating != null && place_id != null && photos != null) {
                                                Log.d("DATA", "hotel place pulled")
                                                Place(
                                                    name,
                                                    vicinity,
                                                    location,
                                                    rating,
                                                    place_id,
                                                    photos
                                                )
                                            } else
                                                null // get a place Object
                                        }
                                        // get a hotel object
                                        if (priceLevel != null && place != null) {
                                            Log.d("DATA", "hotel pulled")
                                            Hotel(place, priceLevel)
                                        } else
                                            null
                                    } ?: emptyList()
                                    // get a SinglePlan Object
                                    if (date != null && destination != null && attractions != null && priceLevel != null
                                        && priceLevelLabel != null && hotel != null && travelType != null
                                    ) {
                                        Log.d("DATA", "single plan pulled")
                                        SinglePlan(
                                            date,
                                            destination,
                                            attractions,
                                            priceLevel,
                                            priceLevelLabel,
                                            hotel,
                                            travelType
                                        )
                                    } else
                                        null
                                } ?: emptyList()
                                // get a Plans object
                                if (title != null && description != null && public != null && plans != null) {
                                    Log.d("DATA", "plan list pulled")
                                    Plans(title, description, public, plans)
                                } else
                                    null
                            } ?: emptyList()
                            // append groupList to current vm
                            updatePlanGroupList(groupList)
                            print(groupList)
                        } else {
                            Log.d("FIRESTORE", "No data found")
                        }
                    } else {
                        Log.d("FIRESTORE", "Document not found")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("FIRESTORE", "Error getting document", exception)
                }
        } else {
            Log.d("USER", "No User Logged In")
        }
    }

    private var _userName = mutableStateOf(TextFieldValue())
    val userName: MutableState<TextFieldValue> = _userName

    fun updateUserName(userName: TextFieldValue) {
        _userName.value = userName
    }

    fun resetUserName(userName: TextFieldValue) {
        val user = auth.currentUser
        if (user != null) {
            db.collection("users").document(user.uid).update("name", userName.text)
        }
    }

    private var _email = mutableStateOf(TextFieldValue())
    val email: MutableState<TextFieldValue> = _email
    private var oldEmail = TextFieldValue()

    fun updateEmail(email: TextFieldValue) {
        _email.value = email
    }

    private fun updateOldEmail(email: TextFieldValue) {
        oldEmail = email
    }

    fun resetEmail(newEmail: TextFieldValue) {
        Log.i("USER", "oldemail: $oldEmail")
        auth.signOut()
        auth.signInWithEmailAndPassword(oldEmail.text, pwd.value).addOnSuccessListener {
            val user = auth.currentUser
            if (user != null) {
                db.collection("users").document(user.uid)
                    .update("email", newEmail.text)
                    .addOnSuccessListener {
                        user.updateEmail(newEmail.text).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("USER", "${newEmail.text} updated successfully")
                                updateOldEmail(newEmail)
                            } else {
                                Log.w("USER", "${newEmail.text} update failed", task.exception)
                            }
                        }
                    }
            }
        }
    }

    private var _pwd = mutableStateOf(String())
    val pwd: MutableState<String> = _pwd

    fun updatePwd(pwd: String) {
        _pwd.value = pwd
    }

    fun resetPwd() {
        val newPwd = _pwd.value
        auth.sendPasswordResetEmail(_email.value.text)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    db.collection("users").document(auth.currentUser!!.uid)
                        .update("password", newPwd)
                    Log.d("USER", "password updated successfully")
                } else {
                    Log.w("USER", "password update failed", task.exception)
                }
            }
    }

    private var _verifyPwd = mutableStateOf(String())
    val verifyPwd: MutableState<String> = _verifyPwd

    fun updateVerifyPwd(pwd: String) {
        _verifyPwd.value = pwd
    }

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
        _selectedPlacesOnMap.value = value
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

    private var _singlePlan =
        mutableStateOf(SinglePlan("", "", listOf(), 4, "luxury", listOf(), ""))
    val singlePlan: MutableState<SinglePlan> = _singlePlan
    fun updateSinglePlan(value: SinglePlan) {
        _singlePlan.value = value
    }

    fun addHotelsToSinglePlan(value: List<Hotel>) {
        singlePlan.value.hotel = value
    }

    private var _planList = mutableStateOf(listOf<SinglePlan>())
    val planList: MutableState<List<SinglePlan>> = _planList
    fun updatePlanList(value: List<SinglePlan>) {
        _planList.value = value
    }

    fun addSinglePlan(value: SinglePlan) {
        val updatedPlanList = _planList.value.toMutableList()
        if (!updatedPlanList.contains(value)) {
            updatedPlanList.add(value)
            updatePlanList(updatedPlanList)
        }
    }

    fun delSinglePlan(value: SinglePlan) {
        val updatedPlanList = _planList.value.toMutableList()
        if (updatedPlanList.contains(value)) {
            updatedPlanList.remove(value)
            updatePlanList(updatedPlanList)
        }
    }

    private var _planGroup = mutableStateOf(Plans("", "", true, listOf()))
    val planGroup: MutableState<Plans> = _planGroup
    fun updatePlanGroup(value: Plans) {
        _planGroup.value = value
    }


    //plan title in a planGroup
    private var _planTitle = mutableStateOf("")
    val planTitle: MutableState<String> = _planTitle
    fun updatePlanTitle(value: String) {
        _planTitle.value = value
        updatePlanTitleToPlanGroup()
    }

    private fun updatePlanTitleToPlanGroup() {
        val isPublic = planGroup.value.isPublic
        val description = planGroup.value.description
        val planList = planGroup.value.plans
        updatePlanGroup(Plans(planTitle.value, description, isPublic, planList))
        Log.d("updatePlanGroup", planGroup.value.toString())
    }

    //plan description in a planGroup
    private var _planDescription = mutableStateOf("")
    val planDescription: MutableState<String> = _planDescription
    fun updatePlanDescription(value: String) {
        _planDescription.value = value
        updatePlanDescripToPlanGroup()
    }

    private fun updatePlanDescripToPlanGroup() {
        val title = planGroup.value.title
        val isPublic = planGroup.value.isPublic
        val planList = planGroup.value.plans
        updatePlanGroup(Plans(title, planDescription.value, isPublic, planList))
        Log.d("updatePlanGroup", planGroup.value.toString())
    }


    //value change when user click different plan on Plan List page
    private var _planOnDetail = mutableStateOf(singlePlan.value)
    val planOnDetail: MutableState<SinglePlan> = _planOnDetail
    fun updatePlanOnDetail(value: SinglePlan) {
        _planOnDetail.value = value
    }

    private var _planGroupList = mutableStateOf(listOf<Plans>())
    val planGroupList: MutableState<List<Plans>> = _planGroupList
    fun updatePlanGroupList(value: List<Plans>) {
        _planGroupList.value = value
    }

    fun addPlanGroupToList(value: Plans) {
        if (!_planGroupList.value.contains(value)) {
            val updatedGroupList = _planGroupList.value.toMutableList()
            updatedGroupList.add(value)
            updatePlanGroupList(updatedGroupList)
        }
    }

    fun delPlanGroupToList(value: Plans) {
        if (_planGroupList.value.contains(value)) {
            val updatedGroupList = _planGroupList.value.toMutableList()
            updatedGroupList.remove(value)
            updatePlanGroupList(updatedGroupList)
        }
    }

    //price level slider variable
    private val _sliderValue = MutableLiveData(0)
    val sliderValue: MutableLiveData<Int> = _sliderValue

    private val _sliderLabel = MutableLiveData("cheap")
    val sliderLabel: LiveData<String> = _sliderLabel

    fun onSliderValueChanged(newValue: Int) {
        _sliderValue.value = newValue
        _sliderLabel.value = when (newValue) {
            0 -> "cheap"
            1 -> "medium"
            2 -> "expensive"
            3 -> "luxury"
            4 -> "extravagant"
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
        return countryList
    }

    private var _StateList = mutableStateOf(listOf<String>())
    val StateList: MutableState<List<String>> = _StateList
    fun updateStateList(value: List<String>) {
        _StateList.value = value
    }

    private var _CityList = mutableStateOf(listOf<String>())
    val CityList: MutableState<List<String>> = _CityList
    fun updateCityList(value: List<String>) {
        _CityList.value = value
    }

    private var _startAttraction =
        mutableStateOf(Place("", "", Location(0.0, 0.0), 0.0, "", listOf()))
    val startAttraction: MutableState<Place> = _startAttraction
    fun updateStartAttraction(value: Place) {
        _startAttraction.value = value
    }

    private var _endAttraction =
        mutableStateOf(Place("", "", Location(0.0, 0.0), 0.0, "", listOf()))
    val endAttraction: MutableState<Place> = _endAttraction
    fun updateEndAttraction(value: Place) {
        _endAttraction.value = value
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


    private var _attractionToHotels = mutableStateOf(mapOf<Place, List<Hotel>>())
    val attractionToHotels: MutableState<Map<Place, List<Hotel>>> = _attractionToHotels
    fun updateAttractionToHotel(value: Map<Place, List<Hotel>>) {
        _attractionToHotels.value = value
    }

    fun addAttractionToHotel(key: Place, value: List<Hotel>) {
        val updateMap = _attractionToHotels.value.toMutableMap()
        if (!updateMap.containsKey(key)) {
            updateMap[key] = value
        }
        updateAttractionToHotel(updateMap)
    }

    private val _travelModeOption = mutableStateOf("driving")
    val travelModeOption: MutableState<String> = _travelModeOption

    fun onTravelModeChanged(option: String) {
        _travelModeOption.value = option
    }

    private val _isPublic = mutableStateOf(true)
    val isPublic: MutableState<Boolean> = _isPublic

    fun onPublicSwitched(isChecked: Boolean) {
        _isPublic.value = isChecked
        updatePlanIsPublicToPlanGroup(isChecked)
    }

    private fun updatePlanIsPublicToPlanGroup(isChecked: Boolean) {
        val title = planGroup.value.title
        val description = planGroup.value.description
        val planList = planGroup.value.plans
        updatePlanGroup(Plans(title, description, isChecked, planList))
        Log.d("updatePlanGroup", planGroup.value.toString())
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
                    location = Location(result.geometry.location.lat, result.geometry.location.lng),
                    rating = result.rating,
                    place_id = result.place_id,
                    photos = result.photos?.toList()
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
                    location = Location(result.geometry.location.lat, result.geometry.location.lng),
                    rating = result.rating,
                    place_id = result.place_id,
                    photos = result.photos?.toList()
                )
            }
        }
    }

    suspend fun searchNearbyHotels(
        placeName: String,
        location: Location,
        radius: Int = 2000,
        apiKey: String,
        maxPriceLevel: Int,
        context: Context
    ): List<Hotel> = withContext(Dispatchers.IO) {
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
            // TODO: Toast Upgrade
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(
                    context,
                    "No hotels found within the specified price range near $placeName",
                    Toast.LENGTH_LONG
                ).show()
            }
            response.results.map { result ->
                Hotel(
                    place = Place(
                        name = result.name,
                        vicinity = result.vicinity,
                        location = Location(
                            result.geometry.location.lat,
                            result.geometry.location.lng
                        ),
                        rating = result.rating,
                        place_id = result.place_id,
                        photos = result.photos?.toList()
                    ),
                    priceLevel = result.price_level,

                    )
            }
        } else {
            mutableHotelList.map { result ->
                Hotel(
                    place = Place(
                        name = result.name,
                        vicinity = result.vicinity,
                        location = Location(
                            result.geometry.location.lat,
                            result.geometry.location.lng
                        ),
                        rating = result.rating,
                        place_id = result.place_id,
                        photos = result.photos?.toList()
                    ),
                    priceLevel = result.price_level,

                    )
            }
        }
    }

    suspend fun getRoutes(
        from: LatLng,
        to: LatLng,
        apiKey: String,
        waypoints: List<LatLng>,
        travelModeOption: String,
        context: Context
    ): List<List<LatLng>> = withContext(Dispatchers.IO) {

        val url = getURL(from, to, apiKey, waypoints, travelModeOption)
        val result = URL(url).readText()
        val jsonObject = JsonParser.parseString(result).asJsonObject
        val routes = jsonObject.getAsJsonArray("routes")
        val allRoutes = mutableListOf<List<LatLng>>()


//    for (i in 0 until routes.size()) {
//        val points = routes[i].asJsonObject
//            .getAsJsonArray("legs")[0].asJsonObject
//            .getAsJsonArray("steps")
//            .flatMap {
//                decodePoly(it.asJsonObject.getAsJsonObject("polyline").get("points").asString)
//            }
//        allPoints.add(points)
//    }
        if (routes.isEmpty) {
            val newTravelMode = when (travelModeOption) {
                "bicycling" -> "walking"
                "transit" -> "driving"
                else -> "driving"
            }
            val newUrl = getURL(from, to, apiKey, emptyList(), newTravelMode)
            val newResult = URL(newUrl).readText()
            val newJsonObj = JsonParser.parseString(newResult).asJsonObject
            val newRoutes = newJsonObj.getAsJsonArray("routes")
            if (newRoutes.isEmpty) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        context,
                        "No more travel mode matching",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                println("No more travel mode matching")
                return@withContext emptyList()
            }
            for (i in 0 until newRoutes.size()) {
                val points = newRoutes[i].asJsonObject
                    .getAsJsonArray("legs")[0].asJsonObject
                    .getAsJsonArray("steps")
                    .flatMap {
                        decodePoly(
                            it.asJsonObject.getAsJsonObject("polyline").get("points").asString
                        )
                    }
                allRoutes.add(points)
            }
            println("$travelModeOption not exist, show $newTravelMode instead")
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(
                    context,
                    "$travelModeOption not exist, show $newTravelMode instead",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            for (i in 0 until routes.size()) {
                val points = routes[i].asJsonObject
                    .getAsJsonArray("legs")[0].asJsonObject
                    .getAsJsonArray("steps")
                    .flatMap {
                        decodePoly(
                            it.asJsonObject.getAsJsonObject("polyline").get("points").asString
                        )
                    }
                allRoutes.add(points)
            }
            println("$travelModeOption exist")
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(
                    context,
                    "$travelModeOption exist",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        allRoutes
    }


}