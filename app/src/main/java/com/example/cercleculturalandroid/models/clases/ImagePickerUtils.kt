package com.example.cercleculturalandroid.models.clases

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object ImagePickerUtils {

    private var currentPhotoPath: String? = null

    fun openImagePicker(activity: Activity) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        val photoFile = createImageFile(activity)
        val photoUri = FileProvider.getUriForFile(
            activity,
            "${activity.packageName}.fileprovider",
            photoFile
                                                 )
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }

        val chooserIntent = Intent.createChooser(galleryIntent, "Seleccionar imagen")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))

        activity.startActivityForResult(chooserIntent, REQUEST_CODE_IMAGE_PICKER)
    }

    private fun createImageFile(activity: Activity): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
                                  ).apply { currentPhotoPath = absolutePath }
    }

    fun getImageUriFromResult(intent: Intent?): Uri? {
        return intent?.data ?: currentPhotoPath?.let { Uri.fromFile(File(it)) }
    }

    const val REQUEST_CODE_IMAGE_PICKER = 102
}