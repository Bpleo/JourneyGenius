package com.example.journeygenius.data.models

data class Plans(
    val title: String,
    val description: String,
    val isPublic: Boolean,
    val plans: List<SinglePlan>,
    var likes: Int = 0
)
