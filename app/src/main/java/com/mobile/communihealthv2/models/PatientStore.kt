package com.mobile.communihealthv2.models

interface PatientStore {
    fun findAll(): List<PatientModel>
    fun findById(patientId:Long) : PatientModel?
    fun create (patient: PatientModel)
}