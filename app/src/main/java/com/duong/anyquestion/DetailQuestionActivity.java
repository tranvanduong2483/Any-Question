package com.duong.anyquestion;
//thaydoi

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.duong.anyquestion.Tool.ToolSupport;
import com.duong.anyquestion.classes.Field;
import com.duong.anyquestion.classes.Question;
import com.duong.anyquestion.classes.RialTextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

public class DetailQuestionActivity extends AppCompatActivity {

    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    ImageView iv_image;
    String path_image = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_question);

        iv_image = findViewById(R.id.iv_image);

        TextView tv_tittle = findViewById(R.id.tv_tittle);
        TextView tv_note = findViewById(R.id.tv_note);
        RialTextView tv_money = findViewById(R.id.tv_money);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Question question = (Question) bundle.getSerializable("question");
            tv_tittle.setText(question.getTittle());
            tv_note.setText("Ghi chú: " + (question.getDetailed_description().isEmpty() ? "(không có)" : question.getDetailed_description()));
            tv_money.setText(question.getMoney() + "");
            if (bundle.getBoolean("local")) {
                Bitmap bitmap = ToolSupport.loadImageFromStorage(question.getImage());
                iv_image.setImageBitmap(ToolSupport.BitmapWithRoundedCorners(bitmap));
                path_image = question.getImage();
            } else {
                dowloadImage(question.getImage());
            }
        }

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailQuestionActivity.this, ImageActivity.class);
                if (path_image == null) return;
                intent.putExtra("picture", path_image);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void dowloadImage(String filename) {
        StorageReference islandRef = mStorageRef.child(filename);

        final long ONE_MEGABYTE = 1024 * 1024 * 100;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = ToolSupport.convertByteArrayToBitmap(bytes);
                iv_image.setImageBitmap(ToolSupport.BitmapWithRoundedCorners(bitmap));
                path_image = ToolSupport.saveToInternalStorage(bitmap, DetailQuestionActivity.this);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }
}
