package com.example.journeygenius.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.journeygenius.data.models.Personal
import com.example.journeygenius.data.models.Plan
import com.example.journeygenius.data.models.Travel

@Database(entities = [Personal::class, Plan::class, Travel::class], version = 1, exportSchema = false)
abstract class PersonalDatabase: RoomDatabase() {
    abstract fun personalDao(): PersonalDao
    abstract fun planDao(): PlanDao
    abstract fun travelDao(): TravelDao
}