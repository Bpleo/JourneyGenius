package com.example.journeygenius

import androidx.lifecycle.ViewModel
import com.example.journeygenius.data.repositories.PersonalRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    personalRepository: PersonalRepository
) : ViewModel() {
    val readAll = personalRepository.getAllProfile
}