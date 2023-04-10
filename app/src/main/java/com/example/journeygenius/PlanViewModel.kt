package com.example.journeygenius

import android.util.Range
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import java.time.LocalDate

class PlanViewModel : ViewModel() {
    private var _dateRange = mutableStateOf(Range(LocalDate.now().minusDays(3), LocalDate.now()))
    val dateRange: MutableState<Range<LocalDate>> = _dateRange

    fun updateRange(start: LocalDate, end: LocalDate){
        _dateRange.value = Range(start, end)
    }

    private var _budget = mutableStateOf(TextFieldValue())
    val budget: MutableState<TextFieldValue> = _budget

    fun updateBudget(value: TextFieldValue){
        _budget.value = value
    }
}