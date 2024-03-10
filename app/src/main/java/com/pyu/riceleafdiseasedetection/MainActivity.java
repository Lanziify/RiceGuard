package com.pyu.riceleafdiseasedetection;

import android.Manifest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button selectFromGallery, selectFromCamera;
    ImageView backgroundImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backgroundImage = findViewById(R.id.backgroundImage);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        selectFromGallery = findViewById(R.id.selectFromGallery);
        selectFromCamera = findViewById(R.id.selectFromCamera);

        selectFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        selectFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 3);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case 1:
                    Uri uri = data.getData();
                    Bitmap galleryImage = null;
                    try {
                        galleryImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Pass the bitmap to the next activity
                    if (galleryImage != null) {
                        galleryImage = Bitmap.createScaledBitmap(galleryImage, 180, 180, false);
                        Intent intent = new Intent(this, PredictionActivity.class);
                        intent.putExtra("imageBitmap", galleryImage);
                        startActivity(intent);
                    }
                    break;
                case 3:
                    Bitmap image = (Bitmap) data.getExtras().get("data");
                    int dims = Math.min(image.getWidth(), image.getHeight());
                    image = ThumbnailUtils.extractThumbnail(image, dims, dims);
                    // Pass the bitmap to the next activity
                    if (image != null) {
                        Intent intent = new Intent(this, PredictionActivity.class);
                        intent.putExtra("imageBitmap", image);
                        startActivity(intent);
                    }
                    break;
            }
        }
    }

}