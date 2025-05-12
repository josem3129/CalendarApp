package com.example.calendarApp

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var currentUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        calendarView.setOnDateChangedListener(OnDateSelectedListener { _, date, _ ->
            val selectedDate = LocalDate.of(date.year, date.month + 1, date.day)
            showAnnouncementDialog(selectedDate)
        })
    }

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
