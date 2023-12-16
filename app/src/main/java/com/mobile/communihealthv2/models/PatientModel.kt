package com.mobile.communihealthv2.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class PatientModel (var patientId: Long = 0,
                         var patientNumber: String ="",
                         var firstName: String = "",
                         var lastName: String = "",
                         var birthDate: String = "",
                         var eircode: String = "",
                         var category: String = "",
                         var uid: String? ="",
                         var email: String? = "joe@bloggs.com"
                         ): Parcelable
{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "patientId" to patientId,
            "firstName" to firstName,
            "lastName" to lastName,
            "birthDate" to birthDate,
            "eircode" to eircode,
            "category" to category,
            "email" to email
        )
    }
}