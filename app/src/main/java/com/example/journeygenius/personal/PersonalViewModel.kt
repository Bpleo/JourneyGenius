package com.example.journeygenius.personal

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel

class PersonalViewModel : ViewModel() {
    private val _email = mutableStateOf(TextFieldValue())
    val email: MutableState<TextFieldValue> = _email

    fun updateEmail(email : TextFieldValue) {
        _email.value = email
    }

    private val _pwd = mutableStateOf(String())
    val pwd : MutableState<String> = _pwd

    fun updatePwd(pwd : String) {
        _pwd.value = pwd
    }
}