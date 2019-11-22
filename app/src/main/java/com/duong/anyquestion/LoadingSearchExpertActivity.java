package com.duong.anyquestion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.duong.anyquestion.classes.ConnectThread;
import com.duong.anyquestion.classes.Expert;
import com.duong.anyquestion.classes.SessionManager;
import com.duong.anyquestion.classes.ToastNew;
import com.duong.anyquestion.classes.User;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import org.json.JSONObject;

public class LoadingSearchExpertActivity extends AppCompatActivity {

    private Socket mSocket = ConnectThread.getInstance().getSocket();
    final Handler handler = new Handler();
    TextView tv_tittle, tv_money, tv_note;
    private String queston_json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_search_expert);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_tittle = findViewById(R.id.tv_tittle);
        tv_money = findViewById(R.id.tv_money);
        tv_note = findViewById(R.id.tv_note);


        if (getIntent().getExtras() != null) {
            queston_json = getIntent().getExtras().getString("question");

            //tv_tittle.setText(tittle);
            //tv_money.setText(money);
            //tv_note.setText(note);
        }


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSocket.emit("user-search-expert", queston_json);
            }
        }, 5000);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ToastNew.showToast(LoadingSearchExpertActivity.this, "Không tìm thấy!", Toast.LENGTH_LONG);
                finish();
            }
        }, 15000);
        mSocket.on("ket-qua-tim-kiem-chuyen-gia", callback_kqtkcg);
    }

    public void btn_cancel(View view) {
        finish();
    }


    private Emitter.Listener callback_kqtkcg = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        if (data.getString("ketqua").equals("true")) {
                            Intent intent_nhantin = new Intent(LoadingSearchExpertActivity.this, MessageListActivity.class);

                           // ToastNew.showToast(LoadingSearchExpertActivity.this, args[0] + "", Toast.LENGTH_SHORT);


                           // String stringAvatar = data.getString("avatar");

                            //Bundle bundle = new Bundle();
                            //bundle.putString("stringAvatar", stringAvatar);
                            //intent_nhantin.putExtras(bundle);

                            intent_nhantin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent_nhantin);
                            finish();
                        } else {
                            ToastNew.showToast(LoadingSearchExpertActivity.this, "Không tìm thấy chuyên gia thích hợp!", Toast.LENGTH_SHORT);
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastNew.showToast(LoadingSearchExpertActivity.this, "Lỗi gì đó", Toast.LENGTH_SHORT);

                    }
                }
            });
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                this.finish();
                return super.onOptionsItemSelected(item);
        }
    }
}
