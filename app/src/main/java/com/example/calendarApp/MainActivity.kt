// MainActivity.kt
// This file contains the main activity for the calendar app
// Is responsible for displaying hte calendar and handling user interactions
package com.example.calendarApp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.time.LocalDate
private lateinit var recyclerView: RecyclerView
private lateinit var adapter: AnnouncementAdapter
private var announcements = mutableListOf<Announcement>()
private lateinit var selectedDate: LocalDate


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
        recyclerView = findViewById(R.id.announcementList)
        recyclerView.layoutManager = LinearLayoutManager(this)
//        adapter = AnnouncementAdapter(announcements)
        recyclerView.adapter = adapter


        val addButton = findViewById<Button>(R.id.addAnnouncementButton)
        val selectedDateText = findViewById<TextView>(R.id.selectedDateText)

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        } else {
            currentUserId = user.uid
            println(user)
        }

        calendarView.setOnDateChangedListener{ _, date, _ ->
            selectedDate = LocalDate.of(date.year, date.month + 1, date.day)
            selectedDateText.text = "Announcements for: $selectedDate"
            loadAnnouncementsForDate(selectedDate)
        }
        addButton.setOnClickListener {
            showAddAnnouncementDialog()
        }
    }

    // showAddAnnouncementDialog is called when the user selects a date in the calendar.
    private fun showAddAnnouncementDialog() {
        val input = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Add Announcement for $selectedDate")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val text = input.text.toString()
                saveAnnouncement(selectedDate, text)
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
        // Save the announcement to the database "announcements" collection.
        // If the save is successful, a toast message is displayed.
        // If the save fails, an error message is displayed.
        db.collection("announcements")
            .add(announcement)
            .addOnSuccessListener {
                Toast.makeText(this, "Announcement saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save announcement", Toast.LENGTH_SHORT).show()
            }
    }

    //Loading announcements from the database
    private fun loadAnnouncementsForDate(date: LocalDate) {
        db.collection("announcements")
            .whereEqualTo("date", date.toString())
            .get()
            .addOnSuccessListener { result ->
                announcements.clear()
                for (doc in result) {
                    val announcement = doc.toObject(Announcement::class.java)
                    announcements.add(announcement)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load announcements", Toast.LENGTH_SHORT).show()
            }

    }
}

