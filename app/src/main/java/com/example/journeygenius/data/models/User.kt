package com.example.journeygenius.data.models

data class User(
    val id: String,
    val name: String,
    val email: String,
    val pwd: String,
    val planGroupList: List<Plans>,
    val likedPlanList: List<String>
)
