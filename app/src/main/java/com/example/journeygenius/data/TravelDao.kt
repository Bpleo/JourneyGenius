package com.example.journeygenius.data

import androidx.room.*
import com.example.journeygenius.data.models.Personal
import com.example.journeygenius.data.models.Plan
import com.example.journeygenius.data.models.Travel
import kotlinx.coroutines.flow.Flow

@Dao
interface TravelDao {
    @Query("SELECT * FROM travel_table ORDER BY travelId ASC")
    fun getAllTravel(): Flow<List<Travel>>

    @Query("SELECT * FROM travel_table WHERE travelId = :travelId")
    fun getSelectedTravel(travelId: String): Flow<Travel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTravel(travel: Travel)

    @Update
    suspend fun updateTravel(travel: Travel)

    @Delete
    suspend fun deleteTravel(travel: Travel)

    @Query("DELETE FROM travel_table")
    suspend fun deleteAllTravels()

    @Query("SELECT * FROM travel_table WHERE destination LIKE :searchQuery")
    fun searchTravel(searchQuery: String): Flow<List<Travel>>
}