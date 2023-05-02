package com.example.journeygenius.components

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.journeygenius.data.models.Plan
import com.example.journeygenius.data.repositories.PlanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CardDetailViewModel : ViewModel() {
    private val _plan = mutableStateOf<Plan?>(null)
    val plan: State<Plan?> = _plan

    fun getPlanById(planId: String) {
        viewModelScope.launch {
            // TODO: call repository function getPlanById
            try {
                _plan.value = Plan(
                    title = "Test Plan #$planId",
                    userId = "0",
                    planId = planId,
                    travels = mutableListOf(),
                    likes =  100,
                    description = "This is a test for number $planId post.")
            } catch (e: Exception) {
                // Handle the exception
            }
        }
    }
}