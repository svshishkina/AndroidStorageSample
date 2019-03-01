package com.sample.sv.fileprovidersample.gallery;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.sample.sv.fileprovidersample.R;

import java.util.ArrayList;
import java.util.List;

import static android.widget.GridLayout.VERTICAL;

public class ImageGalleryActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, VERTICAL));
        ImageGalleryAdapter adapter = new ImageGalleryAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setPhotos(getImagesPath());
    }

    public List<String> getImagesPath() {
        ArrayList<String> images = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.WIDTH, MediaStore.Images.Media.HEIGHT};

        Cursor cursor = getContentResolver().query(uri, projection, null, null,
                MediaStore.MediaColumns.DATE_ADDED + " DESC");

        if (cursor != null) {
            int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            int columnIndexWidth = cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH);
            int columnIndexHeight= cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT);
            while (cursor.moveToNext()) {
                int width = cursor.getInt(columnIndexWidth);
                int height = cursor.getInt(columnIndexHeight);
                if (width > 0 && height > 0) {
                    String absolutePathOfImage = cursor.getString(columnIndexData);
                    images.add(absolutePathOfImage);
                }
            }
            cursor.close();
        }
        return images;
    }
}
