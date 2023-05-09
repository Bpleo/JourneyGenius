package com.example.journeygenius.data.models

/**
 * Data Class
 * Used to store photo attributes
 * photo_reference is the key to get url from api
 */
data class Photo(
    val height: Int,
    val html_attributions: List<String>,
    val photo_reference: String,
    val width: Int
    )
