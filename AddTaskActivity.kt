package com.example.reminderapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        val db = FirebaseFirestore.getInstance()
        val userId = auth.currentUser.uid

        val radioGroup = findViewById<RadioGroup>(R.id.addTaskRadioGroup)
        var radioButton : RadioButton

        val taskName = findViewById<EditText>(R.id.addTaskName)
        val taskDescription = findViewById<EditText>(R.id.addTaskDescription)
        val taskDate = findViewById<EditText>(R.id.addTaskDate)
        val taskTime = findViewById<EditText>(R.id.addTaskTime)

        var deadline : Date = Date()
        // Formats a date from the user's input.
        val dateFormatter = SimpleDateFormat("MM/dd/yyyy HH:mm")
        // Formatters to get the day/month/year from the date the user entered.
        val getDay = SimpleDateFormat("dd")
        val getMonth = SimpleDateFormat("MM")

        val addTaskButton = findViewById<Button>(R.id.addTaskButton)

        addTaskButton.setOnClickListener {
            // Get the repetition from radio button
            var radioId = radioGroup.checkedRadioButtonId
            radioButton = findViewById(radioId)
            Toast.makeText(this, radioButton.text.toString(), Toast.LENGTH_SHORT).show()

            var selectedInterval = radioButton.text.toString()
            // Time
            val date_var = taskDate.text.toString()
            val time_var = taskTime.text.toString()
            val dateTime = date_var + " " + time_var
            deadline = dateFormatter.parse(dateTime)
            var creationTime = dateFormatter.format(Date()).toString()


            // One instance of the task
            // TODO: Create multiple instantes of the task based on interval given.
            // Uses getDay, getMonth
            val task : MutableMap<String, Any?> = HashMap()
            task["name"] = taskName.text.toString()
            task["description"] = taskDescription.text.toString()
            task["deadline"] = deadline.toString()
            task["created"] = creationTime.toString()
            task["interval"] = radioButton.text.toString()

//            db.collection(userId).add
        }
    }

}