package com.example.crimewavee.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider

import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ImageUtils {
    companion object {
        fun createImageFile(context: Context): File {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir: File? = context.getExternalFilesDir("Pictures")
            return File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
            )
        }

        fun getUriForFile(context: Context, file: File): Uri {
            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        }

        fun compressAndSaveImage(context: Context, bitmap: Bitmap, fileName: String): String? {
            return try {
                val file = File(context.filesDir, "product_images")
                if (!file.exists()) {
                    file.mkdirs()
                }

                val imageFile = File(file, "$fileName.jpg")
                val outputStream = FileOutputStream(imageFile)

                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                outputStream.flush()
                outputStream.close()

                imageFile.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
            return try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap.width > 1024 || bitmap.height > 1024) {
                    val ratio = minOf(1024.0 / bitmap.width, 1024.0 / bitmap.height)
                    val width = (bitmap.width * ratio).toInt()
                    val height = (bitmap.height * ratio).toInt()
                    Bitmap.createScaledBitmap(bitmap, width, height, true)
                } else {
                    bitmap
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun getBitmapFromPath(filePath: String): Bitmap? {
            return try {
                val file = File(filePath)
                if (file.exists()) {
                    BitmapFactory.decodeFile(filePath)
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
