package com.mobile.communihealthv2.fragments

import android.os.Bundle
import android.view.*
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.mobile.communihealthv2.R
import com.mobile.communihealthv2.databinding.FragmentPatientBinding
import com.mobile.communihealthv2.main.Communihealthv2App
import com.mobile.communihealthv2.models.PatientModel
import timber.log.Timber

class PatientFragment : Fragment() {

    lateinit var app: Communihealthv2App
    private var _fragBinding: FragmentPatientBinding? = null
    private val fragBinding get() = _fragBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as Communihealthv2App
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentPatientBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.action_addpatient)

        setButtonListener(fragBinding)

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PatientFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    private fun setButtonListener(layout: FragmentPatientBinding) {
        layout.savePatientButton.setOnClickListener {
            val selectedRadioButtonId = layout.category.checkedRadioButtonId

            if (selectedRadioButtonId != -1) {
                val patientNumber = layout.patientNumber.text.toString()
                val firstName = layout.firstName.text.toString()
                val lastName = layout.lastName.text.toString()
                val birthDate = layout.birthDate.text.toString()
                val eircode = layout.eircode.text.toString()
                val selectedRadioButton = layout.root.findViewById<RadioButton>(selectedRadioButtonId)
                val category = selectedRadioButton?.text.toString()

                app.patientsStore.create(
                    PatientModel(
                        patientNumber = patientNumber,
                        firstName = firstName,
                        lastName = lastName,
                        birthDate = birthDate,
                        eircode = eircode,
                        category = category
                    )
                )
                Timber.i("Patient Data: $patientNumber, $firstName, $lastName, $birthDate, $eircode, $category")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_patient, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
            requireView().findNavController()) || super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    override fun onResume() {
        super.onResume()
        // Additional onResume logic if needed
    }
}
