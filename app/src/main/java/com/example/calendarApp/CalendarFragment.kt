package com.example.calendarApp

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
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
        adapter = AnnouncementAdapter(announcement) { selected ->
            showEditDialog(selected) // Show the edit dialog for the selected announcement***
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Set up the calendar view to show the announcements for the selected date
        calendarView.setOnDateChangedListener { _, date, _ ->
            selectedDate = LocalDate.of(date.year, date.month + 1, date.day)
            selectedDateText.text = getString(string.announcements_for)
            loadAnnouncementsForDate(selectedDate)
        }
        //set up button to add an announcement in selected date
        addButton.setOnClickListener {
            if (selectedDate != null) {
                showAddAnnouncementDialog(selectedDate!!)
            } else {
                Toast.makeText(requireContext(), "Select a date first", Toast.LENGTH_SHORT).show()
            }
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
                Toast.makeText(requireContext(), "Failed to load announcements", Toast.LENGTH_SHORT)
                    .show()
            }

    }

    // Show a dialog to add a new announcement for the selected date
    // Get the text from the input field and save it to the database
    // If there is an error, show a toast message
    private fun showAddAnnouncementDialog(selectedDate: LocalDate) {
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL

        val input = EditText(requireContext())
        input.hint = "Enter your announcement"
        layout.addView(input)

        val startTimeBtn = Button(requireContext()).apply { text = "select start time" }
        val endTimeBtn = Button(requireContext()).apply { text = "select end time" }
        layout.addView(startTimeBtn)
        layout.addView(endTimeBtn)

        var selectedStartTime = ""
        var selectedEndTime = ""

        startTimeBtn.setOnClickListener {
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                selectedStartTime = String.format("%02d:%02d", hourOfDay, minute)
                startTimeBtn.text = "Start Time: $selectedStartTime"
            }, 9, 0, true).show()

        }

        endTimeBtn.setOnClickListener {
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                selectedEndTime = String.format("%02d:%02d", hourOfDay, minute)
                endTimeBtn.text = "End Time: $selectedEndTime"
            }, 9, 0, true).show()
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Add Announcement for $selectedDate")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val text = input.text.toString()
                saveAnnouncement(text, selectedDate, selectedStartTime, selectedEndTime)

            }
            .show()
    }

    // Save the new announcement to the database
    // Get the text from the input field and save it to the database
    // save announcement to announcement collections
    // If there is an error, show a toast message
    private fun saveAnnouncement(
        text: String,
        date: LocalDate,
        startTime: String,
        endTime: String
    ) {
        val announcement = hashMapOf(
            "text" to text,
            "date" to date.toString(),
            "startTime" to startTime,
            "endTime" to endTime,
            "userId" to auth.currentUser!!.uid
        )
        db.collection("announcements")
            .add(announcement)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Announcement added", Toast.LENGTH_SHORT).show()
                loadAnnouncementsForDate(date)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to add announcement", Toast.LENGTH_SHORT)
                    .show()
            }
    }
    // Show a dialog to edit an existing announcement
    // Get the text from the input field and save it to the database
    // If there is an error, show a toast message
    private fun showEditDialog(announcement: Announcement) {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 30, 40, 0)
        }

        val input = EditText(requireContext()).apply {
            hint = "Edit text"
            setText(announcement.text)
        }

        val startBtn = Button(requireContext()).apply { text = "Start: ${announcement.startTime}" }
        val endBtn = Button(requireContext()).apply { text = "End: ${announcement.endTime}" }

        layout.addView(input)
        layout.addView(startBtn)
        layout.addView(endBtn)

        var start = announcement.startTime
        var end = announcement.endTime

        startBtn.setOnClickListener {
            TimePickerDialog(requireContext(), { _, h, m ->
                start = String.format("%02d:%02d", h, m)
                startBtn.text = "Start: $start"
            }, 9, 0, true).show()
        }

        endBtn.setOnClickListener {
            TimePickerDialog(requireContext(), { _, h, m ->
                end = String.format("%02d:%02d", h, m)
                endBtn.text = "End: $end"
            }, 10, 0, true).show()
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Announcement")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val updated = hashMapOf(
                    "text" to input.text.toString(),
                    "startTime" to start,
                    "endTime" to end
                )

                announcement.id?.let { docId ->
                    db.collection("announcements").document(docId)
                        .update(updated as Map<String, Any>)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Updated", Toast.LENGTH_SHORT).show()
                            selectedDate?.let { loadAnnouncementsForDate(it) }
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}

