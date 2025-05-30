package com.example.calendarApp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.calendarApp.R.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//activity for completing the profile
//saves the user's first name, last name, and organization to the database
//if the user clicks cancel, the user is taken back to the main activity
class CompleteProfileActivity : AppCompatActivity(){

    //declare variables for the fields and buttons
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    //set up the fields and buttons
    //if the user clicks save, the user is taken to the main activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_complete_profile)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        //set up the fields and buttons
        val firstNameField = findViewById<EditText>(id.firstNameField)
        val lastNameField = findViewById<EditText>(id.lastNameField)
        val orgSpinner = findViewById<Spinner>(id.orgSpinner)
        val saveButton = findViewById<Button>(id.saveProfileButton)

        //set up the spinner for the organization
        //the spinner is populated with the organizations from the strings.xml file
        // items to select are organisation in a ward
        val adapter = ArrayAdapter.createFromResource(
            this,
            array.organizations, android.R.layout.simple_spinner_item
        )

        //set the dropdown view for the spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        orgSpinner.adapter = adapter

        //if the user clicks save, the user is taken to the main activity
        saveButton.setOnClickListener{
            val firstName = firstNameField.text.toString()
            val lastName = lastNameField.text.toString()
            val org = orgSpinner.selectedItem.toString()

            //if the user has not completed all fields, a toast message is displayed
            if (firstName.isBlank() || lastName.isBlank()){
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //if the user has not selected an organization, a toast message is displayed
            val userId = auth.currentUser?.uid ?: return@setOnClickListener

            //gets the user's first name, last name, and organization from the database
            val userInfo = hashMapOf(
                "firstName" to firstName,
                "lastName" to lastName,
                "org" to org
            )

            //saves the user's first name, last name, and organization to the database
            //if database fails to save, a toast message is displayed
            db.collection("users").document(userId).set(userInfo)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Failed to save profile", Toast.LENGTH_SHORT).show()
                }
        }


    }
}