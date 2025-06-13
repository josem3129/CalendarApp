package com.example.calendarApp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

// adapter for the recycler view for the announcements
class AnnouncementAdapter(private val list: List<Announcement>,
    private val onItemClickListener: (Announcement) -> Unit) : //click listener for the recycler view

    RecyclerView.Adapter<AnnouncementAdapter.ViewHolder>() {
    private val auth = FirebaseAuth.getInstance()
        // view holder for the recycler view that shows the announcements
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.announcementText)
            init {
                view.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val clickedItem = list[position]
                        if (clickedItem.userId == auth.currentUser?.uid) {
                            onItemClickListener(list[position])
                    }else{
                        Toast.makeText(view.context, "You cannot edit this announcement", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            }
    }

    // create the view holder for the recycler view inflates the layout for the recycler view
    // this is the layout for the recycler view that shows the announcements
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_announcement, parent, false)
        return ViewHolder(view)
    }

    //gets count of the list of announcements
    override fun getItemCount(): Int = list.size

    // binds the data to the view holder for the recycler view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val announcement = list[position]
        holder.textView.text = announcement.text
    }
}

