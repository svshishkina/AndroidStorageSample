package com.sample.sv.fileprovidersample.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface

import java.io.IOException

fun rotateBitmap(photoFilePath: String): Bitmap {
    val opts = BitmapFactory.Options()
    val bitmap = BitmapFactory.decodeFile(photoFilePath, opts)

    var ei: ExifInterface? = null
    try {
        ei = ExifInterface(photoFilePath)
    } catch (e: IOException) {
        e.printStackTrace()
    }

    val orientation = ei!!.getAttributeInt(ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED)

    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 ->
            return rotateImage(bitmap, 90f)
        ExifInterface.ORIENTATION_ROTATE_180 ->
            return rotateImage(bitmap, 180f)
        ExifInterface.ORIENTATION_ROTATE_270 ->
            return rotateImage(bitmap, 270f)
        ExifInterface.ORIENTATION_UNDEFINED ->
            return rotateImage(bitmap, 90f)
        ExifInterface.ORIENTATION_NORMAL ->
            return bitmap
        else ->
            return bitmap
    }
}

private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height,
            matrix, true)
}