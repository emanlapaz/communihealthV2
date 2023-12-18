package com.mobile.communihealthv2.ui.patient

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
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
import com.mobile.communihealthv2.R
import com.mobile.communihealthv2.databinding.FragmentPatientBinding
import com.mobile.communihealthv2.firebase.FirebaseImageManager
import com.mobile.communihealthv2.models.PatientModel
import com.mobile.communihealthv2.ui.auth.LoggedInViewModel
import com.mobile.communihealthv2.ui.map.MapsViewModel
import com.mobile.communihealthv2.ui.patientlist.PatientListViewModel
import timber.log.Timber

class PatientFragment : Fragment() {

    private var _fragBinding: FragmentPatientBinding? = null

    private val fragBinding get() = _fragBinding!!
    val firebaseImageManager = FirebaseImageManager()
    private lateinit var patientViewModel: PatientViewModel
    private val patientListViewModel: PatientListViewModel by activityViewModels()
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()
    private val mapsViewModel: MapsViewModel by activityViewModels()
    private var selectedImageUri: Uri? = null

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
            fragBinding.patientImageView.setImageURI(uri)
        }

        fragBinding.uploadImageButton.setOnClickListener {
            val chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            val intent = Intent.createChooser(chooseFile, getString(R.string.patientImgButton))
            imagePickerLauncher.launch(intent)
        }
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
                Timber.i("PatientFragment: New Patient Data: $firstName, $lastName, $birthDate, $eircode, $category")

                // Upload the image to Firebase Storage
                val imageBitmap = (fragBinding.patientImageView.drawable as BitmapDrawable).bitmap
                firebaseImageManager.uploadImageToFirebase(
                    loggedInViewModel.liveFirebaseUser.value?.uid ?: "",
                    imageBitmap,
                    true
                ) { imageUrl ->
                    // Callback when image upload is complete
                    if (imageUrl != null) {
                        // Save the patient details with the image URL
                        patientViewModel.addPatient(
                            loggedInViewModel.liveFirebaseUser, PatientModel(
                                firstName = firstName,
                                lastName = lastName,
                                birthDate = birthDate,
                                eircode = eircode,
                                category = category,
                                email = loggedInViewModel.liveFirebaseUser.value?.email!!,
                                patientImage = imageUrl, // Include the image URL in the patient model
                                latitude = mapsViewModel.currentLocation.value!!.latitude, //CHECK THIS OUT, ADD NA OPTION TO USE LOCATION AS PATIENT ADDRESS
                                longitude = mapsViewModel.currentLocation.value!!.longitude
                            )
                        )

                        // Navigate to PatientListView after saving the patient
                        findNavController().navigate(R.id.action_patientFragment_to_patientListFragment)
                    } else {
                        // Handle image upload failure if needed
                        // You can display an error message to the user
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