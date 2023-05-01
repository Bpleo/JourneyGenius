package com.example.journeygenius.data.models
data class Place(
    val name: String,
    val vicinity: String,
    val location: Location,
    val rating: Double,
    val place_id: String,
    val photos: Array<Photo>
)
