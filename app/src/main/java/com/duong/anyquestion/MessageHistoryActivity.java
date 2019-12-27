package com.duong.anyquestion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.duong.anyquestion.Tool.ToolSupport;
import com.duong.anyquestion.classes.ConnectThread;
import com.duong.anyquestion.classes.History;
import com.duong.anyquestion.classes.Message;
import com.duong.anyquestion.classes.MessageListAdapter;
import com.duong.anyquestion.classes.Question;
import com.duong.anyquestion.classes.ToastNew;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class MessageHistoryActivity extends AppCompatActivity {
    Button btn_cancel, btn_question;
    private Socket mSocket = ConnectThread.getInstance().getSocket();
    MessageListAdapter mMessageAdapter;
    RecyclerView mMessageRecycler;
    ArrayList<Message> messageList;

    Question question = null;

    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_history);

        int conversation_id = 1;
        int question_id = 1;
        String id_expert = "", id_user = "";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            conversation_id = bundle.getInt("conversation_id");
            question_id = bundle.getInt("question_id");
            id_user = bundle.getString("id_user");
            id_expert = bundle.getString("id_expert");
        }

        messageList = new ArrayList<>();

        mMessageRecycler = findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this, messageList, id_user, id_expert);

        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);


        btn_question = findViewById(R.id.btn_question);
        btn_question.setVisibility(View.INVISIBLE);
        btn_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageHistoryActivity.this, DetailQuestionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("question", question);
                bundle.putBoolean("local", true);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSocket.emit("get-conversation-history", conversation_id, question_id);
        mSocket.on("server-sent-conversation-history", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONArray data = (JSONArray) args[0];
                    JSONObject data2 = (JSONObject) args[1];
                    String question_json = data2.getString("question");


                    messageList.clear();
                    Gson gson = new Gson();
                    for (int i = data.length() - 1; i >= 0; i--) {
                        String noidung = data.get(i).toString();
                        Message message = gson.fromJson(noidung, Message.class);
                        messageList.add(message);
                    }

                    question = gson.fromJson(question_json, Question.class);
                    dowloadImage(question.getImage());
                    mMessageRecycler.smoothScrollToPosition(0);
                    mMessageAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("test", args[1] + "");
                }


            }
        });
    }


    private void dowloadImage(String filename) {
        StorageReference islandRef = mStorageRef.child(filename);

        final long ONE_MEGABYTE = 1024 * 1024 * 100;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                btn_question.setEnabled(true);

                Bitmap bitmap = ToolSupport.convertByteArrayToBitmap(bytes);
                String filename = ToolSupport.saveToInternalStorage(bitmap, MessageHistoryActivity.this);
                question.setImage(filename);
                btn_question.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
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
