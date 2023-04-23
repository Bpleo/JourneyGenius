package com.example.journeygenius.data

import androidx.room.*
import com.example.journeygenius.data.models.Personal
import com.example.journeygenius.data.models.Plan
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {
    @Query("SELECT * FROM plan_table ORDER BY planId ASC")
    fun getAllPlan(): Flow<List<Plan>>

    @Query("SELECT * FROM plan_table WHERE planId = :planId")
    fun getSelectedPlan(planId: String): Flow<Plan>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPlan(plan: Plan)

    @Update
    suspend fun updatePlan(plan: Plan)

    @Delete
    suspend fun deletePlan(plan: Plan)

    @Query("DELETE FROM plan_table")
    suspend fun deleteAllPlans()

    @Query("SELECT * FROM plan_table WHERE title LIKE :searchQuery OR description LIKE :searchQuery")
    fun searchPlan(searchQuery: String): Flow<List<Plan>>
}