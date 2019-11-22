package com.duong.anyquestion;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.duong.anyquestion.Tool.ToolSupport;
import com.duong.anyquestion.classes.ConnectThread;
import com.duong.anyquestion.classes.Question;
import com.duong.anyquestion.classes.User;
import com.duong.anyquestion.ui_expert.*;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import org.json.JSONObject;

public class ExpertMainActivity extends AppCompatActivity {

    private Socket mSocket = ConnectThread.getInstance().getSocket();;

    final Fragment fragment1 = new AccountFragment();
    final Fragment fragment2 = new HelpFragment();
    final Fragment fragment3 = new HistoryFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_expert);

        fm.beginTransaction().add(R.id.nav_host_fragment, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.nav_host_fragment, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.nav_host_fragment, fragment1, "1").commit();

        mSocket.on("send-question-to-expert", callback_question);

        BottomNavigationView nav_view = findViewById(R.id.nav_view);
        nav_view.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }


    private Emitter.Listener callback_question = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    final String noidung, account;
                    try {
                        noidung = data.getString("question");

                        Gson gson = new Gson();
                        Question question = gson.fromJson(noidung, Question.class);

                        Bitmap question_bitmap = ToolSupport.convertStringBase64ToBitmap(question.getImageString());

                        account = data.getString("id");
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ExpertMainActivity.this);
                        builder.setMessage(question.getTittle())
                                .setTitle("Câu hỏi: " + question.getMoney() + " VND")
                                .setCancelable(false)
                                .setIcon(new BitmapDrawable(getResources(), question_bitmap))
                                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        mSocket.emit("expert-phanhoi", account + "-true");
                                        dialog.cancel();
                                        String gioithieu = "Xin chào, tôi là chuyên gia của bạn! ";
                                        Bundle bundle = new Bundle();
                                        bundle.putString("tinnhangioithieu", gioithieu);
                                        Intent intent_loading_search_expert = new Intent(ExpertMainActivity.this, MessageListActivity.class);
                                        intent_loading_search_expert.putExtras(bundle);
                                        startActivity(intent_loading_search_expert);
                                    }
                                })
                                .setNegativeButton("Từ chối", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        mSocket.emit("expert-phanhoi", account + "-false");
                                        dialog.cancel();
                                    }
                                });
                        androidx.appcompat.app.AlertDialog alert = builder.create();
                        alert.show();
                    } catch (Exception e) {
                    }
                }
            });
        }
    };




    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    return true;

                case R.id.navigation_dashboard:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    return true;

                case R.id.navigation_notifications:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    return true;
            }
            return false;
        }
    };
}
