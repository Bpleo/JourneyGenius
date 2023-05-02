package com.example.journeygenius.community

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.journeygenius.data.models.Plan
import kotlinx.coroutines.launch

class CommunityViewModel : ViewModel(){

    private val _plans = mutableStateListOf<Plan>()
    val plans: List<Plan> = _plans

    init {
        fetchDataFromDatabase()
    }

    fun fetchDataFromDatabase() {
        viewModelScope.launch {
            _plans.clear()
            _plans.addAll(getData())
        }
    }

    fun testRefresh() {
        viewModelScope.launch {
            _plans.clear()
            _plans.addAll(getRefreshedData())
        }
    }

    private suspend fun getData(): List<Plan> {
        val newPlans = mutableListOf<Plan>()

        for (number in 1 until 11) {
            newPlans.add(
                element = Plan(
                    title = "Test Plan #$number",
                    userId = "0",
                    planId = "$number",
                    travels = mutableListOf(),
                    likes = number * 100,
                    description = "This is a test for number $number post."
                )
            )
        }
        for (number in 11 until 21) {
            newPlans.add(
                element = Plan(
                    title = "Test Plan for a loooooooooooooooooog post #$number",
                    userId = "0",
                    planId = "$number",
                    travels = mutableListOf(),
                    likes = number * 100,
                    description = "This is a test for number $number post, it is a really " +
                            "looooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                            "ooooooooooooooooooooooooooooooooooooooooooooooooooooog post."
                )
            )
        }

        return newPlans
    }

    private suspend fun getRefreshedData(): List<Plan> {
        val newPlans = mutableListOf<Plan>()

        for (number in 1 until 11) {
            newPlans.add(
                element = Plan(
                    title = "Refreshed Plan #$number",
                    userId = "0",
                    planId = "$number",
                    travels = mutableListOf(),
                    likes = number * 100,
                    description = "This is a test for Refreshed number $number post."
                )
            )
        }

        return newPlans
    }
}