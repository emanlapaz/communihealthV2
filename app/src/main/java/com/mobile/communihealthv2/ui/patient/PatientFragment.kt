package com.mobile.communihealthv2.ui.patient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.mobile.communihealthv2.R
import com.mobile.communihealthv2.databinding.FragmentPatientBinding
import com.mobile.communihealthv2.models.PatientModel
import com.mobile.communihealthv2.ui.auth.LoggedInViewModel
import com.mobile.communihealthv2.ui.patientlist.PatientListViewModel
import timber.log.Timber

class PatientFragment : Fragment() {

    private var _fragBinding: FragmentPatientBinding? = null

    private val fragBinding get() = _fragBinding!!

    private lateinit var patientViewModel: PatientViewModel
    private val patientListViewModel: PatientListViewModel by activityViewModels()
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("PatientFragment: onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Timber.i("PatientFragment: onCreateView")
        _fragBinding = FragmentPatientBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        setupMenu()

        patientViewModel =
            ViewModelProvider(this).get(PatientViewModel::class.java)

        patientViewModel.observableStatus.observe(viewLifecycleOwner, Observer {
            status -> status?.let {render(status)
            Timber.i("PatientFragment: Observable status changed - $status")}
        })
        setButtonListener(fragBinding)
        return root
    }

    private fun render(status: Boolean) {
        when (status) {
            true -> {
                view?.let {
                }
            }
            false -> Toast.makeText(context,getString(R.string.patientError),Toast.LENGTH_LONG).show()
        }
    }

   fun setButtonListener(layout: FragmentPatientBinding) {
        layout.savePatientButton.setOnClickListener {
            Timber.i("PatientFragment: Save button clicked")
            val selectedRadioButtonId = layout.category.checkedRadioButtonId

            if (selectedRadioButtonId != -1) {
                val patientNumber = layout.patientNumber.text.toString()
                val firstName = layout.firstName.text.toString()
                val lastName = layout.lastName.text.toString()
                val birthDate = layout.birthDate.text.toString()
                val eircode = layout.eircode.text.toString()
                val selectedRadioButton =
                    layout.root.findViewById<RadioButton>(selectedRadioButtonId)
                val category = selectedRadioButton?.text.toString()
                Timber.i("PatientFragment: New Patient Data: $patientNumber, $firstName, $lastName, $birthDate, $eircode, $category")

                patientViewModel.addPatient(
                    loggedInViewModel.liveFirebaseUser, PatientModel(
                        patientNumber = patientNumber,
                        firstName = firstName,
                        lastName = lastName,
                        birthDate = birthDate,
                        eircode = eircode,
                        category = category,
                        email = loggedInViewModel.liveFirebaseUser.value?.email!!
                    )
                )
            }
        }
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_patient, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Validate and handle the selected menu item
                return NavigationUI.onNavDestinationSelected(menuItem,
                    requireView().findNavController())
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Timber.i("PatientFragment: onDestroyView")
        _fragBinding = null
    }

    override fun onResume() {
        super.onResume()
        Timber.i("PatientFragment: onResume")
        val patientListViewModel = ViewModelProvider(this).get(PatientListViewModel::class.java)
        patientListViewModel.observablePatientsList.observe(viewLifecycleOwner, Observer {
        })
    }
}