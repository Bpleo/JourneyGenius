package com.example.journeygenius

data class Plan(
    val title: String,
    val userId: String,
    val planId: String,
    var travels: MutableList<Travel>,
    var likes: Int,
    var description: String
)


