package com.example.journeygenius.community

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.journeygenius.Plan

class CommunityViewModel : ViewModel(){

    private var _plans = mutableStateListOf<Plan>()
    val plans: List<Plan> = _plans

    init {
        getData()
    }

    private fun getData() {
        for (number in 1 until 11) {
            _plans.add(
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
            _plans.add(
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
    }
}