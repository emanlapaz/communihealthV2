package com.mobile.communihealthv2.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.communihealthv2.R
import com.mobile.communihealthv2.databinding.CardPatientlistBinding
import com.mobile.communihealthv2.models.PatientModel

interface PatientClickListener {
    fun onPatientClick(patient: PatientModel)
}
class PatientAdapter constructor( private var patients: ArrayList<PatientModel>,
                                  private var listener: PatientClickListener)
    : RecyclerView.Adapter<PatientAdapter.MainHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardPatientlistBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }
    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val patient = patients[holder.adapterPosition]
        holder.bind(patient, listener)
    }

    fun removeAt(position: Int) {
      patients.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int = patients.size

    inner class MainHolder(val binding: CardPatientlistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(patient: PatientModel, listener: PatientClickListener) {
            binding.root.tag = patient
            binding.patient = patient
            binding.imageIcon.setImageResource(R.mipmap.ic_launcher_round)
            binding.root.setOnClickListener { listener.onPatientClick(patient)}
            binding.executePendingBindings()
        }
    }
}
