package com.example.to_do.room_util

import androidx.room.*
import com.example.to_do.entities.Task

@Dao
interface TaskDAO {
    @Query("SELECT * FROM task ORDER BY priority")
    suspend fun loadAllTask(): List<Task?> // returns a list of task object

    @Insert
    fun insertTask(task: Task?)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateTask(task: Task?)

    @Delete
    fun deleteTask(task: Task?)
}