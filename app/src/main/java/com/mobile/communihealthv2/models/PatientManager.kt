package com.mobile.communihealthv2.models

import timber.log.Timber

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}
object PatientManager: PatientStore {

    private val patients = ArrayList<PatientModel>()

    override fun findAll(): List<PatientModel> {
        return patients
    }

    override fun findById(patientId: Long): PatientModel? {
        val foundPatient: PatientModel? = patients.find { it.patientId == patientId } //? ID
        return foundPatient
    }

    override fun create(patient: PatientModel) {
        patient.patientId = getId()
        patients.add(patient)
        Timber.i("Patient added: $patient")
        logAll()
    }


    fun logAll() {
        Timber.v("** Patients List **")
        patients.forEach { Timber.v("Patient ${it}") }
    }
}
