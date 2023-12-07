package com.mobile.communihealthv2.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.communihealthv2.R
import com.mobile.communihealthv2.databinding.CardPatientlistBinding
import com.mobile.communihealthv2.models.PatientModel

class PatientAdapter constructor( private var patients: List<PatientModel>)
    : RecyclerView.Adapter<PatientAdapter.MainHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardPatientlistBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }
    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val patient = patients[holder.adapterPosition]
        holder.bind(patient)
        }
    override fun getItemCount(): Int = patients.size

    inner class MainHolder(val binding: CardPatientlistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(patient: PatientModel) {
            binding.patientNumber.text = patient.patientNumber
            binding.firstName.text = patient.firstName
            binding.lastName.text = patient.lastName
            binding.category.text = patient.category
            binding.imageIcon.setImageResource(R.mipmap.ic_launcher_round)
        }
    }
}
