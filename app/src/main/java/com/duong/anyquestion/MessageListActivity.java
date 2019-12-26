package com.duong.anyquestion;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duong.anyquestion.Tool.ToolSupport;
import com.duong.anyquestion.classes.ConnectThread;
import com.duong.anyquestion.classes.Expert;
import com.duong.anyquestion.classes.Message;
import com.duong.anyquestion.classes.MessageListAdapter;
import com.duong.anyquestion.classes.Question;
import com.duong.anyquestion.classes.SessionManager;
import com.duong.anyquestion.classes.ToastNew;
import com.duong.anyquestion.classes.User;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MessageListActivity extends AppCompatActivity {

    private final int REQUEST_TAKE_PHOTO = 123;
    private final int REQUEST_CHOOSE_PHOTO = 132;
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private List<Message> messageList = new ArrayList<>();
    private SessionManager sessionManager;
    private ImageButton btn_send, btn_camera, btn_picture;
    private EditText edt_message;
    private User user;
    private Expert expert;
    private Socket mSocket = ConnectThread.getInstance().getSocket();
    private Bundle bundle;

    private View mShadowView;
    private FloatingActionButton btn_close, btn_question;
    private FloatingActionsMenu floatingMenu;
    private int conversation_id;


    Bitmap bitmap_you;
    Bitmap bitmap_me;
    Question question;
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        sessionManager = new SessionManager(this);
        AnhXa();
        initEventButton();

        if (sessionManager.isUser()) user = sessionManager.getUser();
        else expert = sessionManager.getExpert();

        bundle = getIntent().getExtras();
        if (bundle != null) {
            String stringAvatar = bundle.getString("stringAvatar");
            if (stringAvatar != null)
                bitmap_you = ToolSupport.convertStringBase64ToBitmap(stringAvatar);
            question = (Question) bundle.getSerializable("question");
            dowloadImage(question.getImage());

            conversation_id = bundle.getInt("conversation_id");
            ToastNew.showToast(MessageListActivity.this, conversation_id + "", Toast.LENGTH_LONG);
        }

        mSocket.on("server-send-message", callback_nhantinnhan);
        mSocket.on("user-ready-thao-luan", callback_gioithieu);
        mSocket.on("server-bao-nguoi-kia-da-roi-cuoc-thao-luan", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (conversation_id != (int) args[0]) return;
                        if (user != null) {
                            ToastNew.showToast(getApplication(), "Chuyên gia đã rời cuộc thảo luận!", Toast.LENGTH_LONG);
                            Intent intent = new Intent(MessageListActivity.this, RatingForExpertActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            Bundle bundle = new Bundle();
                            bundle.putInt("conversation_id", conversation_id);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            ToastNew.showToast(getApplication(), "Người thắc mắc đã rời cuộc thảo luận!", Toast.LENGTH_LONG);
                        }
                        finish();
                    }
                });
            }
        });


        mMessageAdapter = new MessageListAdapter(this, messageList);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);

        if (user != null) {
            ToastNew.showToast(this, "Bạn và chuyên gia đã được kết nối với nhau! " + user.getUser_id(), Toast.LENGTH_LONG);
            mSocket.emit("user-ready-thao-luan", sessionManager.getAccount());
        }

        if (expert != null) {
            ToastNew.showToast(this, "Bạn và người đặt câu hỏi đã được kết nối với nhau! " + expert.getExpert_id(), Toast.LENGTH_LONG);
            mSocket.emit("expert-ready-thao-luan", sessionManager.getAccount());
        }


        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void AnhXa() {
        btn_picture = findViewById(R.id.btn_picture);
        btn_send = findViewById(R.id.btn_send);
        edt_message = findViewById(R.id.edt_message);
        btn_camera = findViewById(R.id.btn_camera);
        mMessageRecycler = findViewById(R.id.reyclerview_message_list);
        mShadowView=findViewById(R.id.shadowView);
        btn_close = findViewById(R.id.btn_close);
        btn_question = findViewById(R.id.btn_question);
    }


    private void initEventButton() {
        btn_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToolSupport.choosePicture(MessageListActivity.this, REQUEST_CHOOSE_PHOTO);
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = edt_message.getText().toString();
                if (message.isEmpty()) return;
                Message message_new = new Message(conversation_id, user != null ? user.getUser_id() : expert.getExpert_id(), message, false);
                messageList.add(message_new);
                mMessageAdapter.notifyItemInserted(messageList.size() - 1);
                mMessageRecycler.smoothScrollToPosition(mMessageAdapter.getItemCount());
                sendMessage(message_new);
                edt_message.setText("");
            }
        });

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToolSupport.take_picture(MessageListActivity.this, REQUEST_TAKE_PHOTO);
            }
        });

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingMenu.collapseImmediately();
                onBackPressed();
            }
        });

        floatingMenu = findViewById(R.id.floatingMenu);

        floatingMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                mShadowView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                mShadowView.setVisibility(View.GONE);
            }
        });


        btn_question.setEnabled(false);
        btn_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageListActivity.this, DetailQuestionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("question", question);
                bundle.putBoolean("local", true);
                intent.putExtras(bundle);
                startActivity(intent);
                floatingMenu.collapseImmediately();
            }
        });
    }

    private void sendMessage(Message message_new) {
        mSocket.emit("client-send-message-to-other-people", message_new.toJSON(), messageList.size() - 1);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (requestCode == REQUEST_CHOOSE_PHOTO && resultCode == RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        InputStream is = getContentResolver().openInputStream(uri);
                        Bitmap bm = BitmapFactory.decodeStream(is);

                        Message message_new = new Message(conversation_id, user != null ? user.getUser_id() : expert.getExpert_id(), "", true, false, bm);
                        messageList.add(message_new);
                        mMessageAdapter.notifyItemInserted(messageList.size() - 1);
                        mMessageRecycler.smoothScrollToPosition(mMessageAdapter.getItemCount());
                        upload(message_new);
                    } catch (Exception ignored) {
                    }
                } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
                    if (data.getExtras() == null) return;

                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    Message message_new = new Message(conversation_id, user != null ? user.getUser_id() : expert.getExpert_id(), "", true, false, bm);
                    messageList.add(message_new);
                    mMessageAdapter.notifyItemInserted(messageList.size() - 1);
                    mMessageRecycler.smoothScrollToPosition(mMessageAdapter.getItemCount());
                    upload(message_new);
                }
            }
        });

    }

    private void upload(final Message message_new) {
        Calendar calendar = Calendar.getInstance();
        final String filename = "images" + calendar.getTimeInMillis() + ".png";
        final StorageReference mountainsRef = mStorageRef.child(filename);
        // Get the data from an ImageView as bytes
        //iv_upload_image.setDrawingCacheEnabled(true);
        //iv_upload_image.buildDrawingCache();
        //Bitmap bitmap = ((BitmapDrawable) iv_upload_image.getDrawable()).getBitmap();
        Bitmap bitmap = message_new.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mountainsRef.putBytes(data);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                ToastNew.showToast(getApplicationContext(), "Lỗi", Toast.LENGTH_LONG);
                messageList.remove(message_new);
                mMessageAdapter.notifyDataSetChanged();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                message_new.setStatus(true);
                message_new.setMessage(filename);
                mMessageAdapter.notifyDataSetChanged();
                sendMessage(message_new);
            }
        });
    }

    private Emitter.Listener callback_nhantinnhan = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        String message = data.getString("message");
                        Gson gson = new Gson();
                        Message message_new = gson.fromJson(message, Message.class);
                        messageList.add(message_new);
                        mMessageAdapter.notifyDataSetChanged();
                        mMessageRecycler.smoothScrollToPosition(mMessageAdapter.getItemCount());
                    } catch (Exception ignored) {
                    }
                }
            });
        }
    };

    private Emitter.Listener callback_gioithieu = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (bundle != null) {
                            String tinnhangioithieu = bundle.getString("tinnhangioithieu", "");
                            if (!tinnhangioithieu.isEmpty()) {
                                Message message = new Message(conversation_id, expert.getExpert_id(), tinnhangioithieu, false);
                                messageList.add(message);
                                mMessageAdapter.notifyDataSetChanged();
                                mSocket.emit("client-send-message-to-other-people", message.toJSON());
                                mSocket.off("user-ready-thao-luan");
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
            });
        }
    };

    @Override
    public void onBackPressed() {
        ask();
    }

    public void ask() {
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Bạn muốn kết thúc cuộc thảo luận này?")
                    .setCancelable(false)
                    .setPositiveButton("Vâng", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();

                            if (user != null) {
                                Intent intent = new Intent(MessageListActivity.this, RatingForExpertActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                Bundle bundle = new Bundle();
                                bundle.putInt("conversation_id", conversation_id);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }

                            mSocket.emit("client-roi-cuoc-thao-luan", conversation_id);

                            finish();
                        }
                    })
                    .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!mSocket.connected()) return;

        if (expert != null)
            mSocket.emit("expert-refresh-information", sessionManager.getAccount());
        else if (user != null) {
            mSocket.emit("user-refresh-information", sessionManager.getAccount());
        }
    }


    private void dowloadImage(String filename) {
        StorageReference islandRef = mStorageRef.child(filename);

        final long ONE_MEGABYTE = 1024 * 1024 * 100;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                btn_question.setEnabled(true);

                Bitmap bitmap = ToolSupport.convertByteArrayToBitmap(bytes);
                String filename = ToolSupport.saveToInternalStorage(bitmap, MessageListActivity.this);
                question.setImage(filename);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }
}
