package com.mobile.communihealthv2.ui.map

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mobile.communihealthv2.R
import com.mobile.communihealthv2.models.PatientModel
import com.mobile.communihealthv2.ui.auth.LoggedInViewModel
import com.mobile.communihealthv2.ui.patientlist.PatientListViewModel
import com.mobile.communihealthv2.utils.createLoader
import com.mobile.communihealthv2.utils.hideLoader
import com.mobile.communihealthv2.utils.showLoader

class MapsFragment : Fragment() {

    private val mapsViewModel: MapsViewModel by activityViewModels()
    private val patientListViewModel: PatientListViewModel by activityViewModels()
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()
    lateinit var loader : AlertDialog


    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        mapsViewModel.map = googleMap
        mapsViewModel.map.isMyLocationEnabled = true
        mapsViewModel.currentLocation.observe(viewLifecycleOwner) {
            val loc = LatLng(
                mapsViewModel.currentLocation.value!!.latitude,
                mapsViewModel.currentLocation.value!!.longitude
            )

            mapsViewModel.map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 14f))
            mapsViewModel.map.uiSettings.isZoomControlsEnabled = true
            mapsViewModel.map.uiSettings.isMyLocationButtonEnabled = true

            patientListViewModel.observablePatientsList.observe(
                viewLifecycleOwner,
                Observer { patients ->
                    patients?.let {
                        render(patients as ArrayList<PatientModel>)
                        hideLoader(loader)
                    }
                })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loader = createLoader(requireActivity())
        setupMenu()
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
    private fun render(patientsList: ArrayList<PatientModel>) {
        var markerColour: Float
        if (!patientsList.isEmpty()) {
            mapsViewModel.map.clear()
            patientsList.forEach {
                markerColour = if(it.email.equals(this.patientListViewModel.liveFirebaseUser.value!!.email))
                    BitmapDescriptorFactory.HUE_AZURE + 5
                else
                    BitmapDescriptorFactory.HUE_RED

                mapsViewModel.map.addMarker(
                    MarkerOptions().position(LatLng(it.latitude!!, it.longitude!!))
                        .title("${it.firstName} ${it.lastName}")
                        .snippet("${it.category} ${it.eircode}")
                        .icon(
                            BitmapDescriptorFactory.defaultMarker(markerColour))
                )            }
        }
    }
    override fun onResume() {
        super.onResume()
        showLoader(loader, "Downloading Patients")
        loggedInViewModel.liveFirebaseUser.observe(viewLifecycleOwner) {
                firebaseUser -> if (firebaseUser != null) {
            patientListViewModel.liveFirebaseUser.value = firebaseUser
            patientListViewModel.load()
        }        }
    }
    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_patientlist, menu)

                val item = menu.findItem(R.id.togglePatients) as MenuItem
                item.setActionView(R.layout.togglebutton_layout)
                val togglePatients: SwitchCompat = item.actionView!!.findViewById(R.id.toggleButton)
                togglePatients.isChecked = false

                togglePatients.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) patientListViewModel.loadAll()
                    else patientListViewModel.load()
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                return NavigationUI.onNavDestinationSelected(menuItem,
                    requireView().findNavController())
            }     }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}