package com.example.journeygenius.data.models

data class Plans(
    val title: String,
    val description: String,
    var isPublic: Boolean,
    val plans: List<SinglePlan>,
    var likes: Int = 0,
    val userId: String
)
