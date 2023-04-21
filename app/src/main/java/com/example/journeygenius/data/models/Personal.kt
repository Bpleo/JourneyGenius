package com.example.journeygenius.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.journeygenius.ui.util.Constants.DATABASE_TABLE

@Entity(tableName = DATABASE_TABLE)
data class Personal (
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val password: String
    )