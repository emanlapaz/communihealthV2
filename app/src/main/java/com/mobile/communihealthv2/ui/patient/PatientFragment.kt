package com.mobile.communihealthv2.ui.patient

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.material.snackbar.Snackbar
import com.mobile.communihealthv2.R
import com.mobile.communihealthv2.databinding.FragmentPatientBinding
import com.mobile.communihealthv2.firebase.FirebaseImageManager
import com.mobile.communihealthv2.models.PatientModel
import com.mobile.communihealthv2.ui.auth.LoggedInViewModel
import com.mobile.communihealthv2.ui.map.MapsViewModel
import com.mobile.communihealthv2.ui.patientlist.PatientListViewModel
import com.mobile.communihealthv2.utils.calculateAge
import com.mobile.communihealthv2.utils.isFirstNameValid
import com.mobile.communihealthv2.utils.isLastNameValid
import com.mobile.communihealthv2.utils.isValidDate
import com.mobile.communihealthv2.utils.showSnackbar
import timber.log.Timber
import java.io.IOException
import java.util.Locale

class PatientFragment : Fragment() {

    private var _fragBinding: FragmentPatientBinding? = null

    private val fragBinding get() = _fragBinding!!
    val firebaseImageManager = FirebaseImageManager()
    private lateinit var patientViewModel: PatientViewModel
    private val patientListViewModel: PatientListViewModel by activityViewModels()
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()
    private val mapsViewModel: MapsViewModel by activityViewModels()
    private var selectedImageUri: Uri? = null
    private var patientLatitude: Double = 53.21583
    private var patientLongitude: Double = 53.21583 -6.66694

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                patientViewModel.onImageSelected(uri)
            }
        }
    }


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

        patientViewModel.selectedImageUri.observe(viewLifecycleOwner) { uri ->
            fragBinding.uploadImageButton.setImageURI(uri)
        }

        fragBinding.uploadImageButton.setOnClickListener {
            val chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            val intent = Intent.createChooser(chooseFile, getString(R.string.patientImgButton))
            imagePickerLauncher.launch(intent)
        }

        fragBinding.uploadLocationButton.setOnClickListener {
            setLocation()
        }

        fragBinding.searchEircode.setOnClickListener {
           searchEircode()
        }

        setButtonListener(fragBinding)
        return root
    }

    private fun setLocation() {
        mapsViewModel.currentLocation.value?.let { location ->
            patientLatitude = location.latitude
            patientLongitude = location.longitude

            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            try {
                val addresses = geocoder.getFromLocation(patientLatitude, patientLongitude, 1)
                if (addresses != null) {
                    if (addresses.isNotEmpty()) {
                        val address = addresses[0]

                        patientViewModel.patientHouseNumber.value = address.subThoroughfare
                        patientViewModel.patientRoad.value = address.thoroughfare
                        patientViewModel.patientTown.value = address.locality
                        patientViewModel.patientCounty.value = address.adminArea

                        Toast.makeText(context, "Patient Address set to Current Location", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                Toast.makeText(context, "Unable to get address from location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchEircode() {
        val eircode = fragBinding.eircode.text.toString()
        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        try {
            val addresses = geocoder.getFromLocationName(eircode, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    patientLatitude = address.latitude
                    patientLongitude = address.longitude

                    // Update ViewModel with address details
                    patientViewModel.patientHouseNumber.value = address.subThoroughfare
                    patientViewModel.patientRoad.value = address.thoroughfare
                    patientViewModel.patientTown.value = address.locality
                    patientViewModel.patientCounty.value = address.adminArea

                    Toast.makeText(context, "Location set from Eircode: $eircode, ${address.adminArea} ", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Eircode Search Failed", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: IOException) {
            Toast.makeText(context, "Error fetching address from Eircode", Toast.LENGTH_SHORT).show()
        }
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

    private fun setButtonListener(layout: FragmentPatientBinding) {
        layout.savePatientButton.setOnClickListener {
            Timber.i("PatientFragment: Save button clicked")
            val selectedRadioButtonId = layout.category.checkedRadioButtonId

            if (selectedRadioButtonId != -1) {
                val firstName = layout.firstName.text.toString()
                val lastName = layout.lastName.text.toString()
                val birthDate = layout.birthDate.text.toString()
                val eircode = layout.eircode.text.toString()
                val selectedRadioButton = layout.root.findViewById<RadioButton>(selectedRadioButtonId)
                val category = selectedRadioButton?.text.toString()
                val town = patientViewModel.patientTown.value ?: ""
                val county = patientViewModel.patientCounty.value ?: ""
                val road = patientViewModel.patientRoad.value ?: ""
                val houseNumber = patientViewModel.patientHouseNumber.value ?: ""


                if (firstName.isEmpty() || lastName.isEmpty() || birthDate.isEmpty()) {
                    showSnackbar(layout.root, "Please fill in all required fields.", Snackbar.LENGTH_LONG)
                    return@setOnClickListener
                }
                if (!isFirstNameValid(firstName)) {
                    showSnackbar(layout.root, "First name should contain only letters.", Snackbar.LENGTH_LONG)
                    return@setOnClickListener
                }

                if (!isLastNameValid(lastName)) {
                    showSnackbar(layout.root, "Last name should contain only letters.", Snackbar.LENGTH_LONG)
                    return@setOnClickListener
                }

                if (!isValidDate(birthDate)) {
                    showSnackbar(layout.root, "Invalid date format (dd/mm/yyyy)", Snackbar.LENGTH_LONG)
                    return@setOnClickListener
                }

                val age = calculateAge(birthDate)
                if (age == null) {
                    showSnackbar(layout.root, "Input Birthdate", Snackbar.LENGTH_LONG)
                    return@setOnClickListener
                }
                // Upload the image to Firebase Storage
                val imageBitmap = (fragBinding.uploadImageButton.drawable as BitmapDrawable).bitmap

                firebaseImageManager.uploadImageToFirebase(
                    loggedInViewModel.liveFirebaseUser.value?.uid ?: "",
                    imageBitmap) { imageUrl ->
                    if (imageUrl != null) {
                        patientViewModel.addPatient(
                            loggedInViewModel.liveFirebaseUser, PatientModel(
                                firstName = firstName,
                                lastName = lastName,
                                birthDate = birthDate,
                                eircode = eircode,
                                category = category,
                                email = loggedInViewModel.liveFirebaseUser.value?.email!!,
                                patientImage = imageUrl,
                                latitude = patientLatitude,
                                longitude = patientLongitude,
                                road = road,
                                town = town,
                                county = county,
                                houseNumber = houseNumber,
                                age = age
                            )
                        )
                        findNavController().navigate(R.id.action_patientFragment_to_patientListFragment)
                        Timber.i("PatientFragment: New Patient Data: AGE: $age")
                    } else {

                    }
                }
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