package com.example.journeygenius.personalScreen

import androidx.lifecycle.ViewModel

class PersonalScreenViewModel: ViewModel() {
    var name: String = ""
    var email: String = ""
    init {
        name = "myname"
        email = "hello@bu.edu"
    }

}