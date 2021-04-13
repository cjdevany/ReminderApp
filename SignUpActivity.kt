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
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        title = "Sign Up"

        var emailInput = findViewById<EditText>(R.id.signUp_emailInput)
        var pwInput = findViewById<EditText>(R.id.signUp_pwInput)
        var confirmPwInput = findViewById<EditText>(R.id.signUp_confirmPwInput)
        var showLabel = findViewById<TextView>(R.id.signUp_showLabel)
        var showConfirmLabel = findViewById<TextView>(R.id.signUp_showConfirmLabel)
        var createBtn = findViewById<Button>(R.id.signUp_createBtn)
        var backBtn = findViewById<Button>(R.id.signUp_backBtn)

        // --- BUTTON CLICK LISTENERS ---

        createBtn.setOnClickListener {

            var isValidEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput.text).matches()
            var pwdMatch = pwInput.text.toString() == confirmPwInput.text.toString()
            var isValidPw: Boolean = false

            var isLongEnough: Boolean = false
            var hasUpperCase: Boolean = false
            var hasLowerCase: Boolean = false
            var hasNumber: Boolean = false
            var hasSpecialChar: Boolean = false

            var special = arrayListOf('!', '@', '#', '$', '%')

            //Don't run this loop if passwords don't match
            if(pwdMatch) {

                if(pwInput.text.length > 5)
                    isLongEnough = true

                for(i in pwInput.text.toString()) {
                    if( i in 'A'..'Z') {
                        hasUpperCase = true
                    }

                    if(i in 'a'..'z') {
                        hasLowerCase = true
                    }

                    if(i in '1'..'9') {
                        hasNumber = true
                    }

                    if(special.contains(i))
                        hasSpecialChar = true
                }
            }

            //Does the password match all rules?
            if(isLongEnough && hasUpperCase && hasLowerCase && hasNumber && hasSpecialChar)
                isValidPw = true

            //Create account or print why it couldn't be created
            if(isValidEmail && pwdMatch && isValidPw) {
                auth.createUserWithEmailAndPassword(emailInput.text.toString(), pwInput.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful) {
                        startActivity(Intent(this, LogInActivity::class.java))
                        Toast.makeText(this, "Account has been created successfully!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if(!isValidEmail) {
                Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show()
            } else if(!pwdMatch) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else if(!isValidPw) {
                if(!isLongEnough)
                    Toast.makeText(this, "Invalid password, not long enough", Toast.LENGTH_SHORT).show()
                else if(!hasUpperCase)
                    Toast.makeText(this, "Invalid password, contains no uppercase letters", Toast.LENGTH_SHORT).show()
                else if(!hasLowerCase)
                    Toast.makeText(this, "Invalid password, contains no lowercase letters", Toast.LENGTH_SHORT).show()
                else if(!hasNumber)
                    Toast.makeText(this, "Invalid password, contains no numbers", Toast.LENGTH_SHORT).show()
                else if(!hasSpecialChar)
                    Toast.makeText(this, "Invalid password, contains no special characters", Toast.LENGTH_SHORT).show()
            }
        }

        backBtn.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
        }

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

        showConfirmLabel.setOnClickListener {
            if(showConfirmLabel.text == "Show" ) {
                showConfirmLabel.text = "Hide"
                confirmPwInput.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
            else {
                showConfirmLabel.text = "Show"
                confirmPwInput.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }
}