package com.mobile.communihealthv2.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mobile.communihealthv2.databinding.ActivityPatientBinding
import com.mobile.communihealthv2.main.Communihealthv2App

class Patient : AppCompatActivity() {
    private lateinit var patientLayout: ActivityPatientBinding
    lateinit var app: Communihealthv2App

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        patientLayout = ActivityPatientBinding.inflate(layoutInflater)
        setContentView(patientLayout.root)
        app = this.application as Communihealthv2App
    }
}
