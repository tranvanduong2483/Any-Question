package com.duong.anyquestion.ui_expert;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.duong.anyquestion.R;
import com.duong.anyquestion.classes.ConnectThread;
import com.duong.anyquestion.classes.SessionManager;
import com.github.nkzawa.socketio.client.Socket;

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



        Switch sw_expert_ready = view.findViewById(R.id.sw_expert_ready);
        sw_expert_ready.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String gioithieu = edt_gioithieu.getText() + "";
                String keywords = edt_keywords.getText() + "";
                String data = sessionManager.getAccount()+"-"+isChecked + "-" + gioithieu + "-" + keywords;
                mSocket.emit("expert-send-ready", data);
            }
        });
        return view;
    }




}