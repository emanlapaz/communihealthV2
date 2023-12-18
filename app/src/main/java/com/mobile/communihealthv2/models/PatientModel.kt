package com.mobile.communihealthv2.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class PatientModel (
                         var firstName: String = "",
                         var lastName: String = "",
                         var birthDate: String = "",
                         var eircode: String = "",
                         var category: String = "",
                         var patientImage: String = "",
                         var uid: String? ="",
                         var email: String? = "joe@bloggs.com",
                         var latitude: Double? = 0.0,
                         var longitude: Double? = 0.0
                         ): Parcelable
{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "firstName" to firstName,
            "lastName" to lastName,
            "birthDate" to birthDate,
            "eircode" to eircode,
            "category" to category,
            "patientImage" to patientImage,
            "email" to email,
            "latitude" to latitude,
            "longitude" to longitude
        )
    }
}