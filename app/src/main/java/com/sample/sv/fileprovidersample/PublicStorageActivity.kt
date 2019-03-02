package com.sample.sv.fileprovidersample

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Environment.*
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.sample.sv.fileprovidersample.utils.rotateBitmap
import kotlinx.android.synthetic.main.activity_photo_share.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val PERMISSION_REQUEST_STORAGE = 0
private const val IMAGE_MIME_TYPE = "imageUrl/*"

class PublicStorageActivity : AppCompatActivity() {

    private lateinit var photoFile: File
    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_share)

        take_photo_btn.setOnClickListener {
            if (checkPermission()) {
                takePhoto()
            }
        }

        share_photo_btn.setOnClickListener { share() }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                galleryAddPic()

                val takenImage = rotateBitmap(photoFile.absolutePath)

                share_photo_btn.visibility = View.VISIBLE

                val ivPreview = findViewById<ImageView>(R.id.photo_iv)
                ivPreview.setImageBitmap(takenImage)

            } else {
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto()
            } else {
                Snackbar.make(root_layout, "Разрешение отклонено", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermission(): Boolean {
        return if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            requestPermission()
            false
        }
    }

    private fun share() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        shareIntent.type = IMAGE_MIME_TYPE
        shareIntent.putExtra(
                Intent.EXTRA_STREAM,
                FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, photoFile)
        )
        startActivity(Intent.createChooser(shareIntent, "Поделиться изображением"))
    }

    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            try {
                val file = createImageFile()
                if (file == null) {
                    Snackbar.make(root_layout, "Разрешение отклонено", Snackbar.LENGTH_SHORT).show()
                    return
                }

                photoFile = file
            } catch (ex: IOException) {
                ex.printStackTrace()
            }

            val photoURI = FileProvider.getUriForFile(this, "com.sample.sv.fileprovidersample.provider", photoFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //Разрешение уже однажды запрашивалось и пользователь его отклонил,
            // необходимы дополнительные объяснения
            Snackbar.make(
                    root_layout,
                    "Необходим доступ к хранилищу, чтобы сохранить фотографию",
                    Snackbar.LENGTH_INDEFINITE
            ).setAction("Ок") {
                ActivityCompat.requestPermissions(this@PublicStorageActivity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PERMISSION_REQUEST_STORAGE)
            }.show()

        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_STORAGE)
        }
    }

    private fun createImageFile(): File? {
        if (getExternalStorageState() == MEDIA_MOUNTED) {
            val timeStamp = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
            val imageFileName = IMAGE_PREFIX + timeStamp + "_"

            val storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

            val file = File.createTempFile(
                    imageFileName,
                    IMAGE_EXTENSION,
                    storageDir
            )
            currentPhotoPath = file.absolutePath
            return file
        }
        return null
    }

    private fun galleryAddPic() {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(currentPhotoPath)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        this.sendBroadcast(mediaScanIntent)
    }
}
