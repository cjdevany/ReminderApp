package com.example.reminderapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        var db = FirebaseFirestore.getInstance()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        //get the user's id
        var uid = auth.currentUser.uid

        //get all tasks from that uid
        var tasks = arrayListOf<MutableMap<String, Any?>>()
        var accountBtn = findViewById<Button>(R.id.dashboardAccountBtn)
        var addBtn = findViewById<Button>(R.id.dashboardAddTaskBtn)

        db.collection(uid)
            .get()
            .addOnSuccessListener { documents ->
                //for each document, grab its data and add it to the list of all user tasks
                for (document in documents) {
                    if(document != null) {
                        var task = document.data as MutableMap<String, Any?>
                        task["documentID"] = document.id
                        tasks.add(task)
                    }
                }
                //spawn in the listview and add the proper contents to it
                var listView = findViewById<ListView>(R.id.dashboardListView)
                var listAdapter = MyAdapter(tasks, this)

                listView.adapter = listAdapter
                listView.setOnItemClickListener { parent, view, position, id ->
                    var intent = Intent(this, ViewTaskActivity::class.java)
                    //TODO: add the task data needed to the intent before spawning ViewTaskActivity
                    Toast.makeText(this, "clicked position " + position, Toast.LENGTH_SHORT).show()

                    var task = tasks.get(position)
                    intent.putExtra("uid", auth.currentUser.uid)
                    intent.putExtra("documentID", task["documentID"] as String)
                    intent.putExtra("created", task["created"] as String)
                    intent.putExtra("deadline", task["deadline"] as String)
                    intent.putExtra("description", task["description"] as String)
                    intent.putExtra("interval", task["interval"] as String)
                    intent.putExtra("name", task["name"] as String)
                    startActivity(intent)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error pulling user data", Toast.LENGTH_SHORT).show()
                Log.w("TAG", "Error getting documents: ", exception)
            }

        accountBtn.setOnClickListener {
            var intent = Intent(this, AccountViewActivity::class.java)
            startActivity(intent)
        }

        addBtn.setOnClickListener {
            var intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

    }

    //custom adapter class takes an ArrayList<MutableMap>
    class MyAdapter(var dataList : ArrayList<MutableMap<String, Any?>>, var context : Context) : BaseAdapter(){
        //Function tells the adapter how many cells we need for our view
        override fun getCount(): Int {
            return dataList.size
        }

        //returns what to display in each cell depending on it's position on the screen
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val tv = TextView(context)
            tv.text = dataList[position]["name"] as String                            //display the proper index's value
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25F)    //changes font size
            tv.setPadding(10, 10, 10, 10)    //sets the padding
            return tv
        }

        override fun getItem(position: Int): Any {
            return 0
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

    }
}
