package com.sample.sv.fileprovidersample.gallery;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sample.sv.fileprovidersample.R;

import java.util.ArrayList;
import java.util.List;

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.ImageViewHolder> {

    private List<String> photos = new ArrayList<>();

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ImageViewHolder.newInstance(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.bind(photos.get(position));
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;

        static ImageViewHolder newInstance(ViewGroup parent) {
            return new ImageViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_item, parent, false));
        }

        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_iv);
        }

        void bind(String photoPath) {
            Glide.with(itemView)
                    .load(photoPath)
                    .into(imageView);
        }

    }
}
