package com.mobile.communihealthv2.ui.patient

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.mobile.communihealthv2.firebase.FirebaseDBManager
import com.mobile.communihealthv2.models.PatientModel

class PatientViewModel : ViewModel() {

    private val _selectedImageUri = MutableLiveData<Uri>()
    val selectedImageUri: LiveData<Uri> = _selectedImageUri
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
    fun onImageSelected(uri: Uri) {
        _selectedImageUri.value = uri
        // Additional processing if needed
    }

}