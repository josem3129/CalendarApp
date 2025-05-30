package com.example.calendarApp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.time.LocalDate
import androidx.fragment.app.Fragment
import com.example.calendarApp.R.*

class CalendarFragment : Fragment() {

    // Declare variables for the calendar view, authentication, and database.
    private lateinit var calendarView: MaterialCalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: Button
    private lateinit var adapter: AnnouncementAdapter

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val announcement = mutableListOf<Announcement>()
    private var selectedDate = LocalDate.now()

    // Inflate the layout for the fragment and set up the calendar view and recycler view.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for calendar view and recycler view
        val view = inflater.inflate(layout.fragment_calendar, container, false)

        // Set up the calendar view and recycler view from the layout
        calendarView = view.findViewById(R.id.calendarView)
        recyclerView = view.findViewById(R.id.announcementList)
        val selectedDateText = view.findViewById<TextView>(R.id.selectedDateText)
        addButton = view.findViewById(R.id.addAnnouncementButton)

        // Set up the recycler view and adapter for the announcements
        adapter = AnnouncementAdapter(announcement)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Set up the calendar view to show the announcements for the selected date
        calendarView.setOnDateChangedListener{ _, date, _ ->
            selectedDate = LocalDate.of(date.year, date.month + 1, date.day)
            selectedDateText.text = getString(string.announcements_for, selectedDate)
            loadAnnouncementsForDate(selectedDate)
        }
        //set up button to add an announcement in selected date
        addButton.setOnClickListener {
            showAddAnnouncementDialog()
        }
        // Return the view
        return view
    }

    // Load the announcements for the selected date from the database
    // and update the recycler view with the new data
    // If there is an error, show a toast message
    private fun loadAnnouncementsForDate(date: LocalDate) {
        db.collection("announcements")
            .whereEqualTo("date", date.toString())
            .get()
            .addOnSuccessListener { result ->
                announcement.clear()
                for (doc in result) {
                    val announcement = doc.toObject(Announcement::class.java)
                    this.announcement.add(announcement)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load announcements", Toast.LENGTH_SHORT).show()
            }

    }

    // Show a dialog to add a new announcement for the selected date
    // Get the text from the input field and save it to the database
    // If there is an error, show a toast message
    private  fun showAddAnnouncementDialog() {
        val input = EditText(requireContext())
        val currentUserId = auth.currentUser?.uid ?: return

        AlertDialog.Builder(requireContext())
            .setTitle("Add Announcement for $selectedDate")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val text = input.text.toString()
                val announcement = Announcement(text, currentUserId, selectedDate.toString())
                db.collection("announcements")
                    .add(announcement)
                    .addOnSuccessListener {
                        loadAnnouncementsForDate(selectedDate)
                        Toast.makeText(requireContext(), "Announcement saved", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to save announcement", Toast.LENGTH_SHORT).show()
                    }
            }
                // If the user clicks cancel, do nothing
            .setNegativeButton("Cancel", null).show()
    }

}

