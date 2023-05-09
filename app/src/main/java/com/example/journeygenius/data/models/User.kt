package com.example.journeygenius.data.models

/**
 * Data Class
 * Used to store the detail of a user
 * Corresponding to the data structure on Firebase
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val pwd: String,
    val Plan_List: Map<String, Plans>,
    val likedPlanList: List<String>
)
