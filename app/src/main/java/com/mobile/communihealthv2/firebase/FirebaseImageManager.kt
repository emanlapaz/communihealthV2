package com.mobile.communihealthv2.firebase

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import timber.log.Timber
import java.io.ByteArrayOutputStream
class FirebaseImageManager {

    private val storage = FirebaseStorage.getInstance().reference

    fun uploadImageToFirebase(
        userid: String,
        bitmap: Bitmap,
        callback: (String?) -> Unit
    ) {
        // Generate a unique filename for each image
        val filename = "${userid}_${System.currentTimeMillis()}.jpg"
        val imageRef = storage.child("photos/$filename")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                callback(imageUrl)
            }!!.addOnFailureListener {
                callback(null)
            }
        }.addOnFailureListener {
            callback(null)
        }.addOnCompleteListener {
            baos.close()
        }
    }
}
