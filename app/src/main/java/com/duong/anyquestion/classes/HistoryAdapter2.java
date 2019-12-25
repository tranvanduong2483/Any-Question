package com.duong.anyquestion.classes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duong.anyquestion.MessageHistoryActivity;
import com.duong.anyquestion.R;
import com.duong.anyquestion.Tool.ToolSupport;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class HistoryAdapter2 extends RecyclerView.Adapter<HistoryAdapter2.ViewHolder> {

    ArrayList<History> list;
    Context context;
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    public HistoryAdapter2(ArrayList<History> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_question_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final History history = list.get(position);
        holder.tv_tittle.setText(history.getTitle());
        holder.tv_field.setText(history.getName());
        holder.rb_danhgia.setRating(history.getStar());
        holder.tv_fromid.setText(history.getId_user());

        holder.btn_detail1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                XemChiTiet(history);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                XemChiTiet(history);
            }
        });
        dowloadImage(history.getImage(), holder);
    }

    private void XemChiTiet(History history) {
        Intent intent = new Intent(context, MessageHistoryActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("conversation_id", history.getConversation_id());
        bundle.putString("id_expert", history.getId_expert());
        bundle.putString("id_user", history.getId_user());
        bundle.putInt("question_id", history.getQuestion_id());
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private void dowloadImage(String name, final ViewHolder holder) {
        if (name == null || name.isEmpty()) return;

        StorageReference islandRef = mStorageRef.child(name);

        final long ONE_MEGABYTE = 1024 * 1024 * 100;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = ToolSupport.convertByteArrayToBitmap(bytes);
                holder.iv_question.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_tittle;
        TextView tv_field;
        RatingBar rb_danhgia;
        TextView tv_fromid;
        ImageView iv_question;
        View itemView;
        Button btn_detail1, btn_detail2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_tittle = itemView.findViewById(R.id.tv_tittle);
            tv_field = itemView.findViewById(R.id.tv_field);
            rb_danhgia = itemView.findViewById(R.id.rb_danhgia);
            tv_fromid = itemView.findViewById(R.id.tv_fromid);
            iv_question = itemView.findViewById(R.id.iv_question);
            btn_detail1 = itemView.findViewById(R.id.btn_detail1);
            btn_detail2 = itemView.findViewById(R.id.btn_detail2);
            this.itemView = itemView;
        }
    }
}
