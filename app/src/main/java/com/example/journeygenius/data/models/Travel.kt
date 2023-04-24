package com.example.journeygenius.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.journeygenius.ui.util.Constants
import java.time.LocalDate

@Entity(tableName = Constants.TRAVEL_TABLE)
data class Travel(
    @PrimaryKey
    val travelId: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val type: TravelType,
    val budget: Double,
    val destination: String
)
