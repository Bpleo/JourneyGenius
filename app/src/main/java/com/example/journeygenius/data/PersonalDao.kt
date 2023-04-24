package com.example.journeygenius.data

import androidx.room.*
import com.example.journeygenius.data.models.Personal
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalDao {
    @Query("SELECT * FROM users ORDER BY id ASC")
    fun getAllProfile(): Flow<List<Personal>>

    @Query("SELECT * FROM users WHERE id = :personalId")
    fun getSelectedProfile(personalId: String): Flow<Personal>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addProfile(personal: Personal)

    @Update
    suspend fun updateProfile(personal: Personal)

    @Delete
    suspend fun deleteProfile(personal: Personal)

    @Query("DELETE FROM users")
    suspend fun deleteAllProfiles()

    @Query("SELECT * FROM users WHERE userName LIKE :searchQuery OR email LIKE :searchQuery")
    fun searchProfile(searchQuery: String): Flow<List<Personal>>
}