package com.example.reminderapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LogInActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        title = "Log In"

        var emailInput = findViewById<EditText>(R.id.logIn_emailInput)
        var pwInput = findViewById<EditText>(R.id.logIn_pwInput)
        var signInBtn = findViewById<Button>(R.id.logIn_signInBtn)
        var signUpBtn = findViewById<Button>(R.id.logIn_signUpBtn)
        var showLabel = findViewById<TextView>(R.id.logIn_showLabel)


        // --- BUTTON CLICK LISTENERS ---

        showLabel.setOnClickListener {
            if(showLabel.text == "Show" ) {
                showLabel.text = "Hide"
                pwInput.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
            else {
                showLabel.text = "Show"
                pwInput.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        /**
         * Intent Variable: uid - Passed to DashboardActivity
         * */
        signInBtn.setOnClickListener {

            auth.signInWithEmailAndPassword(emailInput.text.toString(), pwInput.text.toString()).addOnCompleteListener {

                if(it.isSuccessful) {
                    var intent = Intent(this, DashboardActivity::class.java)
                    intent.putExtra("uid", auth.currentUser.uid)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Invalid email/password", Toast.LENGTH_SHORT).show()
                }
            }

        }

        signUpBtn.setOnClickListener {
           startActivity(Intent(this, SignUpActivity::class.java))
        }





    }
}