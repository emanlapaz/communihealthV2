package com.mobile.communihealthv2.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.mobile.communihealthv2.R
import com.mobile.communihealthv2.databinding.CardPatientlistBinding
import com.mobile.communihealthv2.models.PatientModel
import com.mobile.communihealthv2.utils.customTransformation
import com.squareup.picasso.Picasso

interface PatientClickListener {
    fun onPatientClick(patient: PatientModel)
}
class PatientAdapter constructor( private var patients: ArrayList<PatientModel>,
                                  private var listener: PatientClickListener,
                                  private val readOnly: Boolean)
    : RecyclerView.Adapter<PatientAdapter.MainHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardPatientlistBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding, readOnly)
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

    inner class MainHolder(val binding: CardPatientlistBinding, private val readOnly : Boolean) :
        RecyclerView.ViewHolder(binding.root) {

        val readOnlyRow = readOnly
        fun bind(patient: PatientModel, listener: PatientClickListener) {
            binding.root.tag = patient
            binding.patient = patient
            Picasso.get().load(patient.patientImage.toUri())
                .resize(200, 200)
                .transform(customTransformation())
                .centerCrop()
                .into(binding.patientImageView)
            binding.root.setOnClickListener { listener.onPatientClick(patient)}
            binding.executePendingBindings()
        }
    }
}
