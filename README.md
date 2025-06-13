# Overview


Description of Project
Project Name: Calendar App
calendar app was done using kotlin and android studio, calendar app uses firebase auth 
for authentication and firebase firestore for storage. 

purpose for creating this software.
Calendar app is was created to help ward member of the church of Jesus Christ of ladder-day saints add announcements for any upcoming activities.
This is to help members of the bishopric not to have too many announcements during sacrament meeting.

youtube video: https://youtu.be/-rwQFMbJww4
# Cloud Database
This project uses firestore as the cloud database.
The structure of the database is as follows:
announcements – This collection will store calendar events or announcements. Each document will include fields such as:

-text (String) > body of the announcement
-date (Timestamp)
-userId (String)
-start time (String)
-end time (String)

users – This collection will store user information. Each user document will have fields such as:

-firstName (String)
-lastName (String)
-org (String)
-email (String)


Data Operations (Insert, Modify, Delete, Retrieve)
The app will demonstrate full CRUD functionality using Firestore:

Insert: Users can add new announcements and create accounts.

Modify: Users can update event details or user information.

Delete: Announcements can be removed as needed by user.

Retrieve: Data will be queried and displayed in the app, with real-time updates reflecting any changes.

# Development Environment

Tools used
Android Studio (IDE)
Firebase Authentication (Backend Service for Authentication)
Firebase Firestorm (Backend Service for Data Storage)

programming language
Kotlin (Programming Language)
XML  (Extensible Markup Language)

# Useful Websites
Kotlin documentation - https://kotlinlang.org/docs/home.html
Firebase documentation - https://firebase.google.com/docs/firestore/quickstart
Android studio documentation - https://developer.android.com/studio/index.html
W3 schools XML - https://www.w3schools.com/xml/default.asp
W3 schools Kotlin - https://www.w3schools.com/kotlin/index.php
Geeks for geeks UI layouts - https://www.geeksforgeeks.org/android-ui-layouts/

Helpful websites for development
W3 schools XML - https://www.w3schools.com/xml/default.asp
W3 schools Kotlin - https://www.w3schools.com/kotlin/index.php
Class Material Calendar view - https://prolificinteractive.github.io/material-calendarview/index.html?com/prolificinteractive/materialcalendarview/MaterialCalendarView.html
Youtube video on layouts - https://youtu.be/cVcXrKcl9cQ?si=VQMDgyBuFaE_KiI-
# Future Work

- Add a Logout function 
- Add a field of list of announcement that changes by selecting the date 
- Add registration page were it asks for more info 
- add more info to add announcement example: organisation, location, ect