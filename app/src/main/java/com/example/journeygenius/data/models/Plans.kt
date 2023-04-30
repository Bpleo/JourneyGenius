package com.example.journeygenius.data.models

data class Plans(
    val title: String,
    val description: String,
    val plans: List<SinglePlan>
)
