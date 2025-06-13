package com.example.calendarApp


import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate

// User profile fragment Where they can see their announcements and delete them
// by swiping left or right.
class ProfileFragment : Fragment() {

    // Declare variables for the recycler view, adapter, and list of announcements.
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AnnouncementAdapter
    private val announcements = mutableListOf<Announcement>()

    // Initialize the database and authentication objects.
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var selectedDate = LocalDate.now()
    private lateinit var spinner: Spinner
    private lateinit var saveButton: Button


    // This method is called when the fragment is created.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        // Set up the recycler view and adapter
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        recyclerView = view.findViewById(R.id.userAnnouncementsList)
        adapter = AnnouncementAdapter(announcements) { selected ->
            showEditDialog(selected)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
        recyclerView.adapter = adapter

        // Load the user's announcements from the database
        // Set up the swipe to delete functionality
        loadUserAnnouncements()
        setupSwipeToDelete()
        return view



    }

    // Form view for use info, organization, and save button
    // when user makes changes to their info, it is saved to the database
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userName = view.findViewById<TextView>(R.id.userName)
        val userEmail = view.findViewById<TextView>(R.id.userEmail)
        spinner = view.findViewById(R.id.organizationSpinner)
        saveButton = view.findViewById(R.id.saveButton)

        val organization = listOf(
            "Elders Quorum", "Relief Society", "Primary",
            "Bishopric", "Sunday School", "Missionary Work", "Family History"
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, organization)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        val currentUser = auth.currentUser ?: return

        db.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val firstName = document.getString("firstName")
                    val lastName = document.getString("lastName")
                    val org = document.getString("organization")
                    val email = currentUser.email.toString()

                    userName.text = "$firstName $lastName"
                    userEmail.text = email
                    val pos = organization.indexOf(org)
                    if (pos != -1) spinner.setSelection(pos)
                }
            }

        saveButton.setOnClickListener{
            val selectedOrg = spinner.selectedItem.toString()
            db.collection("users").document(currentUser.uid).update("organization", selectedOrg)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to save", Toast.LENGTH_SHORT).show()
                }
        }

    }
    // Load the user's announcements from the database
    // if the load is successful, the adapter is notified of the change and Toast is displayed
    //if the load fails, a Toast is displayed
    private fun loadUserAnnouncements() {
        val currentUserId = auth.currentUser?.uid ?: return
        db.collection("announcements")
            .whereEqualTo("userId", currentUserId)
            .get()
            .addOnSuccessListener { result ->
                announcements.clear()
                for (doc in result) {
                    val announcement = doc.toObject(Announcement::class.java)
                    announcement.id = doc.id
                    announcements.add(announcement)
                }
                adapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "Found ${announcements.size} announcements", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load announcements", Toast.LENGTH_SHORT).show()
            }
    }

    // Set up the swipe to delete functionality
    private fun setupSwipeToDelete() {
        // Set up the swipe to delete functionality left or right
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

            // Delete the announcement from the database if the user swipes left or right
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val announcement = announcements[position]

                // Delete the announcement from the database
                //if the delete is successful, the announcement is removed from the list and the adapter is notified of the change
                //if the delete fails, a Toast is displayed
                db.collection("announcements").document(announcement.id!!).delete()
                    .addOnSuccessListener {
                        announcements.removeAt(position)
                        adapter.notifyItemRemoved(position)
                        Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to delete", Toast.LENGTH_SHORT).show()
                    }
            }
        })
        // Attach the item touch helper to the recycler view
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
    // Show the edit dialog for the announcement
    // only user with same id can edit
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
                            selectedDate?.let { loadUserAnnouncements() }
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