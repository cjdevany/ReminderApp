package com.example.reminderapp

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AccountViewActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_view)

        var emailLabel = findViewById<TextView>(R.id.AccountViewEmailLabel)

        emailLabel.setText(auth.getCurrentUser().email.toString())

        var signOutButton = findViewById<Button>(R.id.accountViewSignOutButton)
        signOutButton.setOnClickListener {
            var alert = AlertDialog.Builder(this)
            alert.setTitle("Logging Out")
            alert.setMessage("You are about to log out, do you wish to continue?")
            alert.setPositiveButton("Yes", { dialogInterface: DialogInterface, i: Int->
                auth.signOut()
                startActivity(Intent(this, LogInActivity::class.java))
            })
            alert.setNegativeButton("No", { dialogInterface: DialogInterface, i: Int ->

            })
            alert.show()
        }
    }
}
