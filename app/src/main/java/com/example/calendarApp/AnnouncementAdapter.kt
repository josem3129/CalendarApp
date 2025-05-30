package com.example.calendarApp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// adapter for the recycler view for the announcements
class AnnouncementAdapter(private val list: List<Announcement>) :
    RecyclerView.Adapter<AnnouncementAdapter.ViewHolder>() {

        // view holder for the recycler view that shows the announcements
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.announcementText)
    }

    // create the view holder for the recycler view inflates the layout for the recycler view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_announcement, parent, false)
        return ViewHolder(view)
    }

    //gets count of the list of announcements
    override fun getItemCount() = list.size

    // binds the data to the view holder for the recycler view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val announcement = list[position]
        holder.textView.text = announcement.text
    }
}

