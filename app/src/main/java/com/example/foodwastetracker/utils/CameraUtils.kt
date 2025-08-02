package com.example.foodwastetracker.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object CameraUtils {

    fun createImageFile(context: Context): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = File(context.filesDir, "food_images")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        return File(storageDir, "FOOD_${timeStamp}.jpg")
    }

    fun getUriForFile(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}