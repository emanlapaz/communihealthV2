package com.mobile.communihealthv2.ui.patientlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobile.communihealthv2.models.PatientManager
import com.mobile.communihealthv2.models.PatientModel
class PatientListViewModel : ViewModel() {

    private val patientsList = MutableLiveData<List<PatientModel>>()

    val observablePatientsList: LiveData<List<PatientModel>>
        get() = patientsList
    init{
        load()
    }
    fun load() {
        patientsList.value = PatientManager.findAll()
    }
}