package com.sample.sv.fileprovidersample.gallery

import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.widget.GridLayout.VERTICAL
import com.sample.sv.fileprovidersample.R
import kotlinx.android.synthetic.main.activity_gallery.*
import java.util.*

class ImageGalleryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        recycler_view.layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
        val adapter = ImageGalleryAdapter()
        recycler_view.adapter = adapter
        adapter.setPhotos(getImagesPath())
    }

    private fun getImagesPath(): List<String> {
        val images = mutableListOf<String>()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT
        )

        val cursor = contentResolver.query(
                uri,
                projection,
                null,
                null,
                MediaStore.MediaColumns.DATE_ADDED + " DESC"
        )

        if (cursor != null) {
            val columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            val columnIndexWidth = cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH)
            val columnIndexHeight = cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT)
            while (cursor.moveToNext()) {
                val width = cursor.getInt(columnIndexWidth)
                val height = cursor.getInt(columnIndexHeight)
                if (width > 0 && height > 0) {
                    val absolutePathOfImage = cursor.getString(columnIndexData)
                    images.add(absolutePathOfImage)
                }
            }
            cursor.close()
        }
        return images
    }
}
