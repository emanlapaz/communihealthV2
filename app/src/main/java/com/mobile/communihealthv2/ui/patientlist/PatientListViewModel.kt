package com.mobile.communihealthv2.ui.patientlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.mobile.communihealthv2.firebase.FirebaseDBManager
import com.mobile.communihealthv2.models.PatientModel
import timber.log.Timber

class PatientListViewModel : ViewModel() {

    private val patientsList = MutableLiveData<List<PatientModel>>()

    val observablePatientsList: LiveData<List<PatientModel>>
        get() = patientsList

    var liveFirebaseUser = MutableLiveData<FirebaseUser>()

    init{
        load()
    }
    fun load() {
        try {
            //DonationManager.findAll(liveFirebaseUser.value?.email!!, donationsList)
            FirebaseDBManager.findAll(liveFirebaseUser.value?.uid!!,patientsList)
            Timber.i("PatientList Load Success : ${patientsList.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("PatientList Load Error : $e.message")
        }
    }

    fun delete(userid: String, id: String) {
        try {
            FirebaseDBManager.delete(userid,id)
            Timber.i("Report Delete Success")
        }
        catch (e: Exception) {
            Timber.i("Report Delete Error : $e.message")
        }
    }
}
