package com.duong.anyquestion.classes;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.duong.anyquestion.R;
import com.duong.anyquestion.Tool.ToolSupport;

public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
    private TextView messageText, timeText, nameText;
    ImageView profileImage,iv_image;

    ReceivedMessageHolder(View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.text_message_body);
        timeText =  itemView.findViewById(R.id.text_message_time);
        iv_image = itemView.findViewById(R.id.iv_image);

        // nameText =  itemView.findViewById(R.id.text_message_name);
        profileImage =  itemView.findViewById(R.id.image_message_profile);
    }

    void bind(Message message) {
        messageText.setText(message.getMessage());
        // Format the stored timestamp into a readable String using method.
        timeText.setText(message.getTime());

        if (message.isTypeImage()){
            iv_image.setVisibility(View.VISIBLE);
            messageText.setVisibility(View.GONE);

            byte[] bytes_image = Base64.decode(message.getMessage(), Base64.DEFAULT);
            Bitmap bitmap = ToolSupport.convertByteArrayToBitmap(bytes_image);
            bitmap = ToolSupport.resize(bitmap, 500,500);

            iv_image.setImageBitmap(ToolSupport.BitmapWithRoundedCorners(bitmap));
        }else {
            iv_image.setVisibility(View.GONE);
            messageText.setVisibility(View.VISIBLE);
        }


       // nameText.setText(message.getSender().getAccount());

//        if (bitmap_you!=null)
  //          profileImage.setImageBitmap(bitmap_you);

        // Insert the profile image from the URL into the ImageView.
        //DateUtils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
    }
}