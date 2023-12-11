package com.mobile.communihealthv2.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobile.communihealthv2.models.PatientManager
import com.mobile.communihealthv2.models.PatientModel

class PatientDetailViewModel : ViewModel() {
    private val patient = MutableLiveData<PatientModel>()

    val observablePatient: LiveData<PatientModel>
        get() = patient

    fun getPatient(patientId: Long) {
        patient.value = PatientManager.findById(patientId)
    }

}