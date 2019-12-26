
package com.duong.anyquestion.ui_user;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duong.anyquestion.R;
import com.duong.anyquestion.classes.ConnectThread;
import com.duong.anyquestion.classes.History;
import com.duong.anyquestion.classes.HistoryAdapter2;
import com.duong.anyquestion.classes.SessionManager;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HistoryFragment extends Fragment {
    Button btn_refesh;
    View view;
    private Socket mSocket = ConnectThread.getInstance().getSocket();
    private ArrayList<History> list_history;
    private HistoryAdapter2 historyAdapter;
    private RecyclerView lv_history;
    private Button btn_myhistory, btn_allhistory;
    private String user_id = null;
    private TextView tv_status;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history, container, false);

        list_history = new ArrayList<>();
        lv_history = view.findViewById(R.id.lv_history);
        btn_myhistory = view.findViewById(R.id.btn_myhistory);
        btn_allhistory = view.findViewById(R.id.btn_allhistory);
        tv_status = view.findViewById(R.id.tv_status);


        historyAdapter = new HistoryAdapter2(list_history, view.getContext());


        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        lv_history.setLayoutManager(llm);
        lv_history.setAdapter(historyAdapter);

        btn_refesh = view.findViewById(R.id.btn_refesh);

        btn_refesh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSocket.emit("get-list-history");
            }
        });

        btn_myhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionManager sessionManager = new SessionManager((Activity) view.getContext());
                user_id = sessionManager.getAccount();
                mSocket.emit("get-list-history");
                tv_status.setText("Của tôi");
            }
        });

        btn_allhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_id = null;
                mSocket.emit("get-list-history");
                tv_status.setText("Toàn bộ");

            }
        });


        mSocket.on("server-sent-list-history", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONArray data = (JSONArray) args[0];
                                    Gson gson = new Gson();

                                    list_history.clear();
                                    for (int i = 0; i < data.length(); i++) {
                                        String noidung = data.get(i).toString();
                                        History history = gson.fromJson(noidung, History.class);

                                        if (user_id != null) {
                                            if (user_id.equals(history.getId_user()))
                                                list_history.add(history);
                                        } else {
                                            list_history.add(0, history);
                                        }

                                    }
                                    historyAdapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
        );


        setGetHistory();

        return view;
    }


    private void setGetHistory() {
        mSocket.emit("get-list-history");
        TimerTask timertaks = new TimerTask() {
            @Override
            public void run() {
                if (list_history.isEmpty()) {
                    if (mSocket.connected())
                        mSocket.emit("get-list-history");
                }
            }
        };
        long delay = 3000L;
        Timer timer = new Timer("Timer");
        timer.schedule(timertaks, 0, delay);
    }
}