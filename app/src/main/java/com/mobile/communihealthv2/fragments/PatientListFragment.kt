package com.mobile.communihealthv2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.communihealthv2.R
import com.mobile.communihealthv2.adapters.PatientAdapter
import com.mobile.communihealthv2.databinding.FragmentPatientListBinding
import com.mobile.communihealthv2.main.Communihealthv2App

class PatientListFragment : Fragment() {

    lateinit var app: Communihealthv2App
    private var _fragBinding: FragmentPatientListBinding? = null
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
        _fragBinding = FragmentPatientListBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.patientlistTitle)

        fragBinding.recyclerView.setLayoutManager(LinearLayoutManager(activity))
        fragBinding.recyclerView.adapter = PatientAdapter(app.patientsStore.findAll())

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_patientlist, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
            requireView().findNavController()) || super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PatientListFragment().apply {
                arguments = Bundle().apply { }
            }
        }
    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}

