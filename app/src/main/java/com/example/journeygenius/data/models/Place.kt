package com.example.journeygenius.data.models

/**
 * Data Class
 * Used to store the detail of Place
 */
data class Place(
    val name: String,
    val vicinity: String,
    val location: Location,
    val rating: Double,
    val place_id: String,
    val photos: List<Photo>?
)
