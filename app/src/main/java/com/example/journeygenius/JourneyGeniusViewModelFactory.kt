package com.example.journeygenius

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore

class JourneyGeniusViewModelFactory(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val realtime: DatabaseReference
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JourneyGeniusViewModel::class.java)) {
            return JourneyGeniusViewModel(firestore, auth, realtime) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
