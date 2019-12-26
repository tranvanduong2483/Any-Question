package com.duong.anyquestion.classes;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duong.anyquestion.ImageActivity;
import com.duong.anyquestion.R;
import com.duong.anyquestion.Tool.ToolSupport;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class SentMessageHolder extends RecyclerView.ViewHolder {
    private TextView messageText, timeText, nameText;
    private ImageView iv_image, iv_image_loading;
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    private Bitmap bitmap_picture = null;

    SentMessageHolder(final View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.text_message_body);
        timeText =  itemView.findViewById(R.id.text_message_time);
        iv_image_loading = itemView.findViewById(R.id.iv_image_loading);
        iv_image = itemView.findViewById(R.id.iv_image);
        iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(itemView.getContext(), ImageActivity.class);
                if (bitmap_picture == null) return;
                String picture = ToolSupport.saveToInternalStorage(bitmap_picture, itemView.getContext());
                intent.putExtra("picture", picture);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                itemView.getContext().startActivity(intent);
            }
        });
    }

    void bind(final Message message) {
        timeText.setText(message.getHourMin());
        if (message.isTypeImage()){
            iv_image.setVisibility(View.VISIBLE);
            messageText.setVisibility(View.GONE);

            Bitmap bitmap = message.getBitmap();
            if (bitmap != null) {
                int p120dp = (int) itemView.getResources().getDimension(R.dimen.test_120dp_no_delete);
                Bitmap bitmap_resize = ToolSupport.resize(bitmap, p120dp, p120dp);
                iv_image.setImageBitmap(ToolSupport.BitmapWithRoundedCorners(bitmap_resize));

                Drawable drawable = new BitmapDrawable(itemView.getResources(), ToolSupport.BitmapWithRoundedCorners(bitmap_resize));
                iv_image_loading.setBackground(drawable);

                if (message.isStatus()) {
                    iv_image_loading.setVisibility(View.GONE);
                    bitmap_picture = bitmap;
                } else
                    iv_image_loading.setVisibility(View.VISIBLE);

            } else dowloadImage(message.getMessage());
        }else {
            iv_image.setVisibility(View.GONE);
            messageText.setVisibility(View.VISIBLE);
            messageText.setText(message.getMessage());
        }
    }


    private void dowloadImage(String name) {
        StorageReference islandRef = mStorageRef.child(name);

        final long ONE_MEGABYTE = 1024 * 1024 * 100;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = ToolSupport.convertByteArrayToBitmap(bytes);
                bitmap_picture = bitmap;

                int p120dp = (int) itemView.getResources().getDimension(R.dimen.test_120dp_no_delete);
                Bitmap bitmap_resize = ToolSupport.resize(bitmap, p120dp, p120dp);
                iv_image.setImageBitmap(ToolSupport.BitmapWithRoundedCorners(bitmap_resize));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }

}