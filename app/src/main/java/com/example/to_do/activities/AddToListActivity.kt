package com.example.to_do.activities

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.example.to_do.R
import com.example.to_do.entities.Task
import com.example.to_do.databinding.ActivityAddToListBinding
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.time.ExperimentalTime


class AddToListActivity : AppCompatActivity() {

    private lateinit var map: Map<Int, Int>
    private lateinit var mapPhotos : Map<Int,Int>
    private lateinit var binding: ActivityAddToListBinding
    private lateinit var day : Calendar
    private lateinit var chosenDate : LocalDate
    private lateinit var taskToEdit: Task
    private lateinit var chosenTime : LocalTime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_to_list)
        binding = ActivityAddToListBinding.inflate(layoutInflater)
        initPriorityMap()
        initPhotoMap()
        if (intent.hasExtra("taskToEdit")) {
            taskToEdit = intent.getSerializableExtra("taskToEdit") as Task
            prepareEditActivity()
        } else {
            prepareAddActivity()
        }
    }

    private fun prepareAddActivity() {
        val button = findViewById<Button>(R.id.saveButton)
        button.text = "Add"
        button.setOnClickListener(this::addToList) //namespace accessory
        initDeleteButton(false)
        val dateToSet = LocalDate.now()
        initDatePicker(dateToSet)
        val timeToSet = LocalTime.now()
        initTimePicker(timeToSet)
    }

    private fun prepareEditActivity() {
        val button = findViewById<Button>(R.id.saveButton)
        button.text = "Edit"
        button.setOnClickListener(this::editTask) //namespace accessory
        initDeleteButton(true)
        day = Calendar.getInstance()
        val dateToSet = taskToEdit.getUpdateDateAsLocalDate()
        initDatePicker(dateToSet)
        val timeToSet = taskToEdit.getUpdatedAsLocalTime()
        initTimePicker(timeToSet)
        initDescription(taskToEdit.description)
        initPriorityButtons(taskToEdit.priority)
        initPhotoButtons(taskToEdit.photoShow)
    }

    private fun initPriorityButtons(priority: Int) {
        for (mapEntry in map) {
            if (mapEntry.value == priority) {
                val radioButtonID = mapEntry.key
                val radioButton = findViewById<RadioButton>(radioButtonID)
                radioButton.isChecked = true
                return
            }
        }
    }

    private fun initPhotoButtons(photo: Int) {
        for (mapEntryPhoto in mapPhotos) {
            if (mapEntryPhoto.value == photo) {
                val radioButtonID = mapEntryPhoto.key
                val radioButton = findViewById<RadioButton>(radioButtonID)
                radioButton.isChecked = true
                return
            }
        }
    }

    private fun initDescription(description: String) {
        findViewById<EditText>(R.id.editTextTaskDescription).setText(description)
    }

    private fun initDeleteButton(visible : Boolean) {
        val button : Button = findViewById(R.id.deleteButton)
        button.isVisible = visible
    }

    private fun initDatePicker(dateToSet: LocalDate) {
        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        datePicker.init(dateToSet.year, dateToSet.monthValue,
                dateToSet.dayOfMonth
        ) {
            view, year, month, day ->
            chosenDate = LocalDate.of(year, month, day)
        }
        chosenDate = dateToSet
    }

    private fun initTimePicker(timeToSet : LocalTime) {
        val textView = findViewById<TextView>(R.id.textTime)
        textView.setOnClickListener {
            val picker : TimePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                chosenTime = LocalTime.of(hourOfDay, minute)

                textView.text = (chosenTime.format(DateTimeFormatter.ofPattern("HH:mm")))
                textView.invalidate()
            }, timeToSet.hour, timeToSet.minute, true)
            picker.setTitle("Select Time")
            picker.show()
        }
        chosenTime = timeToSet
        textView.text = chosenTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        textView.invalidate()
    }

    private fun initPriorityMap() {
        map = mapOf(Pair(binding.radButtonLow.id,3),Pair(binding.radButtonHigh.id,1),Pair(binding.radButtonMedium.id,2))
    }

    private fun initPhotoMap() {
        mapPhotos = mapOf(Pair(binding.radButtonA.id,1),Pair(binding.radButtonB.id,2),Pair(binding.radButtonC.id,3))
    }

    fun addToList(view: View) {
        if(isPriorityNotChosen()) {
            Toast.makeText(this, "Choose priority!", Toast.LENGTH_SHORT).show()
            return
        } else if(isPhotoNotChosen()) {
            Toast.makeText(this, "Choose photo!", Toast.LENGTH_SHORT).show()
            return
        }
        val task = obtainNewTask()
        val intent = Intent()
        intent.putExtra("return_message","Added to DB")
        intent.putExtra("new_task", task)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun isPriorityNotChosen(): Boolean {
        return findViewById<RadioGroup>(R.id.radioGroup).checkedRadioButtonId == -1
    }

    private fun isPhotoNotChosen(): Boolean {
        return findViewById<RadioGroup>(R.id.radioGroupPhoto).checkedRadioButtonId == -1
    }

    fun deleteTask(view: View) {
        val intent = Intent()
        intent.putExtra("return_message","Deleted from DB")
        intent.putExtra("task_to_delete", taskToEdit)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    fun editTask(view: View) {
        val task = obtainEditedTask()
        val intent = Intent()
        intent.putExtra("return_message","Edited in DB")
        intent.putExtra("task_to_edit", task)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun obtainNewTask(): Task {
        val text: String = findViewById<EditText>(R.id.editTextTaskDescription).text.toString()
        val checkedRadioButtonId = findViewById<RadioGroup>(R.id.radioGroup).checkedRadioButtonId
        val priority = map[checkedRadioButtonId]!!
        val checkedRadioButtonPhotoId = findViewById<RadioGroup>(R.id.radioGroupPhoto).checkedRadioButtonId
        val photoId = mapPhotos[checkedRadioButtonPhotoId]!!
        return Task(text, priority, chosenDate, chosenTime,photoId)

    }

    private fun obtainEditedTask(): Task {
        val task = obtainNewTask()
        task.id = taskToEdit.id
        return task
    }
}