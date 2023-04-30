package com.example.journeygenius.data.models

data class Result(
    val name: String,
    val vicinity: String,
    val geometry: Geometry,
    val rating: Double,
    val place_id: String,
    val photos: Array<Photo>
)
