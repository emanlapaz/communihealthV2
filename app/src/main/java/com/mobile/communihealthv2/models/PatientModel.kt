package com.mobile.communihealthv2.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class PatientModel (var patientId: Long = 0,
                         var firstName: String = "",
                         var lastName: String = "",
                         var birthDate: Date,
                         var town: String = "",
                         var eircode: String = "",
                         var street: String = "",
                         var category: String = "",
                         ): Parcelable
