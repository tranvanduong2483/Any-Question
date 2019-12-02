package com.duong.anyquestion.classes;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.duong.anyquestion.MessageHistoryActivity;
import com.duong.anyquestion.R;

import java.util.ArrayList;

public class BxhAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<Item_XepHang> listxephang;


    public BxhAdapter(Context context, int layout, ArrayList<Item_XepHang> listxephang) {
        this.context = context;
        this.layout = layout;
        this.listxephang = listxephang;
    }

    @Override
    public int getCount() {
        return listxephang.size();
    }

    @Override
    public Object getItem(int i) {
        return listxephang.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View inflater = layoutInflater.inflate(layout, null, false);
        TextView tv_account = inflater.findViewById(R.id.tv_account);
        TextView tv_field = inflater.findViewById(R.id.tv_field);
        TextView tv_thaoluan = inflater.findViewById(R.id.tv_thaoluan);
        TextView tv_star = inflater.findViewById(R.id.tv_star);
        ImageView iv_huyhieu = inflater.findViewById(R.id.iv_huyhieu);
        TextView tv_thuhang = inflater.findViewById(R.id.tv_thuhang);

        final String expert_id = listxephang.get(i).expert_id;

        inflater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastNew.showToast(context, expert_id + "", Toast.LENGTH_LONG);
            }
        });


        tv_account.setText(listxephang.get(i).expert_id);
        tv_field.setText(listxephang.get(i).name);
        tv_thaoluan.setText(listxephang.get(i).conversation_number + " thảo luận");
        tv_star.setText(listxephang.get(i).AverageStars + " sao");

        tv_thuhang.setVisibility(View.GONE);


        if (i == 0) {
            iv_huyhieu.setImageResource(R.drawable.top1st);
        } else if (i == 1) {
            iv_huyhieu.setImageResource(R.drawable.top2nd);
        } else if (i == 2) {
            iv_huyhieu.setImageResource(R.drawable.top3rd);
        } else {
            iv_huyhieu.setVisibility(View.INVISIBLE);
            tv_thuhang.setVisibility(View.VISIBLE);
            tv_thuhang.setText(" " + (i + 1) + " ");
        }

        return inflater;
    }
}
