package com.example.journeygenius.data.models

import androidx.room.PrimaryKey
import java.time.LocalDate

data class Travel(
    @PrimaryKey
    val travelId: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val type: TravelType,
    val budget: Double,
    val destination: String
)
