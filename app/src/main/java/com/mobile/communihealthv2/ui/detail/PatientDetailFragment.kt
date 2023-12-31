package com.mobile.communihealthv2.ui.detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.storage.FirebaseStorage
import com.mobile.communihealthv2.R
import com.mobile.communihealthv2.databinding.FragmentPatientDetailBinding
import com.mobile.communihealthv2.ui.auth.LoggedInViewModel
import com.mobile.communihealthv2.ui.patientlist.PatientListViewModel
import com.squareup.picasso.Picasso
import timber.log.Timber

class PatientDetailFragment : Fragment() {

    private lateinit var detailViewModel: PatientDetailViewModel
    private val args by navArgs<PatientDetailFragmentArgs>()
    private var _fragBinding: FragmentPatientDetailBinding? = null
    private val fragBinding get() = _fragBinding!!
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()
    private val patientListViewModel : PatientListViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.i("PatientDetailFragment: onCreateView")
        _fragBinding = FragmentPatientDetailBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        detailViewModel = ViewModelProvider(this).get(PatientDetailViewModel::class.java)

        detailViewModel.observablePatient.observe(viewLifecycleOwner, Observer {
            Timber.i("PatientDetailFragment: Observable patient changed")
            render() })

        fragBinding.category.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.fullcare -> detailViewModel.observablePatient.value?.category = "Full Nursing Care"
                R.id.dementia -> detailViewModel.observablePatient.value?.category = "Dementia Care"
                R.id.convalescent -> detailViewModel.observablePatient.value?.category = "Convalescent Care"
                R.id.homehelp -> detailViewModel.observablePatient.value?.category = "Home Help"
            }
        }

        fragBinding.editPatientButton.setOnClickListener {
            detailViewModel.updatePatient(loggedInViewModel.liveFirebaseUser.value?.uid!!,
                args.patientid, fragBinding.patientvm?.observablePatient!!.value!!)
            findNavController().navigateUp()
        }

        fragBinding.deletePatientButton.setOnClickListener {
            patientListViewModel.delete(
                loggedInViewModel.liveFirebaseUser.value?.email!!,
                detailViewModel.observablePatient.value?.uid!!)

            findNavController().navigateUp()
        }
        return root
    }

    private fun render() {
        Timber.i("PatientDetailFragment: render called")
        val patient = detailViewModel.observablePatient.value
        fragBinding.patientvm = detailViewModel
        if (!patient?.patientImage.isNullOrEmpty()) {
            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(patient!!.patientImage)

            storageReference.downloadUrl.addOnSuccessListener { imageUrl ->
                Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.profile) // Placeholder image
                    .error(R.drawable.profile) // Error image (optional)
                    .fit()
                    .centerInside()
                    .into(fragBinding.patientImageView)
            }.addOnFailureListener { exception ->

                Timber.e(exception, "Failed to load patient image from Firebase Storage")
            }
        }

    }

    override fun onResume() {
        super.onResume()
        Timber.i("PatientDetailFragment: onResume - Patient ID: ${args.patientid}")
        detailViewModel.getPatient(loggedInViewModel.liveFirebaseUser.value?.uid!!,
            args.patientid)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.i("PatientDetailFragment: onDestroyView")
        _fragBinding = null
    }
}
