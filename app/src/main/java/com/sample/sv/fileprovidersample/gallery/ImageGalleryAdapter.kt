package com.sample.sv.fileprovidersample.gallery

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.sample.sv.fileprovidersample.R
import kotlinx.android.synthetic.main.image_item.view.*
import java.util.*

class ImageGalleryAdapter : RecyclerView.Adapter<ImageGalleryAdapter.ImageViewHolder>() {

    private var photos: List<String> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder.newInstance(parent)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    fun setPhotos(photos: List<String>) {
        this.photos = photos
        notifyDataSetChanged()
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(photoPath: String) {
            Glide.with(itemView)
                    .load(photoPath)
                    .into(itemView.image_iv)
        }

        companion object {

            fun newInstance(parent: ViewGroup): ImageViewHolder {
                return ImageViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.image_item, parent, false))
            }
        }
    }
}
