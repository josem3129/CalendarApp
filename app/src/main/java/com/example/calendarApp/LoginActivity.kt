package com.example.calendarApp
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import com.example.calendarApp.R
import com.example.calendarApp.MainActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        emailField = findViewById(R.id.emailField)
        passwordField = findViewById(R.id.passwordField)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        loginButton.setOnClickListener{
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            login(email, password)
        }

        registerButton.setOnClickListener{
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString()
            register(email, password)
        }

    }
    private fun login(email:String, password: String){
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { goToMainActivity()}
            .addOnFailureListener{
                Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()}
    }
    private fun register(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                goToMainActivity()
            }
            .addOnFailureListener { exception ->
                val message = if (exception.message?.contains("email address is already in use") == true) {
                    "This email is already registered. Try logging in instead."
                } else {
                    "Registration failed: ${exception.message}"
                }
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
    }

    private fun goToMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}