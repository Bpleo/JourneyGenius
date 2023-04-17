package com.example.journeygenius.personal

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.journeygenius.data.models.Personal
import com.example.journeygenius.data.repositories.PersonalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
class PersonalViewModel : ViewModel() {
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
}