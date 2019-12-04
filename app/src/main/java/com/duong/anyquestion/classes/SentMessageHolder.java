package com.duong.anyquestion.classes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.duong.anyquestion.R;
import com.duong.anyquestion.Tool.ToolSupport;

import java.text.SimpleDateFormat;
import java.util.Date;


public class SentMessageHolder extends RecyclerView.ViewHolder {
    TextView messageText, timeText, nameText;
    ImageView profileImage, iv_image;


    SentMessageHolder(View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.text_message_body);
        timeText =  itemView.findViewById(R.id.text_message_time);
        iv_image = itemView.findViewById(R.id.iv_image);


        //nameText =  itemView.findViewById(R.id.text_message_name);
       // profileImage =  itemView.findViewById(R.id.image_message_profile);
    }

    void bind(Message message) {
        messageText.setText(message.getMessage());

        // Format the stored timestamp into a readable String using method.
        timeText.setText(message.getHourMin());


        if (message.isTypeImage()){
            iv_image.setVisibility(View.VISIBLE);
            messageText.setVisibility(View.GONE);
            Bitmap bitmap;
            if (!message.getMessage().isEmpty()) {
                byte[] bytes_image = Base64.decode(message.getMessage(), Base64.DEFAULT);
                bitmap = ToolSupport.convertByteArrayToBitmap(bytes_image);
            } else {
                bitmap = BitmapFactory.decodeResource(itemView.getResources(), R.drawable.error_image);
            }

            bitmap = ToolSupport.resize(bitmap, 500,500);
            iv_image.setImageBitmap(ToolSupport.BitmapWithRoundedCorners(bitmap));
        }else {
            iv_image.setVisibility(View.GONE);
            messageText.setVisibility(View.VISIBLE);
        }


        // Insert the profile image from the URL into the ImageView.
        //DateUtils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
    }
}
