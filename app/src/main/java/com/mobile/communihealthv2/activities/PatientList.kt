package com.mobile.communihealthv2.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.communihealthv2.R
import com.mobile.communihealthv2.adapters.PatientAdapter
import com.mobile.communihealthv2.databinding.ActivityPatientlistBinding
import com.mobile.communihealthv2.main.Communihealthv2App

class PatientList : AppCompatActivity() {

    lateinit var app: Communihealthv2App
    lateinit var patientlistLayout : ActivityPatientlistBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        patientlistLayout = ActivityPatientlistBinding.inflate(layoutInflater)
        setContentView(patientlistLayout.root)

        app = this.application as Communihealthv2App
        patientlistLayout.recyclerView.layoutManager = LinearLayoutManager(this)
        patientlistLayout.recyclerView.adapter = PatientAdapter(app.patientStore.findAll())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_patientlist, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_addpatient-> {
                startActivity(Intent(this, Patient::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}