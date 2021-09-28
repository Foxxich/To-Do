package com.example.to_do

import android.content.Context
import android.util.Log
import com.example.to_do.entities.Task
import com.example.to_do.room_util.AppDataBase
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.util.*
import kotlin.Comparator

class TaskListController() : Serializable {

    private val mutex = Mutex()
    private var _list : MutableList<Task?> = listOf<Task?>().toMutableList()
    public val list
        get() = _list

    fun orderTasksPriority(): List<Task?> {
        list.sortedWith(Comparator { task1: Task?, task2: Task? -> task1!!.priority - task2!!.priority })
        return list
    }

//    fun orderTasksDate(): List<Task?> {
//        list.sortWith(Comparator {
//            task1: Task?, task2: Task? ->  (LocalDate.parse(task1!!.updateAt, DateTimeFormatter.ISO_DATE)).compareTo(LocalDate.parse(task2!!.updateAt, DateTimeFormatter.ISO_DATE))
//        })
//        return list
//    }
//
//    fun orderTasksTime(): List<Task?> {
//        val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH)
//        list.sortWith(Comparator {
//            task1: Task?, task2: Task? ->  (LocalTime.parse(task1!!.updateTime, formatter)).compareTo(LocalTime.parse(task2!!.updateTime, formatter))
//        })
//        return list
//    }

    fun orderTasksTimeDateDESC(): List<Task?> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.ENGLISH)
        list.sortWith(Comparator { task1: Task?, task2: Task? ->
            (LocalDateTime.parse(task1!!.updateAt + "T" + task1.updateTime, ISO_LOCAL_DATE_TIME)).compareTo(LocalDateTime.parse(task2!!.updateAt + "T" + task2.updateTime, ISO_LOCAL_DATE_TIME))
        })
        return list
    }

    fun orderTasksTimeDateASC(): List<Task?> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.ENGLISH)
        list.sortWith(Comparator { task1: Task?, task2: Task? ->
            (LocalDateTime.parse(task2!!.updateAt + "T" + task2.updateTime, ISO_LOCAL_DATE_TIME)).compareTo(LocalDateTime.parse(task1!!.updateAt + "T" + task1.updateTime, ISO_LOCAL_DATE_TIME))
        })
        return list
    }

    fun orderPhotosASC() : List<Task?> {
        list.sortWith(Comparator { task1: Task?, task2: Task? ->
            (task2!!.photoShow as Int).compareTo(task1!!.photoShow)
        })
        return list
    }

    fun orderPhotosDESC() : List<Task?> {
        list.sortWith(Comparator { task1: Task?, task2: Task? ->
            (task1!!.photoShow as Int).compareTo(task2!!.photoShow)
        })
        return list
    }

    fun addTask(task: Task, context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            AppDataBase.getsInstance(context)?.taskDao()?.insertTask(task)
        }
    }

    fun removeTask(task: Task, context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            AppDataBase.getsInstance(context)?.taskDao()?.deleteTask(task)
        }
    }

    fun updateTask(task: Task, context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            AppDataBase.getsInstance(context)?.taskDao()?.updateTask(task)
        }
    }

    suspend fun refreshTasks(context: Context) {
        Log.i("DB_CONNECTION_DEBUG", "refreshList async - start")

        val result = AppDataBase.getsInstance(context)!!.taskDao()!!.loadAllTask().toMutableList()
        Log.i("DB_CONNECTION_DEBUG", "obtained new list")
        mutex.withLock {
            _list.clear()
            _list.addAll(result)
        }
        Log.i("DB_CONNECTION_DEBUG", "new list set")

        Log.i("DB_CONNECTION_DEBUG", "refreshList async - end")
    }
}