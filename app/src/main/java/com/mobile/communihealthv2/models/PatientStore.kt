package com.mobile.communihealthv2.models

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser

interface PatientStore {
    fun findAll(patientsList:
                MutableLiveData<List<PatientModel>>
    )
    fun findAll(userid:String,
                patientsList:
                MutableLiveData<List<PatientModel>>)
    fun findById(userid:String, patientid: String,  //?patientId??
                 patient: MutableLiveData<PatientModel>)
    fun create(firebaseUser: MutableLiveData<FirebaseUser>, patient: PatientModel)
    fun delete(userid:String, patientid: String)
    fun update(userid:String, patientid: String, patient: PatientModel)
}