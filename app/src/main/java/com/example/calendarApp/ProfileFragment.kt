package com.example.calendarApp


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
        adapter = AnnouncementAdapter(announcements)
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
}