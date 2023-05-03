package com.example.journeygenius.data.models

data class SinglePlan(
    val date: String,
    val destination: String,
    val attractions: List<Place>,
    val startAttraction: Place,
    val endAttraction:Place,
    val attractionRoutes:List<Place>,
    val priceLevel: Int,
    val priceLevelLabel: String,
    var hotel: List<Hotel>,
    val travelType: String
)
