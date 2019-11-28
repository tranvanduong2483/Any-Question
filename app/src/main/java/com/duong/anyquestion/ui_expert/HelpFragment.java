package com.duong.anyquestion.ui_expert;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.duong.anyquestion.ExpertMainActivity;
import com.duong.anyquestion.MessageListActivity;
import com.duong.anyquestion.R;
import com.duong.anyquestion.Tool.ToolSupport;
import com.duong.anyquestion.classes.ConnectThread;
import com.duong.anyquestion.classes.PhanHoiYeuCauGiaiDap;
import com.duong.anyquestion.classes.Question;
import com.duong.anyquestion.classes.SessionManager;
import com.duong.anyquestion.classes.ToastNew;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import org.json.JSONObject;

public class HelpFragment extends Fragment {



    private SessionManager sessionManager;
    private Socket mSocket;
    private EditText edt_keywords, edt_gioithieu;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_help, container, false);
        mSocket = ConnectThread.getInstance().getSocket();
        sessionManager = new SessionManager((Activity) view.getContext());

        edt_gioithieu = view.findViewById(R.id.edt_gioithieu);
        edt_keywords = view.findViewById(R.id.edt_keywords);


        final Switch sw_expert_ready = view.findViewById(R.id.sw_expert_ready);
        sw_expert_ready.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String gioithieu = edt_gioithieu.getText() + "";
                String keywords = edt_keywords.getText() + "";
                String data = sessionManager.getAccount()+"-"+isChecked + "-" + gioithieu + "-" + keywords;
                if (!mSocket.connected()) sw_expert_ready.setChecked(false);
                else mSocket.emit("expert-send-ready", data);
            }
        });

        mSocket.on("disconnect", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sw_expert_ready.setChecked(false);
                    }
                });
            }
        });

        return view;
    }




}