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

    fun uploadImageToFirebase(userid: String, bitmap: Bitmap, updating: Boolean, callback: (String?) -> Unit) {
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

    fun updateUserImage(userid: String, imageUri: Uri?, imageView: ImageView, updating: Boolean) {
        Picasso.get().load(imageUri)
            .resize(200, 200)
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .centerCrop()
            .into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    Timber.i("DX onBitmapLoaded $bitmap")
                    uploadImageToFirebase(userid, bitmap!!, updating) { imageUrl ->
                        // Callback when image upload is complete
                        if (imageUrl != null) {
                            imageView.setImageBitmap(bitmap)
                        } else {
                            // Handle image upload failure if needed
                            // You can display an error message to the user
                        }
                    }
                }

                override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                    Timber.i("DX onBitmapFailed $e")
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            })
    }

    fun updateDefaultImage(userid: String, resource: Int, imageView: ImageView) {
        Picasso.get().load(resource)
            .resize(200, 200)
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .centerCrop()
            .into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    Timber.i("DX onBitmapLoaded $bitmap")
                    uploadImageToFirebase(userid, bitmap!!, false) { imageUrl ->
                        // Callback when image upload is complete
                        if (imageUrl != null) {
                            imageView.setImageBitmap(bitmap)
                        } else {
                            // Handle image upload failure if needed
                            // You can display an error message to the user
                        }
                    }
                }

                override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                    Timber.i("DX onBitmapFailed $e")
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            })
    }
}