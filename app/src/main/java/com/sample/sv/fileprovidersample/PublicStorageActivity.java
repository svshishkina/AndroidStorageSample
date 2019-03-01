package com.sample.sv.fileprovidersample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.os.Environment.MEDIA_MOUNTED;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static android.os.Environment.getExternalStorageState;
import static com.sample.sv.fileprovidersample.PrivateStorageActivity.FILE_PROVIDER_AUTHORITY;

public class PublicStorageActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_STORAGE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private File photoFile = null;
    private String currentPhotoPath;
    private View layout;
    private Button shareBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_share);
        layout = findViewById(R.id.root_layout);

        findViewById(R.id.take_photo_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getPermission()) {
                    takePhoto();
                }
            }
        });

        shareBtn = findViewById(R.id.share_photo_btn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                Snackbar.make(layout, "Разрешение отклонено",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void share() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType("imageUrl/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this,
                FILE_PROVIDER_AUTHORITY,
                photoFile));
        startActivity(Intent.createChooser(shareIntent, "Поделиться изображением"));
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.sample.sv.fileprovidersample.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                galleryAddPic();
                Bitmap takenImage = rotateBitmapOrientation(photoFile.getAbsolutePath());
                shareBtn.setVisibility(View.VISIBLE);
                ImageView ivPreview = findViewById(R.id.photo_iv);
                ivPreview.setImageBitmap(takenImage);
            } else {
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean getPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            //Разрешение уже есть
            return true;
        } else {
            //Запрашиваем разрешение
            requestPermission();
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //Разрешение уже однажды запрашивалось и пользователь его отклонил,
            // необходимы дополнительные объяснения
            Snackbar.make(layout, "Необходим доступ к хранилищу, чтобы сохранить фотографию",
                    Snackbar.LENGTH_INDEFINITE).setAction("Ок", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(PublicStorageActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_STORAGE);
                }
            }).show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
        }
    }
    private File createImageFile() throws IOException {
        if (getExternalStorageState().equals(MEDIA_MOUNTED)) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";

            File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            File file =  File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            currentPhotoPath = file.getAbsolutePath();
            return file;
        }
        return null;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(photoFilePath, opts);

        ExifInterface ei = null;
        try {
            ei = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap = null;
        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;
            case ExifInterface.ORIENTATION_UNDEFINED:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }
        return rotatedBitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
}
