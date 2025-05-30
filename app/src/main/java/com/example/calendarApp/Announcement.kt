package com.example.calendarApp

// announcement class for the database
data class Announcement(
    val text: String = "",
    val userId: String = "",
    val date: String = "",
    var id: String? = null
)
