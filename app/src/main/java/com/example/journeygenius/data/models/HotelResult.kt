package com.example.journeygenius.data.models

data class HotelResult(
    val name: String,
    val vicinity: String,
    val geometry: Geometry,
    val place_id: String,
    val price_level: Int,
    val rating: Double,
    val photos: Array<Photo>
)
