package com.example.journeygenius

import java.time.LocalDate

data class Travel(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val type: TravelType,
    val budget: Double,
    val destination: String
)

enum class TravelType {
    SINGLE,
    FAMILY,
    BUSINESS
}

