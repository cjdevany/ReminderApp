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
                    sort(tasks, 0, tasks.size - 1)

                    //spawn in the listview and add the proper contents to it
                    var listView = findViewById<ListView>(R.id.dashboardListView)
                    var listAdapter = MyAdapter(tasks, this)

                    listView.adapter = listAdapter
                    listView.setOnItemClickListener { parent, view, position, id ->
                        var intent = Intent(this, ViewTaskActivity::class.java)

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
            tv.text = "${dataList[position]["name"]}  ${dataList[position]["deadline"]}"                            //display the proper index's value
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

    private fun sort(arr: ArrayList<MutableMap<String, Any?>>, low: Int, high: Int)
    {

        if(low < high)
        {
            var index = partition(arr, low, high)

            sort(arr, low, index)
            sort(arr, index + 1, high)
        }

    }

    private fun partition(arr: ArrayList<MutableMap<String, Any?>>, low: Int, high: Int): Int
    {
        var pivot = arr[low]
        var pivotDateValue = getDateValue(pivot["deadline"].toString())
        var i: Int = low
        var j: Int = high

        Log.w("***Date Value ", pivotDateValue.toString())

        while(i < j)
        {
            while(getDateValue(arr[i]["deadline"].toString()) <= pivotDateValue && i < high)
            {
                i += 1
            }

            while(getDateValue(arr[j]["deadline"].toString()) > pivotDateValue && j > low)
            {
                j -= 1
            }

            if(i < j)
            {
                var swap = arr[i]
                arr[i] = arr[j]
                arr[j] = swap
            }

        }

        arr[low] = arr[j]
        arr[j] = pivot

        return j

    }

    private fun getDateValue(mm_dd_yyyy_time: String): Long {

        //This will split the string to ['mm', 'dd', 'yyyy hh:mm']
        var dateSplit = mm_dd_yyyy_time.split("/").toTypedArray()

        //Splits ['yyyy xx:xx'] to ['yyyy', 'hh:mm']
        var yearSplit = dateSplit[2].split(" ").toTypedArray()

        //Now lets split the time to hours and minutes: ['xx:xx'] to ['hh', 'mm']
        var timeSplit = yearSplit[1].split(":").toTypedArray()

        /**Then I want to parse each var to an int, this removes any leading 0's and keeps things consistent,
        That way I don't add extra 0's later on.*/

        var yearValue = yearSplit[0].toInt()
        var monthValue = dateSplit[0].toInt()
        var dayValue = dateSplit[1].toInt()
        var hourValue = timeSplit[0].toInt()
        var minuteValue = timeSplit[1].toInt()

        /**
         * For our "date value", want to create an Int that has a consistent length with
         * all other date objects. This means that for any single digit, we need to add a 0 as well.
         * Easier to just convert to a string and concat a '0'
         *
         * Formate for the value - yyyymmddhhmm
         */

        //Are the month, date, time values single-digits??
        var month: String
        var day: String
        var hour: String
        var minute: String

        if(monthValue < 10)
            month = "0$monthValue"
        else
            month ="$monthValue"

        if(dayValue < 10)
            day = "0$dayValue"
        else
            day ="$dayValue"

        if(hourValue < 10)
            hour = "0$hourValue"
        else
            hour ="$hourValue"

        if(minuteValue < 10)
            minute = "0$minuteValue"
        else
            minute ="$minuteValue"

        //Now combine all the values into a single string!
        var valueStr = "$yearValue$month$day$hour$minute"

        return valueStr.toLong()
    }

}