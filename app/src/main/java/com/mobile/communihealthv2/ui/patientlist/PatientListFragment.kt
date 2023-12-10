package com.mobile.communihealthv2.ui.patientlist


import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mobile.communihealthv2.R
import com.mobile.communihealthv2.adapters.PatientAdapter
import com.mobile.communihealthv2.adapters.PatientClickListener
import com.mobile.communihealthv2.databinding.FragmentPatientListBinding
import com.mobile.communihealthv2.main.Communihealthv2App
import com.mobile.communihealthv2.models.PatientModel

class PatientListFragment : Fragment() , PatientClickListener {

    lateinit var app: Communihealthv2App
    private var _fragBinding: FragmentPatientListBinding? = null
    private val fragBinding get() = _fragBinding!!

    private lateinit var patientListViewModel: PatientListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragBinding = FragmentPatientListBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        setupMenu()
        fragBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
        patientListViewModel = ViewModelProvider(this).get(PatientListViewModel::class.java)
        patientListViewModel.observablePatientsList.observe(viewLifecycleOwner, Observer { patients ->
            patients?.let { render(patients) }
        })
        val fab: FloatingActionButton = fragBinding.fab
        fab.setOnClickListener {
            val action = PatientListFragmentDirections.actionPatientListFragmentToPatientFragment()
            findNavController().navigate(action)
        }
        return root
    }
    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_patientlist, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Validate and handle the selected menu item
                return NavigationUI.onNavDestinationSelected(
                    menuItem,
                    requireView().findNavController()
                )
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun render(patientsList: List<PatientModel>) {
        fragBinding.recyclerView.adapter = PatientAdapter(patientsList,this)
        if (patientsList.isEmpty()) {
            fragBinding.recyclerView.visibility = View.GONE
            fragBinding.patientsNotFound.visibility = View.VISIBLE
        } else {
            fragBinding.recyclerView.visibility = View.VISIBLE
            fragBinding.patientsNotFound.visibility = View.GONE
        }
    }
    override fun onPatientClick(patient: PatientModel) {
        val action = PatientListFragmentDirections.actionPatientListFragmentToPatientDetailFragment(patient.patientId)
        findNavController().navigate(action)
    }

    override fun onResume() {
        super.onResume()
        patientListViewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}