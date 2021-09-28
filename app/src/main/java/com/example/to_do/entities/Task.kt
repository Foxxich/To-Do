package com.example.to_do.entities
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.time.ExperimentalTime

@Entity(tableName = "task")
class Task() : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "description")
    lateinit var description: String
    @ColumnInfo(name = "priority") var priority: Int = 0

    @ColumnInfo(name = "date")
    lateinit var updateAt: String
    @ColumnInfo(name = "time")
    lateinit var updateTime: String
    @ColumnInfo(name = "photo")
    var photoShow: Int = 1

    companion object {
        private val formatterTime: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        private val formatterDate: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }

    constructor (description: String, priority: Int, updatedAt: LocalDate, updateTime: LocalTime, photoShow : Int) : this() {
        this.description = description
        this.priority = priority
        this.photoShow = photoShow
        setUpdateTimeFromLocalDate(updatedAt)
        setUpdateTimeFromLocalTime(updateTime)
    }

    private fun setUpdateTimeFromLocalTime(updateTime: LocalTime) {
        this.updateTime = updateTime.format(formatterTime)
    }

    private fun setUpdateTimeFromLocalDate(updateDate: LocalDate) {
        this.updateAt = updateDate.format(formatterDate)
    }

    public fun getUpdatedAsLocalTime(): LocalTime {
        return LocalTime.parse(updateTime, formatterTime)
    }

    public fun getUpdateDateAsLocalDate(): LocalDate {
        return LocalDate.parse(updateAt, formatterDate)
    }
}
