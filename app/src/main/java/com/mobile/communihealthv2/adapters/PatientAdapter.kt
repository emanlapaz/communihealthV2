package com.mobile.communihealthv2.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.communihealthv2.R
import com.mobile.communihealthv2.databinding.CardPatientlistBinding
import com.mobile.communihealthv2.models.PatientModel
import timber.log.Timber

class PatientAdapter(private var patients: List<PatientModel>) :
    RecyclerView.Adapter<PatientAdapter.MainHolder>() {

    // Update the list of patients when needed
    fun updatePatients(updatedPatients: List<PatientModel>) {
        patients = updatedPatients
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardPatientlistBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val patient = patients[position]
        holder.bind(patient)
    }

    override fun getItemCount(): Int = patients.size

    inner class MainHolder(private val binding: CardPatientlistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(patient: PatientModel) {
            // Assuming PatientModel has a category property
            binding.category.text = patient.category
            binding.imageIcon.setImageResource(R.mipmap.ic_launcher_round)
        }
    }
}
