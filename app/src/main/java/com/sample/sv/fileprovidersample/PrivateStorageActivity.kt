package com.sample.sv.fileprovidersample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.Toast
import com.sample.sv.fileprovidersample.utils.rotateBitmap
import kotlinx.android.synthetic.main.activity_photo.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val FILE_PROVIDER_AUTHORITY = "com.sample.sv.fileprovidersample.provider"
const val REQUEST_IMAGE_CAPTURE = 1
const val DATE_FORMAT = "yyyyMMdd_HHmmss"
const val IMAGE_PREFIX = "JPEG_"
const val IMAGE_EXTENSION = ".jpg"

class PrivateStorageActivity : AppCompatActivity() {

    private lateinit var photoFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        take_photo_btn.setOnClickListener { takePhoto() }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                val takenImage = rotateBitmap(photoFile.absolutePath)
                val ivPreview = findViewById<ImageView>(R.id.photo_iv)
                ivPreview.setImageBitmap(takenImage)
            } else {
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }

            val photoURI = FileProvider.getUriForFile(
                    this,
                    FILE_PROVIDER_AUTHORITY,
                    photoFile
            )
            //Uri photoURI = Uri.fromFile(photoFile); так делать нельзя
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {

        val timeStamp = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
        val imageFileName = IMAGE_PREFIX + timeStamp + "_"

        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
                imageFileName,
                IMAGE_EXTENSION,
                storageDir
        )
    }
}
