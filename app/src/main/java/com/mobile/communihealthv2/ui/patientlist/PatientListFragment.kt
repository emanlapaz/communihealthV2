package com.mobile.communihealthv2.ui.patientlist


import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mobile.communihealthv2.R
import com.mobile.communihealthv2.adapters.PatientAdapter
import com.mobile.communihealthv2.adapters.PatientClickListener
import com.mobile.communihealthv2.databinding.FragmentPatientListBinding
import com.mobile.communihealthv2.main.Communihealthv2App
import com.mobile.communihealthv2.models.PatientModel
import com.mobile.communihealthv2.ui.auth.LoggedInViewModel
import com.mobile.communihealthv2.utils.SwipeToDeleteCallback
import com.mobile.communihealthv2.utils.SwipeToEditCallback
import com.mobile.communihealthv2.utils.createLoader
import com.mobile.communihealthv2.utils.hideLoader
import com.mobile.communihealthv2.utils.showLoader
import timber.log.Timber

class PatientListFragment : Fragment() , PatientClickListener {

    lateinit var app: Communihealthv2App
    private var _fragBinding: FragmentPatientListBinding? = null
    private val fragBinding get() = _fragBinding!!
    lateinit var loader: AlertDialog
    private val patientListViewModel: PatientListViewModel by activityViewModels()
    private val loggedInViewModel: LoggedInViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("PatientListFragment: onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i("PatientListFragment: onCreateView")
        _fragBinding = FragmentPatientListBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        setupMenu()
        loader = createLoader(requireActivity())

        fragBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
        fragBinding.fab.setOnClickListener {
            val action = PatientListFragmentDirections.actionPatientListFragmentToPatientFragment()
            findNavController().navigate(action)
        }

        showLoader(loader, "Downloading Patients")
        patientListViewModel.observablePatientsList.observe(viewLifecycleOwner, Observer { patients ->
            patients?.let {
                render(patients as ArrayList<PatientModel>)
                hideLoader(loader)
                checkSwipeRefresh()
            }
        })
        setSwipeRefresh()

        val swipeDeleteHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showLoader(loader, "Deleting Patient")
                val adapter = fragBinding.recyclerView.adapter as PatientAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                patientListViewModel.delete(
                    patientListViewModel.liveFirebaseUser.value?.uid!!,
                    (viewHolder.itemView.tag as PatientModel).uid!!
                )

                hideLoader(loader)
            }
        }
        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(fragBinding.recyclerView)

        val swipeEditHandler = object : SwipeToEditCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val patient = viewHolder.itemView.tag as PatientModel
                val action = PatientListFragmentDirections.actionPatientListFragmentToPatientDetailFragment(patient.uid!!)
                findNavController().navigate(action)
            }
        }

        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchEditHelper.attachToRecyclerView(fragBinding.recyclerView)
        return root
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_patientlist, menu)

                val item = menu.findItem(R.id.togglePatients) as MenuItem
                item.setActionView(R.layout.togglebutton_layout)
                val togglePatients: SwitchCompat = item.actionView!!.findViewById(R.id.toggleButton)
                togglePatients.isChecked = false

                togglePatients.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        patientListViewModel.loadAll()
                        Toast.makeText(requireContext(), "Viewing all patients...", Toast.LENGTH_SHORT).show()
                    } else {
                        patientListViewModel.load()
                        Toast.makeText(requireContext(), "Loading patients...", Toast.LENGTH_SHORT).show()
                    }
                }
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

    private fun render(patientsList: ArrayList<PatientModel>) {
        Timber.i("PatientListFragment: render - Patients list size: ${patientsList.size}")
        fragBinding.recyclerView.adapter = PatientAdapter(patientsList, this, patientListViewModel.readOnly.value!!)
        if (patientsList.isEmpty()) {
            fragBinding.recyclerView.visibility = View.GONE
            fragBinding.patientsNotFound.visibility = View.VISIBLE
        } else {
            fragBinding.recyclerView.visibility = View.VISIBLE
            fragBinding.patientsNotFound.visibility = View.GONE
        }
    }

    override fun onPatientClick(patient: PatientModel) {
        val action =
            PatientListFragmentDirections.actionPatientListFragmentToProfileFragment(patient.uid!!)
        findNavController().navigate(action)
    }


    private fun setSwipeRefresh() {
        fragBinding.swiperefresh.setOnRefreshListener {
            fragBinding.swiperefresh.isRefreshing = true
            showLoader(loader, "Downloading Patients")
            if(patientListViewModel.readOnly.value!!)
                patientListViewModel.loadAll()

            else
                patientListViewModel.load()
        }
    }

    private fun checkSwipeRefresh() {
        if (fragBinding.swiperefresh.isRefreshing)
            fragBinding.swiperefresh.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()
        showLoader(loader, "Downloading Patients")
        loggedInViewModel.liveFirebaseUser.observe(viewLifecycleOwner, Observer { firebaseUser ->
            if (firebaseUser != null) {
                patientListViewModel.liveFirebaseUser.value = firebaseUser
                patientListViewModel.load()
            }
        })
    }
        override fun onDestroyView() {
            super.onDestroyView()
            Timber.i("PatientListFragment: onDestroyView")
            _fragBinding = null
        }
    }
