package com.example.journeygenius.data.models

/**
 * Data Class
 * Used to store the detail of a SinglePlan
 * Corresponding to the data structure on Firebase
 */
data class SinglePlan(
    val date: String,
    val destination: String,
    val attractions: List<Place>,
    val startAttraction: Place,
    val endAttraction:Place,
    var attractionRoutes:List<Place>,
    val priceLevel: Int,
    val priceLevelLabel: String,
    var hotel: List<Hotel>,
    val travelType: String
)
