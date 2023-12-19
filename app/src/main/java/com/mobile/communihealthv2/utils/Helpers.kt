package com.mobile.communihealthv2.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.icu.util.Calendar
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.mobile.communihealthv2.R
import com.squareup.picasso.Transformation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun createLoader(activity: FragmentActivity) : AlertDialog {
    val loaderBuilder = AlertDialog.Builder(activity)
        .setCancelable(true) // 'false' if you want user to wait
        .setView(R.layout.loading)
    var loader = loaderBuilder.create()
    loader.setTitle(R.string.app_name)
    loader.setIcon(R.mipmap.ic_launcher_round)

    return loader
}

fun showLoader(loader: AlertDialog, message: String) {
    if (!loader.isShowing) {
        loader.setTitle(message)
        loader.show()
    }
}

fun hideLoader(loader: AlertDialog) {
    if (loader.isShowing)
        loader.dismiss()
}

fun serviceUnavailableMessage(activity: FragmentActivity) {
    Toast.makeText(
        activity,
        "Service Unavailable. Try again later",
        Toast.LENGTH_LONG
    ).show()
}

fun serviceAvailableMessage(activity: FragmentActivity) {
    Toast.makeText(
        activity,
        "Service Contacted Successfully",
        Toast.LENGTH_LONG
    ).show()
}

fun customTransformation() : Transformation =
    RoundedTransformationBuilder()
        .borderColor(Color.WHITE)
        .borderWidthDp(2F)
        .cornerRadiusDp(35F)
        .oval(false)
        .build()

fun isValidDate(date: String): Boolean {
    // Define a regular expression pattern for dd/mm/yyyy format
    val pattern = "^(0[1-9]|[1-2][0-9]|3[0-1])/(0[1-9]|1[0-2])/(19|20)\\d{2}$"
    return date.matches(pattern.toRegex())
}

fun showSnackbar(view: View, message: String, duration: Int) {
    val snackbar = Snackbar.make(view, message, duration)
    snackbar.show()
}

fun isFirstNameValid(firstName: String): Boolean {
    // Check if the first name contains only letters
    val namePattern = Regex("^[a-zA-Z]+$")
    return firstName.matches(namePattern)
}

fun isLastNameValid(lastName: String): Boolean {
    // Check if the last name contains only letters
    val namePattern = Regex("^[a-zA-Z]+$")
    return lastName.matches(namePattern)
}
fun calculateAge(birthday: String): Int? {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    try {
        val birthDate: Date? = dateFormat.parse(birthday)
        val today: Calendar = Calendar.getInstance()
        val dob: Calendar = Calendar.getInstance()
        dob.time = birthDate

        var age: Int = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    } catch (e: Exception) {
        return null // Return null for invalid date format or parsing error
    }
}
