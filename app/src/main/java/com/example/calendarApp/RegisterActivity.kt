package com.example.calendarApp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// registry activity for email and password
class RegisterActivity : AppCompatActivity(){
    // Declare variables for the UI elements and the Firebase authentication and database objects.
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // This method is called when the activity is created.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize the Firebase authentication and database objects.
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Set up the UI elements and the click listener for the register button.
        val emailField = findViewById<EditText>(R.id.emailField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val confirmPasswordField = findViewById<EditText>(R.id.confirmPasswordField)
        val firstNameField = findViewById<EditText>(R.id.firstNameField)
        val lastNameField = findViewById<EditText>(R.id.lastNameField)
        val orgSpinner = findViewById<Spinner>(R.id.orgSpinner)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val adapter = ArrayAdapter.createFromResource(
            this, R.array.organizations,
            android.R.layout.simple_spinner_item
        )
        // Set the dropdown view resource for the spinner. that holds the organization names.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        orgSpinner.adapter = adapter

        // Set up the click listener for the register button.
        registerButton.setOnClickListener{
            // Get the user input from the UI elements.
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString()
            val confirm = confirmPasswordField.text.toString()
            val firstName = firstNameField.text.toString().trim()
            val lastName = lastNameField.text.toString().trim()
            val org = orgSpinner.selectedItem.toString()

            // Validate the user input.
            //check if any of the fields are empty
            if(email.isBlank() || password.isBlank() || confirm.isBlank() ||
                firstName.isBlank() || lastName.isBlank() || org.isBlank()) {
                Toast.makeText(this, "@string/empty_registration_fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //check if the password and confirm password match
            if(password != confirm) {
                Toast.makeText(this, "@string/password_no_match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //check if the password is at least 6 characters long
            //if not, display an error message
            //if it is, create a new user with the provided email and password
            //and save the user's first name, last name, and organization to the database
            //if the registration fails, display an error message
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val userId = auth.currentUser!!.uid
                    val userInfo = hashMapOf(
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "org" to org
                    )
                    db.collection("users").document(userId).set(userInfo)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                            goToMainActivity()
                            finish()
                        }
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Registration failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }


        }
    }
        // This method is called when the user clicks the "Register" button.
        // redirects the user to the main activity calendar screen.
        private fun goToMainActivity(){
            val intent = Intent(this, BottomNavActivity::class.java)
            startActivity(intent)
            finish()
        }
}