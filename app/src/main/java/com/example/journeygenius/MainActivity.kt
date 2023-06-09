package com.example.journeygenius

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.journeygenius.ui.theme.JourneyGeniusTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import com.example.journeygenius.components.RequestPermission
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Main Activity
 */
class MainActivity : ComponentActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var realtime : DatabaseReference
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isPhone = resources.configuration.smallestScreenWidthDp < 600
        if (isPhone) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        auth = Firebase.auth
        db = Firebase.firestore
        realtime = Firebase.database.reference
        setContent {
//            RequestPermission(permission =ACCESS_FINE_LOCATION,)
            JourneyGeniusTheme {
                Scaffold{
                    Intro(auth, this, db, realtime)
                }
            }
        }
    }
}