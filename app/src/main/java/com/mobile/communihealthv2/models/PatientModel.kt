package com.mobile.communihealthv2.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class PatientModel (var patientId: Long = 0,
                         var patientNumber: String ="",
                         var firstName: String = "",
                         var lastName: String = "",
                         var birthDate: String = "",
                         var eircode: String = "",
                         var category: String = "",
                         ): Parcelable
