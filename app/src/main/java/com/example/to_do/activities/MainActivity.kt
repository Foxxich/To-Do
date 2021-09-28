package com.example.to_do.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do.*
import com.example.to_do.entities.Task
import com.example.to_do.recycler_view_util.TaskListener
import com.example.to_do.recycler_view_util.ToDoListAdapter
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(),
    TaskListener {

    private val taskListController = TaskListController()
    private lateinit var toDoListAdapter : ToDoListAdapter
    private val sharedPrefFile = "kotlinsharedpreference"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRecyclerViewAdapter()
        prepareRecyclerView()
    }

    private fun initRecyclerViewAdapter() {
        toDoListAdapter = ToDoListAdapter(
            taskListController.list,
            this
        )
    }

    private fun prepareRecyclerView() {
        val recyclerView : RecyclerView = findViewById(R.id.recycler_view_main)
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = toDoListAdapter
    }

    private fun updateRecyclerViewAdapter() {
        Log.i("UI_INFO", "updateRecyclerViewAdapter called")
        GlobalScope.launch(Dispatchers.IO) {
            taskListController.refreshTasks(this@MainActivity)
            Log.i("UI_INFO", "list refreshed")

            launch(Dispatchers.Main) {
                Log.i("UI_INFO", "notifyDataSetChanged called")
                toDoListAdapter.notifyDataSetChanged()
            }
            Log.i("UI_INFO", "updateRecyclerViewAdapter async - end")
        }
    }

    override fun onResume() {
        super.onResume()
        updateRecyclerViewAdapter()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.i("UI_INFO", "Selected Item: " + item.title)
        return when (item.itemId) {
            R.id.priority_item ->                // do your code
            {
                taskListController.orderTasksPriority()
                toDoListAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Priority list changed ", Toast.LENGTH_SHORT).show()
                updateRecyclerViewAdapter()
                true
            }
            R.id.date_item_asc -> {
                taskListController.orderTasksTimeDateASC()
                toDoListAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Time list changed ASC", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.date_item_desc -> {
                taskListController.orderTasksTimeDateDESC()
                toDoListAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Time list changed DESC", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.photo_item_asc -> {
                taskListController.orderPhotosASC()
                toDoListAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Time list changed ASC", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.photo_item_desc -> {
                taskListController.orderPhotosDESC()
                toDoListAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Time list changed DESC", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.refresh_button -> {
                Log.i("UI_INFO", "Refreshing list...")
                updateRecyclerViewAdapter()
                Toast.makeText(this, "Refreshing list...", Toast.LENGTH_SHORT)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            1 -> {
                when (resultCode) {
                    RESULT_CANCELED -> Toast.makeText(this, "Adding canceled", Toast.LENGTH_SHORT)
                        .show()
                    RESULT_OK -> {
                        val task = data!!.getSerializableExtra("new_task") as Task
                        addTaskToDatabase(task)
                        val returnMessage = data.getStringExtra("return_message")
                        Toast.makeText(this, returnMessage.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            2 -> {
                when (resultCode) {
                    RESULT_CANCELED -> Toast.makeText(this, "Editing canceled", Toast.LENGTH_SHORT)
                        .show()
                    RESULT_OK -> {
                        if (data!!.hasExtra("task_to_edit")) {
                            val task = data.getSerializableExtra("task_to_edit") as Task
                            updateTaskToDatabase(task)
                        } else if (data.hasExtra("task_to_delete")) {
                            val task = data.getSerializableExtra("task_to_delete") as Task
                            deleteTaskToDatabase(task)
                        }
                        val returnMessage = data.getStringExtra("return_message")
                        Toast.makeText(this, returnMessage.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else -> {
                Toast.makeText(this, "Return to Menu", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun addTaskToDatabase(task: Task) {
        taskListController.addTask(task, this)
        updateRecyclerViewAdapter()
    }

    private fun updateTaskToDatabase(task: Task) {
        taskListController.updateTask(task, this)
        updateRecyclerViewAdapter()
    }

    private fun deleteTaskToDatabase(task: Task) {
        taskListController.removeTask(task, this)
        updateRecyclerViewAdapter()
    }

    fun addToListListener(view: View) {
        val intent = Intent(this, AddToListActivity::class.java).apply {
            putExtra("startMessage", "Provide date")
        }
        startActivityForResult(intent, 1)
    }

    override fun clicked(position: Int) {
        val intent = Intent(this, AddToListActivity::class.java).apply {
            putExtra("taskToEdit", taskListController.list[position])
        }
        startActivityForResult(intent, 2)
    }
}