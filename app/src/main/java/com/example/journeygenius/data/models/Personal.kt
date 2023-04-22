package com.example.journeygenius.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.journeygenius.ui.util.Constants.PERSONAL_TABLE

@Entity(tableName = PERSONAL_TABLE)
data class Personal (
    @PrimaryKey
    val id: String,
    val userName: String,
    val email: String,
    val pwd: String
    )