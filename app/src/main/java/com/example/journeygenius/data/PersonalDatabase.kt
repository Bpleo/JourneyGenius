package com.example.journeygenius.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.journeygenius.data.models.Personal

@Database(entities = [Personal::class], version = 1, exportSchema = false)
abstract class PersonalDatabase: RoomDatabase() {
    abstract fun personalDao(): PersonalDao
}