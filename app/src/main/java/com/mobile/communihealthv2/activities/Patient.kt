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
import timber.log.Timber

class Patient : AppCompatActivity() {
    private lateinit var patientLayout: ActivityPatientBinding
    lateinit var app: Communihealthv2App
    private var categoryCount = mutableMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        patientLayout = ActivityPatientBinding.inflate(layoutInflater)
        setContentView(patientLayout.root)
        app = this.application as Communihealthv2App

        patientLayout.savePatientButton.setOnClickListener {

            val selectedRadioButtonId = patientLayout.category.checkedRadioButtonId

            if (selectedRadioButtonId != -1) {

                val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)

                val selectedCategory = selectedRadioButton?.text.toString()

                val currentCount = categoryCount.getOrDefault(selectedCategory, 0)
                categoryCount[selectedCategory] = currentCount + 1

                Timber.i("Selected Category: $selectedCategory")
                Timber.i("$selectedCategory Patient count: ${categoryCount[selectedCategory]}")

            } else {

                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
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
