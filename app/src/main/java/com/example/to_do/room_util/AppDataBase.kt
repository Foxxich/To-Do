package com.example.to_do.room_util

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room

import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.to_do.entities.Task

@Database(entities = arrayOf(Task::class),version = 1,exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun taskDao(): TaskDAO?

    companion object {
        val LOG_TAG = AppDataBase::class.java.simpleName
        val LOCK = Any()
        const val DATABASE_NAME = "todo-list.db"
        private var sInstance: AppDataBase? = null
        fun getsInstance(context: Context): AppDataBase? {
            if (sInstance == null) {
                synchronized(LOCK) {
                    Log.d(LOG_TAG, "creating new database")
                    sInstance = Room.databaseBuilder(context.applicationContext,
                            AppDataBase::class.java,
                        DATABASE_NAME
                    )
                            .build()
                }
            }
            Log.d(LOG_TAG, "getting the database instance")
            return sInstance
        }
    }
}