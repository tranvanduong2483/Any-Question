package com.duong.anyquestion.classes;

import android.app.Notification;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.duong.anyquestion.R;

import java.util.ArrayList;

public class HistoryAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<History> listHisory;


    public HistoryAdapter(Context context, int layout, ArrayList<History> listNotification) {
        this.context = context;
        this.layout = layout;
        this.listHisory = listNotification;
    }

    @Override
    public int getCount() {
        return listHisory.size();
    }

    @Override
    public Object getItem(int i) {
        return listHisory.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View inflater = layoutInflater.inflate(layout, null, false);
        TextView tv_tittle = inflater.findViewById(R.id.tv_tittle);
        TextView tv_field = inflater.findViewById(R.id.tv_field);
        RatingBar rb_danhgia = inflater.findViewById(R.id.rb_danhgia);


        String title = listHisory.get(i).title;
        String field = listHisory.get(i).name;
        int star = listHisory.get(i).star;


        tv_tittle.setText(title);
        tv_field.setText(field);
        rb_danhgia.setRating(star);


        return inflater;
    }
}
