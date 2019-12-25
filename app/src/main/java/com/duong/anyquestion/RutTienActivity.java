package com.duong.anyquestion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.duong.anyquestion.classes.Bank;
import com.duong.anyquestion.classes.ConnectThread;
import com.duong.anyquestion.classes.Education;
import com.duong.anyquestion.classes.Expert;
import com.duong.anyquestion.classes.Field;
import com.duong.anyquestion.classes.PaymentRequest;
import com.duong.anyquestion.classes.ToastNew;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;

public class RutTienActivity extends AppCompatActivity {

    private Socket mSocket = ConnectThread.getInstance().getSocket();

    Button btn_cancel;
    Button btn_ruttien;

    Spinner spn_bank;
    ProgressBar pb_loading_bank;

    TextView tv_balance;
    EditText edt_money, edt_account_number, edt_account_name, edt_password;
    String account;

    ArrayAdapter<Bank> adapter_bank;
    private ArrayList<Bank> array_bank;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rut_tien);


        array_bank = new ArrayList<>();
        spn_bank = findViewById(R.id.spn_bank);
        edt_account_number = findViewById(R.id.edt_account_number);
        edt_account_name = findViewById(R.id.edt_account_name);
        edt_password = findViewById(R.id.edt_password);


        adapter_bank = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, array_bank);
        spn_bank.setAdapter(adapter_bank);


        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edt_money = findViewById(R.id.edt_money);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_ruttien = findViewById(R.id.btn_ruttien);
        btn_ruttien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int balance = Integer.parseInt(tv_balance.getText() + "");
                    int money = Integer.parseInt(edt_money.getText() + "");
                    if (balance < money || money <= 0) {
                        ToastNew.showToast(RutTienActivity.this, "Số tiền rút phải nhỏ hơn số dư", Toast.LENGTH_LONG);
                        return;
                    }
                    String account_number = edt_account_number.getText() + "";
                    String account_name = edt_account_name.getText() + "";
                    String password = edt_password.getText() + "";
                    if (account_name.isEmpty() || account_number.isEmpty() || password.isEmpty()) {
                        ToastNew.showToast(RutTienActivity.this, "Nhap thieu thong tin", Toast.LENGTH_LONG);
                        return;
                    }
                    int bank_id = array_bank.get(spn_bank.getSelectedItemPosition()).getBank_id();

                    PaymentRequest paymentRequest = new PaymentRequest(account, bank_id, money, account_number, account_name);
                    mSocket.emit("expert-gui-yeu-cau-rut-tien", paymentRequest.toJSON(), password);

                } catch (Exception e) {
                    ToastNew.showToast(RutTienActivity.this, "Nhap thieu hoac sai thong tin", Toast.LENGTH_LONG);
                }
            }
        });

        pb_loading_bank = findViewById(R.id.pb_loading_bank);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_balance = findViewById(R.id.tv_balance);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            tv_balance.setText(bundle.getString("balance"));
            account = bundle.getString("account");
        }

        mSocket.once("server-sent-bank", callback_get_bank);
        mSocket.emit("client-get-bank");

        mSocket.on("server-send-payment-request-status", callback_payment_request_status);
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


    private Emitter.Listener callback_get_bank = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONArray jsonArray = (JSONArray) args[0];
                        Gson gson = new Gson();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Bank bank = gson.fromJson(jsonArray.get(i).toString(), Bank.class);
                            adapter_bank.add(bank);
                        }
                        adapter_bank.notifyDataSetChanged();
                        pb_loading_bank.setVisibility(View.GONE);
                        spn_bank.setVisibility(View.VISIBLE);
                    } catch (Exception ignored) {
                    }

                }
            });
        }
    };


    private Emitter.Listener callback_payment_request_status = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int status = (int) args[0];

                        if (status == 1) {
                            ToastNew.showToast(RutTienActivity.this, "Yêu cầu rút tiền đã được ghi nhận", Toast.LENGTH_LONG);
                        } else {
                            ToastNew.showToast(RutTienActivity.this, "Yêu cầu rút tiền không thành công", Toast.LENGTH_LONG);
                        }

                    } catch (Exception ignored) {
                        ToastNew.showToast(RutTienActivity.this, "Yêu cầu rút tiền không thành công", Toast.LENGTH_LONG);
                    }

                }
            });
        }
    };
}
