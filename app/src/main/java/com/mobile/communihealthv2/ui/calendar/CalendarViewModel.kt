package com.mobile.communihealthv2.ui.calendar

import android.content.ContentResolver
import android.content.ContentValues
import android.provider.CalendarContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobile.communihealthv2.firebase.FirebaseDBManager
import com.mobile.communihealthv2.models.PatientModel
import timber.log.Timber

class CalendarViewModel : ViewModel() {
    private val patient = MutableLiveData<PatientModel>()

    var observablePatient: LiveData<PatientModel>
        get() = patient
        set(value) {
            patient.value = value.value
        }

    fun addEventToCalendar(
        contentResolver: ContentResolver, // Pass the ContentResolver as a parameter
        title: String,
        description: String,
        location: String,
        startTimeInMillis: Long,
        endTimeInMillis: Long
    ) = try {
        // Create a new ContentValues object to store event details
        val eventValues = ContentValues().apply {
            put(CalendarContract.Events.TITLE, title)
            put(CalendarContract.Events.DESCRIPTION, description)
            put(CalendarContract.Events.EVENT_LOCATION, location)
            put(CalendarContract.Events.DTSTART, startTimeInMillis)
            put(CalendarContract.Events.DTEND, endTimeInMillis)
            put(CalendarContract.Events.CALENDAR_ID, 1) // Use the primary calendar
            put(CalendarContract.Events.EVENT_TIMEZONE, "UTC")
        }

        // Insert the event into the calendar
        val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, eventValues)

        if (uri != null) {
            Timber.i("Event added to calendar with ID: ${uri.lastPathSegment}")
            // Handle success, you can also return the event ID if needed
        } else {
            Timber.e("Failed to add event to calendar")
            // Handle failure
        }
    } catch (e: Exception) {
        Timber.e("Error adding event to calendar: ${e.message}")
        // Handle the exception
    }
}
