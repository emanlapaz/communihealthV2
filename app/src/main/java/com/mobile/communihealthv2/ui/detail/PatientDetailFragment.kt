package com.mobile.communihealthv2.ui.detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.mobile.communihealthv2.R
import com.mobile.communihealthv2.databinding.FragmentPatientDetailBinding
import timber.log.Timber

class PatientDetailFragment : Fragment() {

    private lateinit var detailViewModel: PatientDetailViewModel
    private val args by navArgs<PatientDetailFragmentArgs>()
    private var _fragBinding: FragmentPatientDetailBinding? = null
    private val fragBinding get() = _fragBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.i("PatientDetailFragment: onCreateView")
        _fragBinding = FragmentPatientDetailBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        // Initialize ViewModel
        detailViewModel = ViewModelProvider(this).get(PatientDetailViewModel::class.java)

        // Observe the LiveData
        detailViewModel.observablePatient.observe(viewLifecycleOwner, Observer {
            Timber.i("PatientDetailFragment: Observable patient changed")
            render()
        })

        // Set up RadioGroup listener
        fragBinding.category.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.fullcare -> detailViewModel.observablePatient.value?.category = "Full Nursing Care"
                R.id.dementia -> detailViewModel.observablePatient.value?.category = "Dementia Care"
                R.id.convalescent -> detailViewModel.observablePatient.value?.category = "Convalescent Care"
                R.id.homehelp -> detailViewModel.observablePatient.value?.category = "Home Help"
            }
        }

        // Load patient data
        val patientId = args.patientId
        Timber.i("PatientDetailFragment: Received patient ID - $patientId")
        detailViewModel.getPatient(patientId)

        return root
    }

    private fun render() {
        Timber.i("PatientDetailFragment: render called")
        fragBinding.patient = detailViewModel.observablePatient.value
    }

    override fun onResume() {
        super.onResume()
        Timber.i("PatientDetailFragment: onResume - Patient ID: ${args.patientId}")
        detailViewModel.getPatient(args.patientId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.i("PatientDetailFragment: onDestroyView")
        _fragBinding = null
    }
}
