// MainActivity.kt
// This file contains the main activity for the calendar app
// Is responsible for displaying hte calendar and handling user interactions
package com.example.calendarApp

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.time.LocalDate

/**
 * The main activity for the calendar app.
 * This activity is responsible for displaying the calendar and handling user interactions.
 * It also saves and retrieves announcements from the database.**/
class MainActivity : AppCompatActivity() {

    // Declare variables for the calendar view, authentication, and database.
    private lateinit var calendarView: MaterialCalendarView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var currentUserId: String

    // This method is called when the activity is created.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the calendar view, authentication, and database.
        // If the user is not logged in, they are redirected to the login screen.
        calendarView = findViewById(R.id.calendarView)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        } else {
            currentUserId = user.uid
        }

        calendarView.setOnDateChangedListener{ _, date, _ ->
            val selectedDate = LocalDate.of(date.year, date.month + 1, date.day)
            showAnnouncementDialog(selectedDate)
        }
    }

    // showAnnouncementDialog is called when the user selects a date in the calendar.
    private fun showAnnouncementDialog(date: LocalDate) {
        val input = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Add Announcement for $date")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val text = input.text.toString()
                saveAnnouncement(date, text)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    // saveAnnouncement is called when the user clicks the "Save" button in the announcement dialog.
    private fun saveAnnouncement(date: LocalDate, text: String) {
        val announcement = hashMapOf(
            "userId" to currentUserId,
            "date" to date.toString(),
            "text" to text
        )

        db.collection("announcements")
            .add(announcement)
            .addOnSuccessListener {
                Toast.makeText(this, "Announcement saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save announcement", Toast.LENGTH_SHORT).show()
            }
    }
}

//Todo: Implement proper authentication flow after registration and login
//Todo: add error handling for fetching announcements