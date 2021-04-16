package com.example.reminderapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        // Data base instance and user id of current user
        val db = FirebaseFirestore.getInstance()
        val userId = auth.currentUser.uid

        // Variables for the radio group for interval selection.
        val radioGroup = findViewById<RadioGroup>(R.id.addTaskRadioGroup)
        var radioButton : RadioButton

        // Text Field variables.
        val taskName = findViewById<EditText>(R.id.addTaskName)
        val taskDescription = findViewById<EditText>(R.id.addTaskDescription)
        val taskDate = findViewById<EditText>(R.id.addTaskDate)
        val taskTime = findViewById<EditText>(R.id.addTaskTime)

        // Current date - updated in addTaskButton onClickListener using user input.
        var deadline : Date = Date()
        // Formats a date from the user's input.
        val dateFormatter = SimpleDateFormat("MM/dd/yyyy HH:mm")

        // Button variable
        val addTaskButton = findViewById<Button>(R.id.addTaskButton)

        addTaskButton.setOnClickListener {
            // Get the repetition from radio button
            var radioId = radioGroup.checkedRadioButtonId
            radioButton = findViewById(radioId)
            var selectedInterval = radioButton.text.toString()
            // Time
            val date_var = taskDate.text.toString()
            val time_var = taskTime.text.toString()
            val dateTime = date_var + " " + time_var
            deadline = dateFormatter.parse(dateTime)

            var creationTime = dateFormatter.format(Date()).toString()

            // Formatters to get day/month/year from date object.
            var getDay = SimpleDateFormat("dd")
            var getMonth = SimpleDateFormat("MM")
            var getYear = SimpleDateFormat("yyyy")
            var getHour = SimpleDateFormat("HH")
            var getMinute = SimpleDateFormat("mm")
            var taskDay = getDay.format(deadline).toInt()
            var taskMonth = getMonth.format(deadline).toInt()
            var taskYear = getYear.format(deadline).toInt()
            var taskHour = getHour.format(deadline).toInt()
            var taskMinute = getMinute.format(deadline).toInt()

            var deadlineCalendar = Calendar.getInstance()
            // Offset month by 1 because January is month 0.
            deadlineCalendar.set(taskYear, taskMonth, taskDay, taskHour, taskMinute, 0)

            // First instance of the task
            val task : MutableMap<String, Any?> = HashMap()
            task["name"] = taskName.text.toString()
            task["description"] = taskDescription.text.toString()
            task["deadline"] = dateTime.toString()
            task["created"] = creationTime.toString()
            task["interval"] = radioButton.text.toString()

            // Add the initial task to the database:
            // Each user has a unique collection, each task will be an entry in that collection.
            db.collection(userId).add(task)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Task Added", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Problem Adding Task", Toast.LENGTH_SHORT).show()
                        }
                    }

            // Note: if the user selects "Never" then the task only needs to be added the first time.
            // Generate next tasks, number of occurences based on interval chosen.
            var numOccurences : Int = 0
            if (selectedInterval.equals("Daily")) {
                // One Month of tasks
//                numOccurences = 30
                numOccurences = 5
                for (i in 0..numOccurences) {
                    // Add one day each time
                    deadlineCalendar.add(Calendar.HOUR, 24)
                    Log.w("***Deadline Day", deadlineCalendar.get(Calendar.MONTH).toString())
                    // Convert it to a string to format with time.
                    var nextTaskDate = deadlineCalendar.time
                    var nextTask = dateFormatter.format(nextTaskDate)

                    //Need to style the minutes, so that they look normal. i.e. 17:00 doesn't look like 17:0 or 17:06 isn't 17:6
                    var minuteStr: String
                    var minute = deadlineCalendar.get(Calendar.MINUTE)
                    if(minute == 0)
                        minuteStr = "00"
                    else if(minute < 10)
                        minuteStr = "0$minute"
                    else
                        minuteStr = "$minute"

                    task["deadline"] = "${deadlineCalendar.get(Calendar.MONTH)}/${deadlineCalendar.get(Calendar.DAY_OF_MONTH)}/${deadlineCalendar.get(Calendar.YEAR)} ${deadlineCalendar.get(Calendar.HOUR_OF_DAY)}:$minuteStr"

                    db.collection(userId).add(task)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(this, "Task Added", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Problem Adding Task", Toast.LENGTH_SHORT).show()
                                }
                            }
                }
            } else if (selectedInterval.equals("Weekly")) {
                // About 3 months of tasks
//                numOccurences = 13
                numOccurences = 5
                for (i in 0..numOccurences) {
                    // Add one week each time
                    deadlineCalendar.add(Calendar.WEEK_OF_YEAR, 1)
                    // Convert it to a string to format with time.
                    var nextTaskDate = deadlineCalendar.time
                    var nextTask = dateFormatter.format(nextTaskDate)

                    //Need to style the minutes, so that they look normal. i.e. 17:00 doesn't look like 17:0 or 17:06 isn't 17:6
                    var minuteStr: String
                    var minute = deadlineCalendar.get(Calendar.MINUTE)
                    if(minute == 0)
                        minuteStr = "00"
                    else if(minute < 10)
                        minuteStr = "0$minute"
                    else
                        minuteStr = "$minute"

                    task["deadline"] = "${deadlineCalendar.get(Calendar.MONTH)}/${deadlineCalendar.get(Calendar.DAY_OF_MONTH)}/${deadlineCalendar.get(Calendar.YEAR)} ${deadlineCalendar.get(Calendar.HOUR_OF_DAY)}:$minuteStr"

                    db.collection(userId).add(task)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(this, "Task Added", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Problem Adding Task", Toast.LENGTH_SHORT).show()
                                }
                            }
                }
            } else if (selectedInterval.equals("Monthly")) {
                // One year of tasks
//                    numOccurences = 12
                numOccurences = 5
                for (i in 0..numOccurences) {
                    // Add one month each time
                    deadlineCalendar.add(java.util.Calendar.MONTH, 1)
                    // Convert it to a string to format with time.
                    var nextTaskDate = deadlineCalendar.time
                    var nextTask = dateFormatter.format(nextTaskDate)

                    task["deadline"] = nextTask.toString()

                    //Need to style the minutes, so that they look normal. i.e. 17:00 doesn't look like 17:0 or 17:06 isn't 17:6
                    var minuteStr: String
                    var minute = deadlineCalendar.get(Calendar.MINUTE)
                    if(minute == 0)
                        minuteStr = "00"
                    else if(minute < 10)
                        minuteStr = "0$minute"
                    else
                        minuteStr = "$minute"

//                        task["name"] = taskName.text.toString()
//                        task["description"] = taskDescription.text.toString()
                    task["deadline"] = "${deadlineCalendar.get(Calendar.MONTH)}/${deadlineCalendar.get(Calendar.DAY_OF_MONTH)}/${deadlineCalendar.get(Calendar.YEAR)} ${deadlineCalendar.get(Calendar.HOUR_OF_DAY)}:$minuteStr"
//                        task["created"] = creationTime.toString()
//                        task["interval"] = radioButton.text.toString()
                    db.collection(userId).add(task)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    android.widget.Toast.makeText(this, "Task Added", android.widget.Toast.LENGTH_SHORT).show()
                                } else {
                                    android.widget.Toast.makeText(this, "Problem Adding Task", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                }
            }
            taskName.setText("")
            taskDescription.setText("")
            taskDate.setText("")
            taskTime.setText("")
            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }

}