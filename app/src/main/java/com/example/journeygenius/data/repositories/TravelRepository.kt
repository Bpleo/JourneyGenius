package com.example.journeygenius.data.repositories

import com.example.journeygenius.data.TravelDao
import com.example.journeygenius.data.models.Travel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
@ViewModelScoped
class TravelRepository @Inject constructor(
    private val travelDao: TravelDao
){
    val getAllTravel: Flow<List<Travel>> = travelDao.getAllTravel()

    fun getSelectedTravel(travelId: String): Flow<Travel> {
        return travelDao.getSelectedTravel(travelId)
    }

    suspend fun addTravel(travel: Travel) {
        travelDao.addTravel(travel)
    }

    suspend fun updateTravel(travel: Travel) {
        travelDao.updateTravel(travel)
    }

    suspend fun deleteTravel(travel: Travel) {
        travelDao.deleteTravel(travel)
    }

    suspend fun deleteAllTravels() {
        travelDao.deleteAllTravels()
    }

    fun searchTravel(searchQuery: String): Flow<List<Travel>> {
        return travelDao.searchTravel(searchQuery = searchQuery)
    }
}