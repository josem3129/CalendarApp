package com.example.calendarApp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

//bottom navigation bar for the app where the user can switch between the calendar and the profile
class BottomNavActivity : AppCompatActivity() {
    //creates the bottom navigation bar for the app
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav)

        //sets the default fragment to the calendar fragment
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        loaderManager(CalendarFragment())

        //sets the fragment to the calendar fragment when the user clicks on the calendar button
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_calendar -> {
                    loadFragment(CalendarFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

    }

    //loads the fragment into the fragment container
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
    //loads the fragment into the fragment container
    // used to switch between the calendar and the profile
    private fun loaderManager(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }
}

