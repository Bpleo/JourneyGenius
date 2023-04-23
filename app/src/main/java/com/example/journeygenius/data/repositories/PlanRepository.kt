package com.example.journeygenius.data.repositories

import com.example.journeygenius.data.PersonalDao
import com.example.journeygenius.data.PlanDao
import com.example.journeygenius.data.models.Personal
import com.example.journeygenius.data.models.Plan
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
@ViewModelScoped
class PlanRepository @Inject constructor(
    private val planDao: PlanDao
){
    val getAllPlan: Flow<List<Plan>> = planDao.getAllPlan()

    fun getSelectedPlan(planId: String): Flow<Plan> {
        return planDao.getSelectedPlan(planId)
    }

    suspend fun addPlan(plan: Plan) {
        planDao.addPlan(plan)
    }

    suspend fun updatePlan(plan: Plan) {
        planDao.updatePlan(plan)
    }

    suspend fun deletePlan(plan: Plan) {
        planDao.deletePlan(plan)
    }

    suspend fun deleteAllPlans() {
        planDao.deleteAllPlans()
    }

    fun searchPlan(searchQuery: String): Flow<List<Plan>> {
        return planDao.searchPlan(searchQuery = searchQuery)
    }
}