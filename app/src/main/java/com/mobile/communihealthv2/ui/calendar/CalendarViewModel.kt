package com.mobile.communihealthv2.ui.calendar

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

    fun getPatientData(userid: String, id: String) {
        try {
            FirebaseDBManager.findById(userid, id, patient)
            Timber.i("Detail getPatientData() Success : ${patient.value.toString()}")
        } catch (e: Exception) {
            Timber.i("Detail getPatientData() Error : $e.message")
        }
    }

    fun updatePatientData(userid: String, id: String, patient: PatientModel) {
        try {
            FirebaseDBManager.update(userid, id, patient)
            Timber.i("Detail updatePatientData() Success : $patient")
        } catch (e: Exception) {
            Timber.i("Detail updatePatientData() Error : $e.message")
        }
    }
}
