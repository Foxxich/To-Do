package com.example.to_do.recycler_view_util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do.R
import com.example.to_do.entities.Task
import java.time.format.DateTimeFormatter

class ToDoListAdapter(private var listTasks: List<Task?>, private val listener: TaskListener) : RecyclerView.Adapter<ToDoListAdapter.TaskViewHolder>() {

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        // Determine the values of the wanted data
        val taskEntry : Task = listTasks[position]!!
        val description = taskEntry.description
        val priority = taskEntry.priority
        holder.taskDescriptionView.text = description //ustwaimy konkrenta wartowsc do wyswietlenia w elemencie
        val priorityString = "" + priority // converts int to String
        val photoId = taskEntry.photoShow

        holder.priorityView.text = priorityString
        getPriorityColor(holder, priority)

        setUpdateTime(holder, taskEntry)
        getPhotoType(holder,photoId)
    }

    private fun setUpdateTime(holder: TaskViewHolder, task: Task) {
        holder.updatedAtView.text = "${task.updateAt} - ${task.updateTime}"
    }

    private fun getPriorityColor(holder: TaskViewHolder, priority: Int) {
        when (priority) {
            1 -> {
                holder.priorityView.setBackgroundResource(R.drawable.green)
            }
            2 -> {
                holder.priorityView.setBackgroundResource(R.drawable.yellow)
            }
            3 -> {
                holder.priorityView.setBackgroundResource(R.drawable.red)
            }
            else -> {
                holder.priorityView.setBackgroundResource(R.drawable.white)
            }
        }
    }

    private fun getPhotoType(holder: TaskViewHolder, photoId: Int) {
        when (photoId) {
            1 -> {
                holder.photoView.setBackgroundResource(R.drawable.a_photo)
            }
            2 -> {
                holder.photoView.setBackgroundResource(R.drawable.b_photo)
            }
            3 -> {
                holder.photoView.setBackgroundResource(R.drawable.c_photo)
            }
            else -> {
                holder.photoView.setBackgroundResource(R.drawable.white)
            }
        }
    }

    override fun getItemCount(): Int {
        return listTasks.size
    }

    // Inner class for creating ViewHolders
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Class variables for the task description and priority TextViews
        var taskDescriptionView: TextView = itemView.findViewById(R.id.taskDescription)
        val updatedAtView: TextView = itemView.findViewById(R.id.taskUpdatedAt)
        var priorityView: TextView = itemView.findViewById(R.id.priorityTextView)
        var photoView : ImageView = itemView.findViewById(R.id.imageViewUser)

        init {
            itemView.findViewById<LinearLayout>(R.id.itemBackground).setOnClickListener {
                listener.clicked(layoutPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflate = LayoutInflater.from(parent.context).inflate(R.layout.task_list, parent, false)
        return TaskViewHolder(inflate)
    }

}