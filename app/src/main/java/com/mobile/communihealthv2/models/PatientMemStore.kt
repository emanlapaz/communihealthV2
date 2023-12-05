package com.mobile.communihealthv2.models

import timber.log.Timber

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}
class PatientMemStore: PatientStore {

    val patients = ArrayList<PatientModel>()

    override fun findAll(): List<PatientModel> {
        return patients
    }

    override fun findById(id: Long): PatientModel? {
        val foundPatient: PatientModel? = patients.find { it.patientId == id }
        return foundPatient
    }

    override fun create(patient: PatientModel) {
        patient.patientId = getId()
        patients.add(patient)
        logAll()
    }

    fun logAll() {
        Timber.v("** Patients List **")
        patients.forEach { Timber.v("Patient ${it}") }
    }
}
