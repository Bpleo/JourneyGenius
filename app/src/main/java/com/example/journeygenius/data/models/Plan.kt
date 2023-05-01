package com.example.journeygenius.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.journeygenius.ui.util.Constants.PLAN_TABLE

/* Attention: To be Deleted */
@Entity(tableName = PLAN_TABLE)
data class Plan(
    val title: String,
    val userId: String,
    @PrimaryKey
    val planId: String,
    var travels: MutableList<Travel>,
    var likes: Int,
    var description: String
)


