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
    var imageUri = MutableLiveData<Uri>()

    fun uploadImageToFirebase(
        userid: String,
        bitmap: Bitmap,
        updating: Boolean,
        callback: (String?) -> Unit
    ) {
        val imageRef = storage.child("photos").child("${userid}.jpg")
        val baos = ByteArrayOutputStream()
        lateinit var uploadTask: UploadTask

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        imageRef.metadata
            .addOnSuccessListener {
                if (updating) {
                    uploadTask = imageRef.putBytes(data)
                    uploadTask
                        .addOnSuccessListener { taskSnapshot ->
                            taskSnapshot.metadata?.reference?.downloadUrl
                                ?.addOnCompleteListener { task ->
                                    val imageUrl = task.result?.toString()
                                    callback(imageUrl)

                                }
                        }
                }
            }
            .addOnFailureListener {
                uploadTask = imageRef.putBytes(data)
                uploadTask
                    .addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.metadata?.reference?.downloadUrl
                            ?.addOnCompleteListener { task ->
                                val imageUrl = task.result?.toString()
                                callback(imageUrl)
                            }
                    }
            }
    }
}



