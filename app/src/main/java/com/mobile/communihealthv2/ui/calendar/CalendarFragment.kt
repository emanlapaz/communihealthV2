package com.mobile.communihealthv2.ui.calendar

import android.Manifest
import android.app.TimePickerDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.pm.PackageManager
import android.icu.util.TimeZone
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mobile.communihealthv2.databinding.FragmentCalendarBinding
import com.mobile.communihealthv2.models.PatientModel
import com.mobile.communihealthv2.ui.auth.LoggedInViewModel
import com.mobile.communihealthv2.ui.patientlist.PatientListViewModel
import timber.log.Timber
import java.util.Calendar

class CalendarFragment : Fragment() {

    private lateinit var detailViewModel: CalendarViewModel
    private var _fragBinding: FragmentCalendarBinding? = null
    private val fragBinding get() = _fragBinding!!
    private val loggedInViewModel: LoggedInViewModel by activityViewModels()
    private val patientListViewModel: PatientListViewModel by activityViewModels()

    // Declare variables to store start and end times in milliseconds
    private var startTimeInMillis: Long = 0
    private var endTimeInMillis: Long = 0

    // Array of required calendar permissions
    private val calendarPermissions = arrayOf(
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR
    )

    private val PERMISSION_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        detailViewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)

        // Observe the LiveData
        detailViewModel.observablePatient.observe(viewLifecycleOwner, Observer {
            // Handle LiveData updates here, e.g., update UI elements
            render(it)
        })

        // Set click listeners for buttons and handle user interactions
        fragBinding.selectStartTimeButton.setOnClickListener {
            // Handle start time selection
            Timber.d("selectStartTimeButton clicked")
            showTimePickerDialog(true)
        }

        fragBinding.selectEndTimeButton.setOnClickListener {
            // Handle end time selection
            Timber.d("selectEndTimeButton clicked")
            showTimePickerDialog(false)
        }

        fragBinding.addEventButton.setOnClickListener {
            // Check and request calendar permissions
            if (areCalendarPermissionsGranted()) {
                // Permissions are already granted, proceed with adding the event
                Timber.d("addEventButton clicked")
                addEvent()
            } else {
                // Request calendar permissions
                Timber.d("Requesting calendar permissions")
                requestPermissions(calendarPermissions, PERMISSION_REQUEST_CODE)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    private fun render(patient: PatientModel) {
        // Update the UI with patient data
        // Example: fragBinding.eventTitleEditText.text = patient.title
    }

    private fun showTimePickerDialog(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedCalendar.set(Calendar.MINUTE, minute)

                val selectedTimeInMillis = selectedCalendar.timeInMillis

                if (isStartTime) {
                    // Set the start time
                    startTimeInMillis = selectedTimeInMillis
                } else {
                    // Set the end time
                    endTimeInMillis = selectedTimeInMillis
                }
            },
            hourOfDay,
            minute,
            true
        )

        timePickerDialog.show()
    }

    private fun areCalendarPermissionsGranted(): Boolean {
        for (permission in calendarPermissions) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun addEvent() {
        // Implement event creation logic using detailViewModel.addEventToCalendar()
        val title = fragBinding.eventTitleEditText.text.toString()
        val description = fragBinding.eventDescriptionEditText.text.toString()
        val location = fragBinding.eventLocationEditText.text.toString()

        val contentResolver: ContentResolver = requireContext().contentResolver

        if (startTimeInMillis != 0.toLong() && endTimeInMillis != 0.toLong()) {
            // Use the contentResolver to add an event to the device's calendar
            val eventValues = ContentValues().apply {
                put(CalendarContract.Events.TITLE, title)
                put(CalendarContract.Events.DESCRIPTION, description)
                put(CalendarContract.Events.EVENT_LOCATION, location)
                put(CalendarContract.Events.DTSTART, startTimeInMillis)
                put(CalendarContract.Events.DTEND, endTimeInMillis)
                put(CalendarContract.Events.CALENDAR_ID, 1) // Change to the appropriate calendar ID
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            }

            val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, eventValues)

            if (uri != null) {
                // Event added successfully
                Timber.d("Event added successfully. URI: $uri")
            } else {
                // Handle the case where the event addition failed
                Timber.e("Event addition failed")
            }
        } else {
            // Handle the case where the user hasn't selected start and end times
            // You can display a message to inform the user to select both start and end times
            Timber.w("User hasn't selected start and end times")
        }
    }
}
