// LoginActivity.kt
package com.example.calendarApp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast

/** LoginActivity.kt
 * This file contains the login activity for the calendar app.
 * It is responsible for handling user login and registration.**/
class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    // This method is called when the activity is created.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        // Initialize the UI elements.
        emailField = findViewById(R.id.emailField)
        passwordField = findViewById(R.id.passwordField)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        // Set up the login button click listener.
        loginButton.setOnClickListener{
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            login(email, password)
        }

        // Set up the register button click listener.
        registerButton.setOnClickListener{
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString()
            register(email, password)
        }

    }

    // login is called when the user clicks the login button.
    // It validates the user's credentials and logs them in if they are valid.
    private fun login(email:String, password: String){
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "@String/empty_fields", Toast.LENGTH_SHORT).show()
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { goToMainActivity()}
            .addOnFailureListener{
                Toast.makeText(this@LoginActivity, "@String/error_login_failed", Toast.LENGTH_SHORT).show()}
    }

    // register is called when the user clicks the register button.
    // It validates the user's credentials and registers them if they are valid.
    private fun register(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "@String/error_empty_field", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                goToMainActivity()
            }
            .addOnFailureListener { exception ->
                val message = if (exception.message?.contains("email address is already in use") == true) {
                    "@string/error_email_already_registered"
                } else {
                    "@string/error_registration_failed"
                }
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
    }

    // goToMainActivity is called when the user logs in successfully.
    // It starts the main activity.
    private fun goToMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}