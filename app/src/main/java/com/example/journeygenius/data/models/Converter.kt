package com.example.journeygenius.data.models

import androidx.room.TypeConverter
import com.google.gson.Gson
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.atStartOfDay(ZoneOffset.UTC)?.toEpochSecond()?.div(86400)
    }

    @TypeConverter
    fun fromTravelList(travelList: MutableList<Travel>?): String? {
        if (travelList == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<MutableList<Travel>>() {}.type
        return gson.toJson(travelList, type)
    }

    @TypeConverter
    fun toTravelList(travelListString: String?): MutableList<Travel>? {
        if (travelListString == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<MutableList<Travel>>() {}.type
        return gson.fromJson(travelListString, type)
    }
}