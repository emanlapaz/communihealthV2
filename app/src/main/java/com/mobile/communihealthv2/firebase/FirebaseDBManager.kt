package com.mobile.communihealthv2.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mobile.communihealthv2.firebase.FirebaseDBManager.database
import com.mobile.communihealthv2.models.PatientModel
import com.mobile.communihealthv2.models.PatientStore
import timber.log.Timber

object FirebaseDBManager : PatientStore {

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun findAll(patientsList: MutableLiveData<List<PatientModel>>) {
        database.child("patients")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Patient error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<PatientModel>()
                    val children = snapshot.children
                    children.forEach {
                        val patient = it.getValue(PatientModel::class.java)
                        localList.add(patient!!)
                    }
                    database.child("patients")
                        .removeEventListener(this)

                    patientsList.value = localList
                }
            })
    }

    override fun findAll(userid: String, patientsList: MutableLiveData<List<PatientModel>>) {

        database.child("user-patients").child(userid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Patient error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<PatientModel>()
                    val children = snapshot.children
                    children.forEach {
                        val patient = it.getValue(PatientModel::class.java)
                            localList.add(patient!!)
                    }
                    database.child("user-patients").child(userid)
                        .removeEventListener(this)

                    patientsList.value = localList
                }
            })
    }

    override fun findById(userid: String, patientid: String, patient: MutableLiveData<PatientModel>) {

        database.child("user-patients").child(userid)
            .child(patientid).get().addOnSuccessListener {
                patient.value = it.getValue(PatientModel::class.java)
                Timber.i("firebase Got value ${it.value}")
            }.addOnFailureListener{
                Timber.e("firebase Error getting data $it")
            }
    }

    override fun create(firebaseUser: MutableLiveData<FirebaseUser>, patient: PatientModel) {
        Timber.i("Firebase DB Reference : $database")

        val uid = firebaseUser.value!!.uid
        val key = database.child("patients").push().key
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        patient.uid = key
        val patientValues = patient.toMap()

        val childAdd = HashMap<String, Any>()
        childAdd["/patients/$key"] = patientValues
        childAdd["/user-patients/$uid/$key"] = patientValues

        database.updateChildren(childAdd)
    }

    override fun delete(userid: String, patientid: String) {

        val childDelete : MutableMap<String, Any?> = HashMap()
        childDelete["/patients/$patientid"] = null
        childDelete["/user-patients/$userid/$patientid"] = null

        database.updateChildren(childDelete)
    }

    override fun update(userid: String, patientid: String, patient: PatientModel) {

        val patientValues = patient.toMap()

        val childUpdate : MutableMap<String, Any?> = HashMap()
        childUpdate["patients/$patientid"] = patientValues
        childUpdate["user-patients/$userid/$patientid"] = patientValues

        database.updateChildren(childUpdate)
    }
}

fun updateImageRef(userid: String,imageUri: String) {

    val userPatients = database.child("user-patients").child(userid)
    val allPatients = database.child("patients")

    userPatients.addListenerForSingleValueEvent(
        object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    //Update Users imageUri
                    it.ref.child("patientImage").setValue(imageUri)
                    //Update all donations that match 'it'
                    val patient = it.getValue(PatientModel::class.java)
                    allPatients.child(patient!!.uid!!)
                        .child("patientImage").setValue(imageUri)
                }
            }
        })
}