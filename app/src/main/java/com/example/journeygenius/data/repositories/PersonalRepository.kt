package com.example.journeygenius.data.repositories

import com.example.journeygenius.data.PersonalDao
import com.example.journeygenius.data.models.Personal
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
@ViewModelScoped
class PersonalRepository @Inject constructor(
    private val personalDao: PersonalDao
){
    val getAllProfile: Flow<List<Personal>> = personalDao.getAllProfile()

    fun getSelectedProfile(id: Int): Flow<Personal> {
        return personalDao.getSelectedProfile(id)
    }

    suspend fun addProfile(personal: Personal) {
        personalDao.addProfile(personal)
    }

    suspend fun updateProfile(personal: Personal) {
        personalDao.updateProfile(personal)
    }

    suspend fun deleteProfile(personal: Personal) {
        personalDao.deleteProfile(personal)
    }

    suspend fun deleteAllProfiles() {
        personalDao.deleteAllProfiles()
    }

    fun searchProfile(searchQuery: String): Flow<List<Personal>> {
        return personalDao.searchProfile(searchQuery = searchQuery)
    }
}