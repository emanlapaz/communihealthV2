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

class PatientDetailFragment : Fragment() {

    private lateinit var detailViewModel: PatientDetailViewModel
    private val args by navArgs<PatientDetailFragmentArgs>()
    private var _fragBinding: FragmentPatientDetailBinding? = null
    private val fragBinding get() = _fragBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _fragBinding = FragmentPatientDetailBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        detailViewModel = ViewModelProvider(this).get(PatientDetailViewModel::class.java)
        detailViewModel.observablePatient.observe(viewLifecycleOwner, Observer { render() })
        return root
    }

    private fun render() {
        fragBinding.patientvm = detailViewModel
    }

    override fun onResume() {
        super.onResume()
        detailViewModel.getPatient(args.patientId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}