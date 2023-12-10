package com.mobile.communihealthv2.ui.patient

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobile.communihealthv2.models.PatientManager
import com.mobile.communihealthv2.models.PatientModel

class PatientViewModel : ViewModel() {

    private val status = MutableLiveData<Boolean>()

    val observableStatus: LiveData<Boolean>
        get() = status

    fun addPatient(patient: PatientModel) {
        status.value = try {
            PatientManager.create(patient)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}