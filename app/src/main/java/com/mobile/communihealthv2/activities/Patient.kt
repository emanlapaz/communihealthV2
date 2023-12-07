package com.mobile.communihealthv2.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mobile.communihealthv2.R
import com.mobile.communihealthv2.databinding.ActivityPatientBinding
import com.mobile.communihealthv2.main.Communihealthv2App
import com.mobile.communihealthv2.models.PatientModel
import timber.log.Timber

class Patient : AppCompatActivity() {
    private lateinit var patientLayout: ActivityPatientBinding
    lateinit var app: Communihealthv2App


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        patientLayout = ActivityPatientBinding.inflate(layoutInflater)
        setContentView(patientLayout.root)
        app = this.application as Communihealthv2App

        patientLayout.addDetailsButton.setOnClickListener {
            val intent = Intent(this, PatientDetailsActivity::class.java)
            startActivity(intent)
        }

        patientLayout.savePatientButton.setOnClickListener {

            val selectedRadioButtonId = patientLayout.category.checkedRadioButtonId

            if (selectedRadioButtonId != -1) {

                val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
                val selectedCategory = selectedRadioButton?.text.toString()

                val patientNumber = patientLayout.patientNumber.text.toString()
                val firstName = patientLayout.firstName.text.toString()
                val lastName = patientLayout.lastName.text.toString()
                val birthDate = patientLayout.birthDate.text.toString()
                val eircode = patientLayout.eircode.text.toString()

                app.patientsStore.create(PatientModel(
                    patientNumber = patientNumber,
                    firstName = firstName,
                    lastName = lastName,
                    birthDate = birthDate,
                    eircode = eircode,
                    category = selectedCategory
                ))
                Timber.i("Patient Data: $patientNumber, $firstName, $lastName, $birthDate, $eircode, $selectedCategory")
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_patient, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_patientlist -> {
                startActivity(Intent(this, PatientList::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
