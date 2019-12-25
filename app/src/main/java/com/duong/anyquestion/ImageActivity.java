package com.duong.anyquestion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.duong.anyquestion.Tool.ToolSupport;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        ImageView imageView = findViewById(R.id.iv_image);

        Intent intent = getIntent();
        String pathPicture = intent.getStringExtra("picture");

        Bitmap bitmap = ToolSupport.loadImageFromStorage(pathPicture);
        imageView.setImageBitmap(bitmap);
    }
}
