package com.duong.anyquestion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.duong.anyquestion.classes.ConnectThread;
import com.duong.anyquestion.classes.History;
import com.duong.anyquestion.classes.HistoryAdapter2;
import com.duong.anyquestion.classes.SessionManager;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;

public class ExpertHistoryActivity extends AppCompatActivity {


    private Socket mSocket = ConnectThread.getInstance().getSocket();
    private ArrayList<History> list_history;
    private HistoryAdapter2 historyAdapter;
    private RecyclerView lv_history;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_history);

        sessionManager = new SessionManager(this);

        lv_history = findViewById(R.id.lv_history);
        list_history = new ArrayList<>();

        historyAdapter = new HistoryAdapter2(list_history, ExpertHistoryActivity.this);
        LinearLayoutManager llm = new LinearLayoutManager(ExpertHistoryActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        lv_history.setLayoutManager(llm);
        lv_history.setAdapter(historyAdapter);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSocket.emit("expert-get-their-history", sessionManager.getAccount());
        mSocket.once("server-send-expert-history", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONArray data = (JSONArray) args[0];
                                    list_history.clear();
                                    for (int i = data.length() - 1; i >= 0; i--) {
                                        String noidung = data.get(i).toString();
                                        Gson gson = new Gson();
                                        History history = gson.fromJson(noidung, History.class);
                                        list_history.add(history);
                                    }
                                    historyAdapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                }
                            }
                        });
                    }
                }
        );
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

