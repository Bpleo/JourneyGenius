package com.example.journeygenius.di

import android.content.Context
import androidx.room.Room
import com.example.journeygenius.data.PersonalDatabase
import com.example.journeygenius.ui.util.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        PersonalDatabase::class.java,
        DATABASE_NAME
    ).createFromAsset("database/personal.db").fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideDao(database: PersonalDatabase) = database.personalDao()

    @Singleton
    @Provides
    fun providePlanDao(database: PersonalDatabase) = database.planDao()

    @Singleton
    @Provides
    fun provideTravelDao(database: PersonalDatabase) = database.travelDao()
}