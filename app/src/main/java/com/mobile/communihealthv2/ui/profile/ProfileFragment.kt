package com.mobile.communihealthv2.ui.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.google.firebase.storage.FirebaseStorage
import com.mobile.communihealthv2.R
import com.mobile.communihealthv2.databinding.FragmentProfileBinding
import com.mobile.communihealthv2.models.PatientModel
import com.mobile.communihealthv2.ui.auth.LoggedInViewModel
import com.squareup.picasso.Picasso
import timber.log.Timber

class ProfileFragment : Fragment() {

    private lateinit var detailViewModel: ProfileViewModel
    private val args by navArgs<ProfileFragmentArgs>() // Add this line
    private var _fragBinding: FragmentProfileBinding? = null
    private val fragBinding get() = _fragBinding!!
    private val loggedInViewModel: LoggedInViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentProfileBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        // Initialize ViewModel
        detailViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        // Observe the LiveData
        detailViewModel.observablePatient.observe(viewLifecycleOwner, Observer { patient ->
            render(patient)
        })
        return root
    }

    private fun render(patient: PatientModel?) {
        Timber.i("ProfileFragment: render called")
        fragBinding.patientvm = detailViewModel
        if (!patient?.patientImage.isNullOrEmpty()) {
            val storageReference =
                FirebaseStorage.getInstance().getReferenceFromUrl(patient!!.patientImage)

            storageReference.downloadUrl.addOnSuccessListener { imageUrl ->
                Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.profile) // Placeholder image
                    .error(R.drawable.profile) // Error image (optional)
                    .fit()
                    .centerInside()
                    .into(fragBinding.patientImageView)
            }.addOnFailureListener { exception ->
                // Handle failure to load image
                Timber.e(exception, "Failed to load patient image from Firebase Storage")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        detailViewModel.getPatient(
            loggedInViewModel.liveFirebaseUser.value?.uid!!,
            args.patientid
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}
