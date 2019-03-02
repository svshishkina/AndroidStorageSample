package com.sample.sv.fileprovidersample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.sample.sv.fileprovidersample.gallery.ImageGalleryActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        take_private_photo_btn.setOnClickListener {
                    startActivity(Intent(this@MainActivity, PrivateStorageActivity::class.java))
                }

        take_gallery_photo_btn.setOnClickListener {
            startActivity(Intent(this@MainActivity, PublicStorageActivity::class.java))
        }

        image_gallery_btn.setOnClickListener {
            startActivity(Intent(this@MainActivity, ImageGalleryActivity::class.java))
        }
    }
}
