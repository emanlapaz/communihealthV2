package com.mobile.communihealthv2.ui.calendar

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.storage.FirebaseStorage
import com.mobile.communihealthv2.R
import com.mobile.communihealthv2.databinding.FragmentCalendarBinding
import com.mobile.communihealthv2.models.PatientModel
import com.mobile.communihealthv2.ui.auth.LoggedInViewModel
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.util.Calendar
import java.util.Locale

class CalendarFragment : Fragment() {

    private lateinit var calendarViewModel: CalendarViewModel
    private var _fragBinding: FragmentCalendarBinding? = null
    private val fragBinding get() = _fragBinding!!
    private val loggedInViewModel: LoggedInViewModel by activityViewModels()
    private val args: CalendarFragmentArgs by navArgs()

    private var startTimeInMillis: Long = 0
    private var endTimeInMillis: Long = 0

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

        calendarViewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)

        calendarViewModel.getPatientData(
            loggedInViewModel.liveFirebaseUser.value?.uid!!,
            args.patientid
        )
        fragBinding.patientvm = calendarViewModel

        // Observe the LiveData
        calendarViewModel.observablePatient.observe(viewLifecycleOwner, Observer { patient ->
            render(patient)
        })

        fragBinding.selectStartTimeButton.setOnClickListener {
            Timber.d("selectStartTimeButton clicked")
            showTimePickerDialog(true)
        }
        fragBinding.selectEndTimeButton.setOnClickListener {
            Timber.d("selectEndTimeButton clicked")
            showTimePickerDialog(false)
        }

        fragBinding.saveAppButton.setOnClickListener {
            if (areCalendarPermissionsGranted()) {
                Timber.d("Save Appointment Button clicked")
                saveApp()

                val action = CalendarFragmentDirections.actionCalendarFragmentToProfileFragment(args.patientid)
                findNavController().navigate(action)
            } else {
                // Request calendar permissions
                Timber.d("Requesting calendar permissions")
                requestPermissions(calendarPermissions, PERMISSION_REQUEST_CODE)
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendarView = fragBinding.calendarView

        calendarViewModel.getPatientData(
            loggedInViewModel.liveFirebaseUser.value?.uid!!,
            args.patientid
        )

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->

            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            val dayOfWeek = selectedDate.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedDate.time)

            val startTime = getTimeString(startTimeInMillis)
            val endTime = getTimeString(endTimeInMillis)

            fragBinding.appDate.text = formattedDate
            fragBinding.appDay.text = dayOfWeek
            fragBinding.startTime.text = startTime
            fragBinding.endTime.text = endTime

            Timber.d("Selected Date: $formattedDate")
            Timber.d("Day of the Week: $dayOfWeek")
            Timber.d("Start Time: $startTime")
            Timber.d("End Time: $endTime")
        }
    }

    override fun onResume() {
        super.onResume()
        calendarViewModel.getPatientData(
            loggedInViewModel.liveFirebaseUser.value?.uid!!,
            args.patientid
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    private fun render(patient: PatientModel?) {
        Timber.i("CalendarFragment: render called")

        if (patient != null) {
            fragBinding.patientvm = calendarViewModel

            if (!patient.patientImage.isNullOrEmpty()) {
                val storageReference =
                    FirebaseStorage.getInstance().getReferenceFromUrl(patient.patientImage)

                storageReference.downloadUrl.addOnSuccessListener { imageUrl ->
                    Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.profile)
                        .error(R.drawable.profile)
                        .fit()
                        .centerInside()
                        .into(fragBinding.patientImageView)
                }.addOnFailureListener { exception ->

                    Timber.e(exception, "Failed to load patient image from Firebase Storage")
                }
            }
        } else {
            Toast.makeText(requireContext(), "Patient data not available", Toast.LENGTH_SHORT).show()
        }
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

                    startTimeInMillis = selectedTimeInMillis

                    fragBinding.startTime.text = getTimeString(startTimeInMillis)
                } else {

                    endTimeInMillis = selectedTimeInMillis

                    fragBinding.endTime.text = getTimeString(endTimeInMillis)
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

    private fun getTimeString(timeInMillis: Long): String {
        if (timeInMillis == 0L) {
            return ""
        }
        val timeCalendar = Calendar.getInstance()
        timeCalendar.timeInMillis = timeInMillis
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return timeFormat.format(timeCalendar.time)
    }

    private fun saveApp() {

        if (startTimeInMillis == 0L || endTimeInMillis == 0L) {
            Toast.makeText(requireContext(), "Please select valid start and end times", Toast.LENGTH_SHORT).show()
            return
        }

        val patient = calendarViewModel.observablePatient.value

        if (patient != null) {

            val updatedPatient = patient.copy(
                startTime = getTimeString(startTimeInMillis),
                endTime = getTimeString(endTimeInMillis),
                appDay = fragBinding.appDay.text.toString(),
                appDate = fragBinding.appDate.text.toString()
            )

            calendarViewModel.updatePatientData(
                loggedInViewModel.liveFirebaseUser.value?.uid ?: "",
                updatedPatient.uid ?: "",
                updatedPatient
            )

            Toast.makeText(requireContext(), "Appointment details saved to Firebase", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Patient data not available", Toast.LENGTH_SHORT).show()
        }
    }

}
