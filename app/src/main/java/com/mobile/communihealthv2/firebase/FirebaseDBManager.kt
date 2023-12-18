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
                        snapshot.children.forEach { child ->
                            val patient = child.getValue(PatientModel::class.java)
                            patient?.let { localList.add(it) }
                        }
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
                        snapshot.children.forEach { child ->
                            val patient = child.getValue(PatientModel::class.java)
                            patient?.let { localList.add(it) }
                        }
                        patientsList.value = localList
                    }
                })
        }

        override fun findById(
            userid: String,
            patientid: String,
            patient: MutableLiveData<PatientModel>
        ) {
            database.child("user-patients").child(userid).child(patientid).get()
                .addOnSuccessListener { dataSnapshot ->
                    patient.value = dataSnapshot.getValue(PatientModel::class.java)
                }
                .addOnFailureListener { exception ->
                    Timber.e("firebase Error getting data $exception")
                }
        }

        override fun create(firebaseUser: MutableLiveData<FirebaseUser>, patient: PatientModel) {
            val uid = firebaseUser.value?.uid ?: return
            val key = database.child("patients").push().key ?: run {
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
            val childDelete: MutableMap<String, Any?> = HashMap()
            childDelete["/patients/$patientid"] = null
            childDelete["/user-patients/$userid/$patientid"] = null

            database.updateChildren(childDelete)
        }

        override fun update(userid: String, patientid: String, patient: PatientModel) {
            val patientValues = patient.toMap()

            val childUpdate: MutableMap<String, Any?> = HashMap()
            childUpdate["patients/$patientid"] = patientValues
            childUpdate["user-patients/$userid/$patientid"] = patientValues

            database.updateChildren(childUpdate)
        }
    }

