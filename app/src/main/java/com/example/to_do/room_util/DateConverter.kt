package com.example.to_do.room_util

import androidx.room.TypeConverter
import java.sql.Date

class DateConverter {
    @TypeConverter
    fun toDate(timeStamp: Long?): Date? {
        return timeStamp?.let { Date(it) }
    }

    @TypeConverter
    fun toTimeStamp(date: Date?): Long? {
        return date?.time
    }
}