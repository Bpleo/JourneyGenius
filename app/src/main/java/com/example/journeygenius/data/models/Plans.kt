package com.example.journeygenius.data.models

/**
 * Data Class
 * Used to store the detail of Plans, which contains a list of SinglePlan
 * Corresponding to the data structure on Firebase
 */
data class Plans(
    val title: String,
    val description: String,
    var isPublic: Boolean,
    val plans: List<SinglePlan>,
    var likes: Int = 0,
    val userId: String
)
