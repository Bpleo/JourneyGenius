package com.example.journeygenius.plan

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class PlanListViewModel : ViewModel() {
    var items = mutableStateListOf("Plan 1", "Plan 2", "Plan 3")
}