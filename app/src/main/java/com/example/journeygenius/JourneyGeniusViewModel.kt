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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import java.net.URL
import java.nio.charset.Charset
import java.time.LocalDate
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

const val PlacesapiKey = "AIzaSyCNcLRKVJXQ8TL3WRiSujLRVD_qTLMxj8E"

class JourneyGeniusViewModel(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val realtime: DatabaseReference
) : ViewModel() {

    private fun uploadList(shareable: Boolean, planId: String) {
        val plans = Plans(
            title = planTitle.value,
            description = planDescription.value,
            isPublic = isPublic.value,
            plans = planList.value,
            likes = planLikes.value
        )
        if (shareable) {
            realtime.child("planList").child(planId).setValue(plans)
        }
        val user = auth.currentUser
        if (user != null) {
            db.collection("users").document(user.uid).update("Plan_List", _planGroupList.value)
                .addOnSuccessListener {
                    Log.d("PLAN", "Upload ${planTitle.value} to firestore")
                }
        }
    }

    private fun realTimeDataFetch(
        limit: Long = 10,
        onComplete: (Map<String, Plans>, String) -> Unit
    ) {
        var query = realtime.child("planList").orderByKey().limitToFirst(limit.toInt())
        if (_startAtValue.value.isNotEmpty()){
            query = query.startAt(_startAtValue.value)
        }
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val groupListData = snapshot.value as? Map<String, Any>?
                val groupList = getGroupList(groupListData)
//                Log.d("REALTIME1", groupList.toString())
                val nextStartAtValue = snapshot.children.lastOrNull()?.key.orEmpty()
                _startAtValue.value = nextStartAtValue
                if (groupList != null)
                    onComplete(groupList, nextStartAtValue)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("DATA", "Realtime pulled data false", error.toException())
            }
        })
    }

    suspend fun fetchGroupData(limit: Long): Map<String, Plans> {
        return suspendCoroutine { continuation ->
            realTimeDataFetch(limit) { groupListData, _ ->
//                Log.d("REALTIME2", groupListData.toString())
                continuation.resume(groupListData)
            }
        }
    }

    private val _communityPlanList = mutableStateOf(mapOf<String, Plans>())
    val communityPlanList: MutableState<Map<String, Plans>> = _communityPlanList

    private fun updateCommunityPlanList(value : Map<String, Plans>){
        _communityPlanList.value = value
    }

    fun fetchGroupDataAndPrint(limit: Long, startAtValue: String = "") {
        viewModelScope.launch {
            try {
                val groupListData = fetchGroupData(limit)
//                Log.d("REALTIME3",groupListData.toString())
                _communityPlanList.value = _communityPlanList.value + groupListData
            } catch (e: Exception) {
                Log.e("fetchGroupDataAndPrint","Error fetching data: ${e.message}")
            }
        }
    }

    fun getPlanById(category: String, planId: String): Plans? {
        if (category == "Community") {
            return communityPlanList.value[planId]
        } else {
            return planGroupList.value[planId]
        }
    }


    private fun getGroupList(groupListData: Map<String, Any>?): Map<String, Plans> {
        val groupList = groupListData?.mapNotNull { (key, value) ->
            val plans = (value as? Map<String, Any>)?.let { planListData ->
                val description = planListData["description"] as? String
                val public = planListData["public"] as? Boolean
                val title = planListData["title"] as? String
                val likesData = planListData["likes"] as? Long
                val likes = likesData?.toInt()
                // get a list of single plans
                val plansData =
                    planListData["plans"] as? List<Map<String, Any>>
                val plans: List<SinglePlan> =
                    plansData?.mapNotNull { planData ->
                        val date = planData["date"] as? String
                        val destination = planData["destination"] as? String
                        val priceLevelData = planData["priceLevel"] as? Long
                        val priceLevel: Int = priceLevelData?.toInt() ?: 0
                        val priceLevelLabel =
                            planData["priceLevelLabel"] as? String
                        val travelType = planData["travelType"] as? String
                        // get a list of attractions
                        val attractionsData = planData["attractions"] as? List<Map<String, Any>>
                        val attractions: List<Place> =
                            attractionsData?.mapNotNull { data ->
                                val name = data["name"] as? String
                                val vicinity = data["vicinity"] as? String
                                val locationData = data["location"] as? Map<String, Any>
                                val location = locationData?.let {
                                    Location(
                                        lat = it["lat"] as? Double ?: 0.0,
                                        lng = it["lng"] as? Double ?: 0.0
                                    )
                                }
                                val ratingData = data["rating"]
                                val rating: Double? = if (ratingData is Long)
                                    ratingData.toDouble()
                                else
                                    ratingData as Double
                                val place_id = data["place_id"] as? String
                                // get a list of photos
                                val photosData = data["photos"] as? List<Map<String, Any>>
                                val photos: List<Photo>? = photosData?.mapNotNull { photo ->
                                    val heightData = photo["height"] as? Long
                                    val height = heightData?.toInt() ?: 0
                                    val widthData = photo["width"] as? Long
                                    val width = widthData?.toInt() ?: 0
                                    val photo_reference = photo["photo_reference"] as? String
                                    val html_attributions = photo["html_attributions"] as? List<String>
                                    if (height != null && width != null && photo_reference != null && html_attributions != null){
                                        Log.d("DATA", "attraction photo pulled")
                                        Photo(height, html_attributions, photo_reference, width)
                                    } else
                                        null
                                }
                                if (name != null && vicinity != null && location != null && rating != null && place_id != null) {
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
                        // get startAttraction
                        val startAttractionData = planData["startAttraction"] as? Map<String, Any>
                        val startAttraction = startAttractionData?.let {data ->
                            val name = data["name"] as? String
                            val vicinity = data["vicinity"] as? String
                            val locationData = data["location"] as? Map<String, Any>
                            val location = locationData?.let {
                                Location(
                                    lat = it["lat"] as? Double ?: 0.0,
                                    lng = it["lng"] as? Double ?: 0.0
                                )
                            }
                            val ratingData = data["rating"]
                            val rating: Double? = if (ratingData is Long)
                                ratingData.toDouble()
                            else
                                ratingData as Double
                            val place_id = data["place_id"] as? String
                            // get a list of photos
                            val photosData = data["photos"] as? List<Map<String, Any>>
                            val photos: List<Photo>? = photosData?.mapNotNull { photo ->
                                val heightData = photo["height"] as? Long
                                val height = heightData?.toInt() ?: 0
                                val widthData = photo["width"] as? Long
                                val width = widthData?.toInt() ?: 0
                                val photo_reference = photo["photo_reference"] as? String
                                val html_attributions = photo["html_attributions"] as? List<String>
                                if (height != null && width != null && photo_reference != null && html_attributions != null){
                                    Log.d("DATA", "attraction photo pulled")
                                    Photo(height, html_attributions, photo_reference, width)
                                } else
                                    null
                            }
                            if (name != null && vicinity != null && location != null && rating != null && place_id != null) {
                                Log.d("DATA", "start attraction pulled")
                                Place(name, vicinity, location, rating, place_id, photos)
                            } else
                                null // get a Place Object
                        }
                        // get endAttraction
                        val endAttractionData = planData["endAttraction"] as? Map<String, Any>
                        val endAttraction = endAttractionData?.let { data ->
                            val name = data["name"] as? String
                            val vicinity = data["vicinity"] as? String
                            val locationData = data["location"] as? Map<String, Any>
                            val location = locationData?.let {
                                Location(
                                    lat = it["lat"] as? Double ?: 0.0,
                                    lng = it["lng"] as? Double ?: 0.0
                                )
                            }
                            val ratingData = data["rating"]
                            val rating: Double? = if (ratingData is Long)
                                ratingData.toDouble()
                            else
                                ratingData as Double
                            val place_id = data["place_id"] as? String
                            // get a list of photos
                            val photosData = data["photos"] as? List<Map<String, Any>>
                            val photos: List<Photo>? = photosData?.mapNotNull { photo ->
                                val heightData = photo["height"] as? Long
                                val height = heightData?.toInt()
                                val widthData = photo["width"] as? Long
                                val width = widthData?.toInt()
                                val photo_reference = photo["photo_reference"] as? String
                                val html_attributions = photo["html_attributions"] as? List<String>
                                if (height != null && width != null && photo_reference != null && html_attributions != null){
                                    Log.d("DATA", "attraction photo pulled")
                                    Photo(height, html_attributions, photo_reference, width)
                                } else
                                    null
                            }
                            if (name != null && vicinity != null && location != null && rating != null && place_id != null) {
                                Log.d("DATA", "start attraction pulled")
                                Place(name, vicinity, location, rating, place_id, photos)
                            } else
                                null // get a Place Object
                        }
                        // get attractionRoutes
                        val attractionRoutesData = planData["attractionRoutes"] as? List<Map<String, Any>>
                        val attractionRoutes: List<Place> = attractionRoutesData?.mapNotNull { data->
                            val name = data["name"] as? String
                            val vicinity = data["vicinity"] as? String
                            val locationData = data["location"] as? Map<String, Any>
                            val location = locationData?.let {
                                Location(
                                    lat = it["lat"] as? Double ?: 0.0,
                                    lng = it["lng"] as? Double ?: 0.0
                                )
                            }
                            val ratingData = data["rating"]
                            val rating: Double? = if (ratingData is Long)
                                ratingData.toDouble()
                            else
                                ratingData as Double
                            val place_id = data["place_id"] as? String
                            // get a list of photos
                            val photosData = data["photos"] as? List<Map<String, Any>>
                            val photos: List<Photo>? = photosData?.mapNotNull { photo ->
                                val heightData = photo["height"] as? Long
                                val height = heightData?.toInt()
                                val widthData = photo["width"] as? Long
                                val width = widthData?.toInt()
                                val photo_reference = photo["photo_reference"] as? String
                                val html_attributions = photo["html_attributions"] as? List<String>
                                if (height != null && width != null && photo_reference != null && html_attributions != null){
                                    Log.d("DATA", "attraction photo pulled")
                                    Photo(height, html_attributions, photo_reference, width)
                                } else
                                    null
                            }
                            if (name != null && vicinity != null && location != null && rating != null && place_id != null) {
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
                        val hotelData = planData["hotel"] as? List<Map<String, Any>>
                        val hotel:List<Hotel> = hotelData?.mapNotNull { hotelData ->
                            val priceLevelData = hotelData["priceLevel"] as? Long
                            val priceLevel = priceLevelData?.toInt()
                            val placeData = hotelData["place"] as? Map<String, Any>
                            val place = placeData?.let{
                                    data ->
                                val name = data["name"] as? String
                                val vicinity = data["vicinity"] as? String
                                val locationData = data["location"] as? Map<String, Any>
                                val location = locationData?.let {
                                    Location(
                                        lat = it["lat"] as? Double ?: 0.0,
                                        lng = it["lng"] as? Double ?: 0.0
                                    )
                                }
                                val ratingData = data["rating"]
                                val rating: Double? = if (ratingData is Long)
                                    ratingData.toDouble()
                                else
                                    ratingData as Double
                                val place_id = data["place_id"] as? String
                                // get a list of photos
                                val photosData = data["photos"] as? List<Map<String, Any>>
                                val photos: List<Photo>? = photosData?.mapNotNull { photo ->
                                    val heightData = photo["height"] as? Long
                                    val height = heightData?.toInt()
                                    val widthData = photo["width"] as? Long
                                    val width = widthData?.toInt()
                                    val photo_reference = photo["photo_reference"] as? String
                                    val html_attributions = photo["html_attributions"] as? List<String>
                                    if (height != null && width != null && photo_reference != null && html_attributions != null){
                                        Log.d("DATA", "hotel photo pulled")
                                        Photo(height, html_attributions, photo_reference, width)
                                    } else
                                        null
                                }
                                if (name != null && vicinity != null && location != null && rating != null && place_id != null) {
                                    Log.d("DATA", "hotel place pulled")
                                    Place(name, vicinity, location, rating, place_id, photos)
                                } else
                                    null // get a Place Object
                            }
                            if (priceLevel != null && place != null){
                                Log.d("DATA", "hotel pulled")
                                Hotel(place, priceLevel)
                            } else {
                                null
                            }
                        }?: emptyList()
                        // get a SinglePlan Object
                        if (date != null && destination != null && attractions != null && priceLevel != null
                            && startAttraction != null && endAttraction != null && attractionRoutes != null
                            && priceLevelLabel != null && hotel != null && travelType != null
                        ) {
                            Log.d("DATA", "single plan pulled")
                            SinglePlan(
                                date,
                                destination,
                                attractions,
                                startAttraction,
                                endAttraction,
                                attractionRoutes,
                                priceLevel,
                                priceLevelLabel,
                                hotel,
                                travelType
                            )
                        } else
                            null
                    } ?: emptyList()
                // get a Plans object
                if (title != null && description != null && public != null && plans != null && likes != null) {
                    Log.d("DATA", "plan list pulled")
                    Plans(title, description, public, plans, likes)
                } else
                    null
            }
            plans?.let { key to it }
        }!!.toMap()
        return groupList
    }

    fun signOut() {
        Firebase.auth.signOut()
        updateUserName(TextFieldValue(""))
        updateEmail(TextFieldValue(""))
        updatePwd("")
        updateVerifyPwd("")
    }

    private val _startAtValue = mutableStateOf("")
    fun updateStartAtValue(value: String){
        _startAtValue.value = value
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
                                documentSnapshot.get("Plan_List") as? Map<String, Any>
                            var groupList: Map<String, Plans> = emptyMap()
                            if (groupListData != null)
                                groupList = getGroupList(groupListData)
                            val likedListData = documentSnapshot.get("likedPlanList") as List<String>
                            updateLikedPlanList(likedListData)
                            // append groupList to current vm
                            if (groupList != null)
                                updatePlanGroupList(groupList)
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

    //change given plan's visibility, either make it public or private by given variable
    fun updatePlanVisibility(public: Boolean, planId: String){
        val user = auth.currentUser
        val fireStoreUpdates = mapOf(
            "Plan_List.$planId.public" to public
        )
        if (user != null) {
            val planGroupList = _planGroupList.value.toMutableMap()
            val updatePlan = planGroupList[planId]
            if (updatePlan != null){
                updatePlan.isPublic = public
                planGroupList[planId] = updatePlan
                updatePlanGroupList(planGroupList)
                if (public) {
                    db.collection("users").document(user.uid).update(fireStoreUpdates)
                        .addOnSuccessListener {
                            realtime.child("planList").child(planId).setValue(updatePlan)
                                .addOnSuccessListener {
                                    Log.d("DATA","$planId change to public")
                                }
                                .addOnFailureListener {exception ->
                                    Log.e("DATA","Error updating likes field: $exception")
                                }
                        }
                        .addOnFailureListener {exception ->
                            Log.e("DATA","Error updating likes field: $exception")
                        }
                } else {
                    db.collection("users").document(user.uid).update(fireStoreUpdates)
                        .addOnSuccessListener {
                            realtime.child("planList").child(planId).removeValue()
                                .addOnSuccessListener {
                                    Log.d("DATA","$planId change to private")

                                }
                                .addOnFailureListener {
                                        exception ->
                                    Log.e("DATA","Error updating likes field: $exception")
                                }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("DATA","Error updating likes field: $exception")
                        }
                }
                updateStartAtValue("")
                updateCommunityPlanList(emptyMap())
            }
        }
    }

    fun updateLikes(likes: Int, planId: String, isLikeAction: Boolean){
        val user = auth.currentUser
        val fireStoreUpdates = mapOf(
            "Plan_List.$planId.likes" to likes
        )
        val realtimeUpdates = mapOf<String, Any>(
            "/planList/$planId/likes" to likes
        )
        if (user != null) {
            val list = _likedPlanList.value.toMutableList()
            val communityPlanList = _communityPlanList.value.toMutableMap()
            if (isLikeAction) {
                list.add(planId)
            } else {
                if (list.contains(planId))
                    list.remove(planId)
            }
            val updatePlan = communityPlanList[planId]
            if (updatePlan != null) {
                updatePlan.likes = likes
                communityPlanList[planId] = updatePlan
                updateCommunityPlanList(communityPlanList)
            }
            updateLikedPlanList(list)
            db.collection("users").document(user.uid).update("likedPlanList",list).addOnSuccessListener {
                db.collection("users").document(user.uid).update(fireStoreUpdates)
                    .addOnSuccessListener {
                        realtime.updateChildren(realtimeUpdates)
                            .addOnSuccessListener {
                                Log.d("DATA", "$planId's like updates to $likes")
                            }
                            .addOnFailureListener { exception ->
                                Log.e("DATA","Error updating likes field: $exception")
                            }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("DATA","Error updating likes field: $exception")
                    }
            }
        }
    }


    private var _languageList = mutableStateOf(mapOf<String, String>(
        "en" to "English",
        "zh" to "中文",
        "es" to "Español"
    ))
    val languageList: MutableState<Map<String,String>> = _languageList

    private var _language = mutableStateOf("en")

    val language: MutableState<String> = _language


    fun updateLanguage(value: String) {
        _language.value = value
        Log.d("Data", "Try changing language to "+ _language.value)
        // TODO:
        //  Need to change the device language settings here
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
            Log.d("USER", "User Name Updated ${userName.text}")
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

    private var _dateRange = mutableStateOf(Range(LocalDate.now(), LocalDate.now().plusDays(3)))
    val dateRange: MutableState<Range<LocalDate>> = _dateRange

    fun updateRange(start: LocalDate, end: LocalDate) {
        _dateRange.value = Range(start, end)
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

    private var _likedPlanList = mutableStateOf<List<String>>(listOf())
    val likedPlanList: MutableState<List<String>> = _likedPlanList
    fun updateLikedPlanList(value: List<String>){
        _likedPlanList.value = value
    }

    private var _attractionRoutes = mutableStateOf<List<Place>>(listOf())
    val attractionRoutes:MutableState<List<Place>> = _attractionRoutes
    fun updateAttractionRoutes(value: List<Place>){
        _attractionRoutes.value=value
    }
    fun addAttractionToRoutes(value: Place){
        val updatedRoutes=_attractionRoutes.value.toMutableList()
        if(!updatedRoutes.contains(value)){
            updatedRoutes.add(value)
            updateAttractionRoutes(updatedRoutes)
        }
    }
    fun delAttractionToRoutes(value:Place){
        val updatedRoutes=_attractionRoutes.value.toMutableList()
        if(updatedRoutes.contains(value)){
            updatedRoutes.remove(value)
            updateAttractionRoutes(updatedRoutes)
        }
    }


    private var _singlePlan =
        mutableStateOf(SinglePlan("", "", listOf(),
            Place("", "", Location(0.0, 0.0), 0.0, "", listOf()),
            Place("", "", Location(0.0, 0.0), 0.0, "", listOf()),
            listOf(),4, "extravagant", listOf(), ""))
    val singlePlan: MutableState<SinglePlan> = _singlePlan
    fun updateSinglePlan(value: SinglePlan) {
        _singlePlan.value = value
    }

    fun addHotelsToSinglePlan(value: List<Hotel>) {
        singlePlan.value.hotel = value
    }
    fun addRoutesToSinglePlan(value:List<Place>){
        singlePlan.value.attractionRoutes=value
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
            updatePlanGroup(
                Plans(
                    planGroup.value.title,
                    planGroup.value.description,
                    planGroup.value.isPublic,
                    planList.value
                )
            )
        }

    }
    private var _planGroup = mutableStateOf(Plans("", "", true, listOf()))
    val planGroup: MutableState<Plans> = _planGroup
    fun updatePlanGroup(value: Plans) {
        _planGroup.value = value
    }

    //plan Likes in a planGroup, 0 as default
    private var _planLikes = mutableStateOf(0)
    val planLikes: MutableState<Int> = _planLikes

    fun updatePlanLikes(value: Int) {
        _planLikes.value = value
        updatePlanLikesToPlanGroup()
    }

    private fun updatePlanLikesToPlanGroup() {
        val isPublic = planGroup.value.isPublic
        val description = planGroup.value.description
        val planList = planGroup.value.plans
        val title = planGroup.value.title
        updatePlanGroup(Plans(title, description, isPublic, planList, planGroup.value.likes))
        Log.d("updatePlanGroup", planGroup.value.toString())
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
        val planLikes = planGroup.value.likes
        updatePlanGroup(Plans(planTitle.value, description, isPublic, planList, planLikes))
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
        val planLikes = planGroup.value.likes
        updatePlanGroup(Plans(title, planDescription.value, isPublic, planList, planLikes))
        Log.d("updatePlanGroup", planGroup.value.toString())
    }


    //value change when user click different plan on Plan List page
    private var _planOnDetail = mutableStateOf(singlePlan.value)
    val planOnDetail: MutableState<SinglePlan> = _planOnDetail
    fun updatePlanOnDetail(value: SinglePlan) {
        _planOnDetail.value = value
    }

    private var _planGroupList = mutableStateOf(mapOf<String, Plans>())
    val planGroupList: MutableState<Map<String, Plans>> = _planGroupList
    fun updatePlanGroupList(value: Map<String, Plans>) {
        _planGroupList.value = value
    }

    fun addPlanGroupToList(value: Plans) {
        val containPlan = _planGroupList.value.any { (_, values) -> values == value }
        if (!containPlan) {
            val planId = UUID.randomUUID()
            val updatedGroupList = _planGroupList.value.toMutableMap()
            updatedGroupList[planId.toString()] = value
            updatePlanGroupList(updatedGroupList)
            uploadList(isPublic.value, planId.toString())
        }
    }

    fun delPlanGroupToList(planId: String) {
        if (_planGroupList.value.contains(planId)) {
            val updatedGroupList = _planGroupList.value.toMutableMap()
            updatedGroupList.remove(planId)
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

    private var _countryList = mutableStateOf(mapOf<String,Int>())
    val countryList: MutableState<Map<String,Int>> = _countryList
    private fun updateCountryList(value: Map<String,Int>){
        _countryList.value=value
    }
    //initialize countries list from api
    init {
        viewModelScope.launch {
            getAllCountries()
        }
    }


    private var _departStateList = mutableStateOf(mapOf<String,Int>())
    val departStateList: MutableState<Map<String,Int>> = _departStateList
    fun updateDepartStateList(geoNameId: Int) {
        viewModelScope.launch {
            _departStateList.value=getAllStates(geoNameId)
        }

    }
    private var _destStateList = mutableStateOf(mapOf<String,Int>())
    val destStateList: MutableState<Map<String,Int>> = _destStateList
    fun updateDestStateList(geoNameId: Int) {
        viewModelScope.launch {
            _destStateList.value=getAllStates(geoNameId)
        }
    }

//    private var _countyList= mutableStateOf(mapOf<String,Int>())
//    val countyList:MutableState<Map<String,Int>> = _countyList
//    fun updateCountyList(value: Map<String,Int>){
//        _countyList.value=value
//    }

    private var _departCityList = mutableStateOf(mapOf<String,LatLng>())
    val departCityList: MutableState<Map<String,LatLng>> = _departCityList
    fun updateDepartCityList(geoNameId: Int) {
        viewModelScope.launch {
            _departCityList.value=getAllCities(geoNameId)
        }
    }
    fun clearDepartCityList(){
        _departCityList.value.toMutableMap().clear()
    }

    private var _destCityList = mutableStateOf(mapOf<String,LatLng>())
    val destCityList: MutableState<Map<String,LatLng>> = _destCityList
    fun updateDestCityList(geoNameId: Int) {
        viewModelScope.launch {
            _destCityList.value=getAllCities(geoNameId)
        }
    }
    fun clearDestCityList(){
        _destCityList.value.toMutableMap().clear()
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
        val planLikes = planGroup.value.likes
        updatePlanGroup(Plans(title, description, isChecked, planList, planLikes))
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
            try {
                val url =
                    URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${location.lat},${location.lng}&radius=$radius&key=$apiKey&type=tourist_attraction&language=${_language}")
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
            } catch (e: Exception) {
                Log.e("searchNearbyPlaces", "Exception caught: ${e.message}")
                // Handle the exception here
            }
        }
    }


    suspend fun getNearbyPlaces(
        location: Location,
        radius: Int = 5000000,
        apiKey: String
    ): List<Place> {
        return withContext(Dispatchers.IO) {
            try {
                val url =
                    URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${location.lat},${location.lng}&radius=$radius&key=$apiKey&type=tourist_attraction&language=${_language}")
                val json = url.readText(Charset.defaultCharset())
                val gson = Gson()
                val response = gson.fromJson(json, Response::class.java)
                response.results.map { result ->
                    Place(
                        name = result.name,
                        vicinity = result.vicinity,
                        location = Location(result.geometry.location.lat, result.geometry.location.lng),
                        rating = result.rating,
                        place_id = result.place_id,
                        photos = result.photos?.toList()
                    )
                }
            } catch (e: Exception) {
                // handle the exception here, e.g. log or display an error message
                emptyList()
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
            URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${location.lat},${location.lng}&radius=$radius&type=lodging&key=$apiKey&language=${_language}")
        try {
            val json = url.readText(Charset.defaultCharset())
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
//            println("mutableHotelSize: ${mutableHotelList.size}")

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
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList<Hotel>()
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
        try {
            val url = getURL(from, to, apiKey, waypoints, travelModeOption)
            val result = URL(url).readText()
            val jsonObject = JsonParser.parseString(result).asJsonObject
            val routes = jsonObject.getAsJsonArray("routes")
            val allRoutes = mutableListOf<List<LatLng>>()
            val legs = mutableListOf<LatLng>()
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
//                    println("No more travel mode matching")
                    return@withContext emptyList()
                }
                for (i in 0 until newRoutes.size()) {
                    val pointsLegs = newRoutes[i].asJsonObject
                        .getAsJsonArray("legs")
                    for (j in 0 until pointsLegs.size()){
                        val pointsSteps=pointsLegs[j].asJsonObject.getAsJsonArray("steps").flatMap {
                            decodePoly(it.asJsonObject.getAsJsonObject("polyline").get("points").asString)
                        }
                        legs.addAll(pointsSteps)
                    }

                    allRoutes.add(legs.toList())
                    legs.clear()
                }
//                println("$travelModeOption not exist, show $newTravelMode instead")
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        context,
                        "$travelModeOption not exist, show $newTravelMode instead",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                for (i in 0 until routes.size()) {
                    val pointsLegs = routes[i].asJsonObject
                        .getAsJsonArray("legs")
                    for (j in 0 until pointsLegs.size()){
                        val pointsSteps=pointsLegs[j].asJsonObject.getAsJsonArray("steps").flatMap {
                            decodePoly(it.asJsonObject.getAsJsonObject("polyline").get("points").asString)
                        }
                        legs.addAll(pointsSteps)
                    }

                    allRoutes.add(legs.toList())
                    legs.clear()
                }
//                println("$travelModeOption exist")
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        context,
                        "$travelModeOption exist",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            allRoutes
        } catch (e: Exception) {
            Log.e("getRoutes","Error occurred: ${e.message}")
            emptyList()
        }
    }

    private suspend fun getAllCountries() {
        withContext(Dispatchers.IO) {
            try {
                val url = "http://api.geonames.org/countryInfoJSON?username=journeyGenius"
                val result = URL(url).readText()
                val jsonObject = JsonParser.parseString(result).asJsonObject
                val geoNamesList = jsonObject.getAsJsonArray("geonames")
                val allCountries = mutableMapOf<String, Int>()
                if (!geoNamesList.isEmpty) {
                    for (i in geoNamesList) {
                        allCountries[i.asJsonObject["countryName"].asString] = i.asJsonObject["geonameId"].asInt
                    }
                }
                updateCountryList(allCountries)
            } catch (e: Exception) {
                // Handle the exception here, for example:
                Log.e("getAllCountries", "Error: ${e.message}")
            }
        }
    }

    suspend fun getAllStates(geoNameId: Int):Map<String,Int> = withContext(Dispatchers.IO){
        try{
            val url =
                "http://api.geonames.org/childrenJSON?geonameId=${geoNameId}&username=journeyGenius"
            val result = URL(url).readText()
            val jsonObject = JsonParser.parseString(result).asJsonObject
            val geoNamesList = jsonObject.getAsJsonArray("geonames")
            val allStates = mutableMapOf<String, Int>()
            if (!geoNamesList.isEmpty) {
                for (i in geoNamesList) {

                    allStates[i.asJsonObject["name"].asString.replace(Regex(" Shi"), "")] =
                        i.asJsonObject["geonameId"].asInt
                }
            }
            allStates
        }catch (e:Exception){
            Log.e("getAllStates", "Error: ${e.message}")
            emptyMap()
        }
    }

    // TODO: Clean Rubbish
    suspend fun getAllCities(geoNameId:Int):Map<String,LatLng> = withContext(Dispatchers.IO){

        val CAandUSiDList= listOf(5883102,5909050,6065171,6087430,6354959,6091069,6091530,6091732,6093943,6113358,6115047,6141242,6185811,
            4829764,5879092,5551752,4099753,5332921,5417618,4831725,4142224,4155751,4197000,5855797,5596512,4896861,4921868,4862182,4273857,6254925,
            4331987,4971068,4361885,6254926,5001836,5037779,4436296,4398678,5667009,5073708,5509151,5090174,5101760,5481136,5128638,4482348,5690763,5165418,
            4544379,5744337,6254927,5224323,4597040,5769223,4662168,4736286,5549030,5242283,6254928,5815135,4138106,4826850,5279468,5843591)
        try {
            if (geoNameId in CAandUSiDList) {  //US and Canada
                val geoIDList = mutableListOf<Int>()
                val url =
                    "http://api.geonames.org/childrenJSON?geonameId=${geoNameId}&username=journeyGenius"
                val result = URL(url).readText()
                val jsonObject = JsonParser.parseString(result).asJsonObject
                val geoNamesList = jsonObject.getAsJsonArray("geonames")
                if (!geoNamesList.isEmpty) {
                    for (i in geoNamesList) {
                        geoIDList.add(i.asJsonObject["geonameId"].asInt)
                    }
                }
                val allCities = mutableMapOf<String, LatLng>()
                for (i in geoIDList) {
                    val url =
                        "http://api.geonames.org/childrenJSON?geonameId=${i}&username=journeyGenius"
                    val result = URL(url).readText()
                    val jsonObject = JsonParser.parseString(result).asJsonObject
                    val geoNamesList = jsonObject.getAsJsonArray("geonames")
                    if (!geoNamesList.isEmpty) {
                        for (i in geoNamesList) {
//                            val temp=i.asJsonObject["name"].asString
//                            if(temp.contains("City of ")){
//                                allStates[temp.replace(Regex("City of "), "")]= i.asJsonObject["geonameId"].asInt
//                            }
                            allCities[i.asJsonObject["name"].asString] = LatLng(
                                i.asJsonObject["lat"].asDouble,
                                i.asJsonObject["lng"].asDouble
                            )
                        }
                    }
                }
                allCities
            } else {
                val url =
                    "http://api.geonames.org/childrenJSON?geonameId=${geoNameId}&username=journeyGenius"
                val result = URL(url).readText()
                val jsonObject = JsonParser.parseString(result).asJsonObject
                val geoNamesList = jsonObject.getAsJsonArray("geonames")
                val allCities = mutableMapOf<String, LatLng>()
                if (!geoNamesList.isEmpty) {
                    for (i in geoNamesList) {

                        allCities[i.asJsonObject["name"].asString.replace(Regex(" Shi"), "")] =
                            LatLng(i.asJsonObject["lat"].asDouble, i.asJsonObject["lng"].asDouble)
                    }
                }
                allCities
            }
        }catch (e:Exception){
            Log.e("getAllCities", "Error: ${e.message}")
            emptyMap()
        }


    }



    fun getPhotoUrl(photoReference: String, apiKey: String): String {
        return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=100&photoreference=$photoReference&key=$apiKey"
    }

    fun checkUserLikedPost(planId: String): Boolean {
        val list = _likedPlanList.value.toMutableList()
        return list.contains(planId)
    }
}