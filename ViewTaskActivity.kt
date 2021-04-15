package com.example.reminderapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.split
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap

class ViewTaskActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_task)


        // Variables for the radio group for interval selection.
        val radioGroup = findViewById<RadioGroup>(R.id.viewTaskRadioGroup)
        var radioButton : RadioButton

        // Text Field variables.
        val taskName = findViewById<EditText>(R.id.viewTaskName)
        val taskDescription = findViewById<EditText>(R.id.viewTaskDescription)
        val taskDate = findViewById<EditText>(R.id.viewTaskDate)
        val taskTime = findViewById<EditText>(R.id.viewTaskTime)

        // Current date - updated in viewTaskButton onClickListener using user input.
        var deadline : Date = Date()
        // Formats a date from the user's input.
        val dateFormatter = SimpleDateFormat("MM/dd/yyyy HH:mm")
        // Formatters to get the day/month/year from the date the user entered.
        val getDate = SimpleDateFormat("MM/dd/yyyy")
        val getTime = SimpleDateFormat("HH:mm")
        val testFormatter = SimpleDateFormat("E L dd HH:mm:ss z yyyy")

        // Button variable
        val viewTaskButton = findViewById<Button>(R.id.viewTaskButton)

        // Get date and time from intent and format them.
//        val test = SimpleDateFormat(Calendar.LONG_FORMAT)
        val taskDeadlineVar = intent.getStringExtra("deadline")?.split(" ")!!

        taskName.setText(intent.getStringExtra("name").toString())
        taskDescription.setText(intent.getStringExtra("description").toString())
        taskDate.setText(taskDeadlineVar[0])
        taskTime.setText(taskDeadlineVar[1])

        var documentId = intent.getStringExtra("documentID").toString()
        val userId = intent.getStringExtra("uid").toString()
        var interval = intent.getStringExtra("interval").toString()

        viewTaskButton.setOnClickListener {
            var radioId = radioGroup.checkedRadioButtonId
            radioButton = findViewById(radioId)

            // Data base instance and user id of current user
            val db = FirebaseFirestore.getInstance()
            val task : MutableMap<String, Any?> = HashMap()
            task["name"] = taskName.text.toString()
            task["description"] = taskDescription.text.toString()
            task["deadline"] = taskDate.text.toString() + " " + taskTime.text.toString()
            task["created"] = dateFormatter.format(Date()).toString()
            task["interval"] = radioButton.text.toString()
            
            db.collection(userId).document(documentId).set(task).addOnCompleteListener { 
                if (it.isSuccessful) {
                    Toast.makeText(this, "Task Updated", Toast.LENGTH_SHORT).show()
//                    finish()
                    startActivity(Intent(this, DashboardActivity::class.java))
                } else {
                    Toast.makeText(this, "There was a problem updating the task.", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}