package com.example.journeygenius

import android.util.Log
import android.util.Range
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

//@HiltViewModel
//class PersonalViewModel @Inject constructor(
//    private val personalRepository: PersonalRepository
//) : ViewModel() {
//    private val _allProfiles = MutableStateFlow<List<Personal>>(emptyList())
//    val allProfiles: StateFlow<List<Personal>> = _allProfiles
//
//    fun getAllProfiles() {
//        viewModelScope.launch {
//            personalRepository.getAllProfile.collect {
//                _allProfiles.value = it
//            }
//        }
//    }
//}
class JourneyGeniusViewModel(
    private val db: FirebaseFirestore,
    private val auth : FirebaseAuth
) : ViewModel() {

    fun signOut() {
        Firebase.auth.signOut()
        updateUserName(TextFieldValue(""))
        updateEmail(TextFieldValue(""))
        updatePwd("")
        updateVerifyPwd("")
    }

    fun signIn(){
        val user = auth.currentUser
        if (user != null) {
            Log.d("USER", user.email.toString())
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener {documentSnapshot ->
                    if (documentSnapshot != null) {
                        val data = documentSnapshot.data
                        if (data != null) {
                            updateUserName(TextFieldValue(data["name"].toString()))
                            updateEmail(TextFieldValue(data["email"].toString()))
                            updatePwd(data["password"].toString())
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

    fun updateUserName(userName : TextFieldValue) {
        _userName.value = userName
    }

    private var _email = mutableStateOf(TextFieldValue())
    val email: MutableState<TextFieldValue> = _email

    fun updateEmail(email : TextFieldValue) {
        _email.value = email
    }

    private var _pwd = mutableStateOf(String())
    val pwd : MutableState<String> = _pwd

    fun updatePwd(pwd : String) {
        _pwd.value = pwd
    }

    private var _verifyPwd = mutableStateOf(String())
    val verifyPwd : MutableState<String> = _verifyPwd

    fun updateVerifyPwd(pwd : String) {
        _verifyPwd.value = pwd
    }

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

    private var _departCountry= mutableStateOf("US");
    val departCountry:MutableState<String> = _departCountry
    fun updateDepartCountry(value: String){
        _departCountry.value=value;
    }

    private var _departState= mutableStateOf("MA")
    val departState:MutableState<String> =_departState
    fun updateDepartState(value:String){
        _departState.value=value;
    }

    private var _departCity= mutableStateOf("Boston")
    val departCity:MutableState<String> =_departCity
    fun updateDepartCity(value:String){
        _departCity.value=value;
    }

    private var _destCountry= mutableStateOf("");
    val destCountry:MutableState<String> = _destCountry
    fun updateDestCountry(value: String){
        _destCountry.value=value;
    }

    private var _destState= mutableStateOf("")
    val destState:MutableState<String> =_destState
    fun updateDestState(value:String){
        _destState.value=value;
    }

    private var _destCity= mutableStateOf("")
    val destCity:MutableState<String> =_destCity
    fun updateDestCity(value:String){
        _destCity.value=value;
    }

    private var _selectedCityLocation= mutableStateOf<Pair<Double, Double>>(Pair<Double,Double>(42.361145, -71.057083))
    val selectedCityLocation:MutableState<Pair<Double, Double>> = _selectedCityLocation
    fun updateSelectedCityLocation(value: Pair<Double, Double>?){
        if (value != null) {
            _selectedCityLocation.value=value
        }
    }
}