package com.duong.anyquestion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.duong.anyquestion.classes.ConnectThread;
import com.duong.anyquestion.classes.History;
import com.duong.anyquestion.classes.SessionManager;
import com.duong.anyquestion.classes.ToastNew;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import org.json.JSONArray;

public class NapTienActivity extends AppCompatActivity {


    private Socket mSocket = ConnectThread.getInstance().getSocket();

    Button btn_cancel, btn_nap_tien;
    SessionManager sessionManager;
    EditText edt_serial, edt_ma_the;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nap_tien);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);

        edt_ma_the = findViewById(R.id.edt_ma_the);
        edt_serial = findViewById(R.id.edt_serial);

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_nap_tien = findViewById(R.id.btn_nap_tien);
        btn_nap_tien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_id = sessionManager.getAccount();
                String serial = edt_serial.getText() + "";
                String card_code = edt_ma_the.getText() + "";
                mSocket.emit("user-nap-tien", user_id, serial, card_code);
            }
        });


        mSocket.on("user-nap-tien-status", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String user_naptien = args[0] + "";
                                    int card_money = (int) args[1];

                                    if (!user_naptien.equals(sessionManager.getAccount())) return;

                                    if (card_money == -1) {
                                        ToastNew.showToast(getApplication(), "Mã thẻ cào không hợp lệ hoặc đã sử dụng!", Toast.LENGTH_LONG);
                                    } else if (card_money == -2) {
                                        ToastNew.showToast(getApplication(), "Lỗi", Toast.LENGTH_LONG);
                                    } else if (card_money > 0) {
                                        ToastNew.showToast(getApplication(), "Bạn đã nạp thành công " + card_money + " VNĐ", Toast.LENGTH_LONG);
                                        finish();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ToastNew.showToast(getApplication(), "Nạp tiền không thành công", Toast.LENGTH_LONG);
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
