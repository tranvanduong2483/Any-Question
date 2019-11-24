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

    private int count = 0;

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
                ToastNew.showToast(LoadingSearchExpertActivity.this, "Hủy do quá lâu!", Toast.LENGTH_LONG);
                finish();
            }
        }, 60000);
        mSocket.on("tim kiemn chuyen gia that bai", timkiemthabai);
        mSocket.on("bat dau cuoc thao luan", callback_kqtkcg);

        mSocket.on("disconnect", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastNew.showToast(LoadingSearchExpertActivity.this, "Máy chủ ngắt kết nối!", Toast.LENGTH_LONG);
                        finish();
                    }
                });
            }
        });

    }

    public void btn_cancel(View view) {
        finish();
    }


    private Emitter.Listener timkiemthabai = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    count++;

                    if (count >= 3) {
                        ToastNew.showToast(LoadingSearchExpertActivity.this, "Không tìm thấy chuyên gia thích hợp!", Toast.LENGTH_SHORT);
                        finish();
                    }

                }

            });
        }
    };


    private Emitter.Listener callback_kqtkcg = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ToastNew.showToast(LoadingSearchExpertActivity.this, "Đây nè", Toast.LENGTH_SHORT);

                        int conversation_id = (int) args[0];
                            Intent intent_nhantin = new Intent(LoadingSearchExpertActivity.this, MessageListActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putString("question", queston_json);
                        bundle.putInt("conversation_id", conversation_id);
                        intent_nhantin.putExtras(bundle);

                            intent_nhantin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent_nhantin);
                            finish();
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
