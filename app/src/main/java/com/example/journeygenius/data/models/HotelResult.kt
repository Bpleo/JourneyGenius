package com.example.journeygenius.data.models

/**
 * Data Class
 * Used to store the information of hotel result
 */
data class HotelResult(
    val name: String,
    val vicinity: String,
    val geometry: Geometry,
    val place_id: String,
    val price_level: Int,
    val rating: Double,
    val photos: Array<Photo>
)
