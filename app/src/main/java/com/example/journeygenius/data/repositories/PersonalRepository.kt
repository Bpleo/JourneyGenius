package com.example.journeygenius.data.repositories

import com.example.journeygenius.data.PersonalDao
import com.example.journeygenius.data.models.Personal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PersonalRepository @Inject constructor(
    private val persinalDao: PersonalDao
){
    val getAllTasks: Flow<List<Personal>> = persinalDao.getAllProfile()
}