package com.mobile.communihealthv2.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobile.communihealthv2.R
import com.mobile.communihealthv2.databinding.ActivityPatientdetailsBinding

class PatientDetailsActivity : AppCompatActivity() {
    private lateinit var patientDetailsLayout: ActivityPatientdetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        patientDetailsLayout = ActivityPatientdetailsBinding.inflate(layoutInflater)
        setContentView(patientDetailsLayout.root)

        patientDetailsLayout.cancelPatientButton.setOnClickListener {

            finish()
        }
    }
}