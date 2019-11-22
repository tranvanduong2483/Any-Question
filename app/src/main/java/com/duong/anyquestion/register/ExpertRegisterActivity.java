package com.duong.anyquestion.register;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.duong.anyquestion.R;
import com.duong.anyquestion.classes.ConnectThread;
import com.duong.anyquestion.classes.Education;
import com.duong.anyquestion.classes.Expert;
import com.duong.anyquestion.classes.Field;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ExpertRegisterActivity extends AppCompatActivity {

    private Socket mSocket = ConnectThread.getInstance().getSocket();
    Button btn_login, btn_register;
    EditText edt_account,edt_mail, edt_address, edt_password1,edt_password2, edt_fullname;
    Spinner spn_education,spn_field;

    ArrayAdapter<String> adapter_education,adapter_field;
    private ArrayList<String> array_education,array_field ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_expert);

        array_education=new ArrayList<>();

        array_field=new ArrayList<>();


        mSocket.emit("client-get-education","get");
        mSocket.emit("client-get-field","get");

        mSocket.on("server-sent-education",callback_get_education);
        mSocket.on("server-sent-field",callback_get_field);


        spn_education= findViewById(R.id.spn_education);
        spn_field= findViewById(R.id.spn_field);



        adapter_education = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, array_education);
        spn_education.setAdapter(adapter_education);


        adapter_field = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, array_field);
        spn_field.setAdapter(adapter_field);

        mSocket.on("ket-qua-dang-ki-expert",callback_dangky_expert );

        edt_account = findViewById(R.id.edt_account);
        edt_address = findViewById(R.id.edt_address);
        edt_fullname = findViewById(R.id.edt_fullname);
        edt_password1=  findViewById(R.id.edt_password1);
        edt_password2 = findViewById(R.id.edt_password2);
        edt_mail = findViewById(R.id.edt_email);

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Account = edt_account.getText() +"";
                //String Education = edt_education.getText() + "";
                //String Field = edt_field.getText() + "";
                String FullName = edt_fullname.getText() +"";
                String Password1 =  edt_password1.getText() +"";
                String Password2 =  edt_password2.getText() +"";
                String Mail = edt_mail.getText() +"";
                String Address = edt_address.getText() +"";

                if (Account.isEmpty() || "Education".isEmpty() || "Field".isEmpty() || FullName.isEmpty() || Password1.isEmpty()
                        || Address.isEmpty() || Mail.isEmpty() || Password2.isEmpty()){
                    Toast.makeText(getApplication(),"Thiếu thông tin", Toast.LENGTH_LONG).show();
                    return;
                }

                if (Password1.compareTo(Password2) !=0){
                    Toast.makeText(getApplication(),"Mật khẩu không khớp", Toast.LENGTH_LONG).show();
                    return;
                }

                    Expert expert = new Expert(Account,Password1,FullName,null, 1, 1,Address,Mail,0);
                mSocket.emit("client-dang-ki-expert",expert.toJSON());
            }
        });





        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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





    private Emitter.Listener callback_dangky_expert = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String noidung;
                    try {
                        noidung = data.getString("ketqua");
                        if (noidung=="true"){
                            Toast.makeText(getApplicationContext(), "Đăng ký thành công",Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }else{
                            Toast.makeText(getApplicationContext(), "Đăng ký thất bại!",Toast.LENGTH_LONG).show();
                        }


                    } catch (JSONException e) {
                        return;
                    }

                }
            });
        }
    };



    private Emitter.Listener callback_get_education = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {

                        JSONArray jsonArray = (JSONArray) args[0];

                        Gson gson = new Gson();
                        for (int i=0; i<jsonArray.length(); i++){
                            Education education = gson.fromJson( jsonArray.get(i).toString(),Education.class);
                            array_education.add(education.getEducation_id() +" - " + education.getName());
                        }
                        adapter_education.notifyDataSetChanged();
                    } catch (Exception ignored) { }

                }
            });
        }
    };


    private Emitter.Listener callback_get_field = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {

                        JSONArray jsonArray = (JSONArray) args[0];

                        Gson gson = new Gson();
                        for (int i=0; i<jsonArray.length(); i++){
                            Field field = gson.fromJson( jsonArray.get(i).toString(),Field.class);
                            adapter_field.add(field.getField_id() +" - " + field.getName());
                        }
                        adapter_field.notifyDataSetChanged();
                    } catch (Exception ignored) { }

                }
            });
        }
    };
}