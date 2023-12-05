package com.mobile.communihealthv2.main

import android.app.Application
import com.mobile.communihealthv2.models.PatientMemStore
import com.mobile.communihealthv2.models.PatientStore
import timber.log.Timber

class Communihealthv2App : Application() {

    lateinit var patientStore: PatientStore
    override fun onCreate(){
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        patientStore = PatientMemStore()
        Timber.i("Starting Communihealthv2 Application")
    }
}