package com.mobile.communihealthv2.ui.patient

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.mobile.communihealthv2.firebase.FirebaseDBManager
import com.mobile.communihealthv2.models.PatientModel

class PatientViewModel : ViewModel() {

    private val status = MutableLiveData<Boolean>()

    val observableStatus: LiveData<Boolean>
        get() = status

    fun addPatient(firebaseUser: MutableLiveData<FirebaseUser>,
                    patient: PatientModel) {
        status.value = try {
            FirebaseDBManager.create(firebaseUser,patient)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}