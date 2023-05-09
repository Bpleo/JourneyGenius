package com.example.journeygenius.data.models

data class User(
    val id: String,
    val name: String,
    val email: String,
    val pwd: String,
    val Plan_List: List<Plans>,
    val likedPlanList: List<String>
)
