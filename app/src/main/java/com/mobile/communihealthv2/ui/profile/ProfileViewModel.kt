package com.mobile.communihealthv2.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobile.communihealthv2.firebase.FirebaseDBManager
import com.mobile.communihealthv2.models.PatientModel
import timber.log.Timber

class ProfileViewModel : ViewModel() {
    private val patient = MutableLiveData<PatientModel>()

    var observablePatient: LiveData<PatientModel>
        get() = patient
        set(value) {patient.value = value.value}

    fun getPatient(userid:String, id: String) {
        try {
            FirebaseDBManager.findById(userid, id, patient)
            Timber.i("Detail getPatient() Success : ${
                patient.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("Detail getPatient() Error : $e.message")
        }
    }
    fun updatePatient(userid:String, id: String, patient: PatientModel) {
        try {
            FirebaseDBManager.update(userid, id, patient)
            Timber.i("Detail update() Success : $patient")
        } catch (e: Exception) {
            Timber.i("Detail update() Error : $e.message")
        }
    }
}