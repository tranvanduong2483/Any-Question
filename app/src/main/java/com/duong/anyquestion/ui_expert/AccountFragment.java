package com.duong.anyquestion.ui_expert;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.duong.anyquestion.ChangePasswordActivity;
import com.duong.anyquestion.EditInfomationActivity;
import com.duong.anyquestion.R;
import com.duong.anyquestion.RutTienActivity;
import com.duong.anyquestion.SecurityActivity;
import com.duong.anyquestion.Tool.ToolSupport;
import com.duong.anyquestion.classes.ConnectThread;
import com.duong.anyquestion.classes.Education;
import com.duong.anyquestion.classes.Expert;
import com.duong.anyquestion.classes.Field;
import com.duong.anyquestion.classes.SessionManager;
import com.duong.anyquestion.classes.ToastNew;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {
    private SessionManager sessionManager;
    private Socket mSocket = ConnectThread.getInstance().getSocket();
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    private View view;
    private Bundle bundle_update;
    private TextView tv_fullname, tv_education, tv_field, tv_email, tv_address, tv_money, tv_socuocthaoluan, tv_saoTB;
    private CircleImageView im_avatar;
    private Expert expert;
    private String avatar_path = null;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account_expert, container, false);
        Init();
        sessionManager.checkLogin();
        ShowThongTinChuyengia();

        Event();

        mSocket.on("ketqua-logout", callback_logout);
        mSocket.on("server-to-update-status", callback_update_information);
        return view;
    }

    private void Event() {
        ImageButton btn_dangxuat = view.findViewById(R.id.btn_dangxuat);
        btn_dangxuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSocket.connected()) {
                    mSocket.emit("logout", "expert");
                } else {
                    ToastNew.showToast(getActivity(), "Máy chủ ngắt kết nối!", Toast.LENGTH_LONG);
                }
            }
        });

        Button btn_change_password = view.findViewById(R.id.btn_change_password);
        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inten = new Intent(getContext(), ChangePasswordActivity.class);
                startActivity(inten);
            }
        });

        Button btn_edit = view.findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("avatar_path", avatar_path);
                bundle.putString("name", tv_fullname.getText() + "");
                bundle.putString("email", tv_email.getText() + "");
                bundle.putString("address", tv_address.getText() + "");
                Intent intent = new Intent(getContext(), EditInfomationActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_CODE_EDIT_INFORMATION);
            }
        });

        Button btn_security = view.findViewById(R.id.btn_security);
        btn_security.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SecurityActivity.class);
                startActivityForResult(intent, REQUEST_CODE_EDIT_INFORMATION);
            }
        });


        Button btn_rut_tien = view.findViewById(R.id.btn_rut_tien);
        btn_rut_tien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("avatar", sessionManager.getAvatar());
                bundle.putString("name", tv_fullname.getText() + "");
                bundle.putString("email", tv_email.getText() + "");
                bundle.putString("address", tv_address.getText() + "");
                bundle.putString("balance", tv_money.getText() + "");
                bundle.putString("account", sessionManager.getAccount());

                Intent intent = new Intent(getContext(), RutTienActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        mSocket.emit("client-get-education");
        mSocket.once("server-sent-education", callback_get_education);

        mSocket.emit("client-get-field");
        mSocket.once("server-sent-field", callback_get_field);
    }

    private Emitter.Listener callback_get_education = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity) view.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {

                        JSONArray jsonArray = (JSONArray) args[0];

                        Gson gson = new Gson();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Education education = gson.fromJson(jsonArray.get(i).toString(), Education.class);
                            if (education.getEducation_id() == expert.getEducation_id()) {
                                tv_education.setText(education.getName());
                                break;
                            }
                        }
                    } catch (Exception ignored) {
                    }

                }
            });
        }
    };


    private Emitter.Listener callback_get_field = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity) view.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONArray jsonArray = (JSONArray) args[0];
                        Gson gson = new Gson();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Field field = gson.fromJson(jsonArray.get(i).toString(), Field.class);
                            if (field.getField_id() == expert.getField_id()) {
                                tv_field.setText(field.getName());
                                break;
                            }
                        }
                    } catch (Exception ignored) {
                    }

                }
            });
        }
    };


    private void ShowThongTinChuyengia() {
        tv_fullname.setText(expert.getFullName());
        tv_education.setText(expert.getEducation_id() + "");
        tv_field.setText(expert.getField_id() + "");
        tv_email.setText(expert.getEmail());
        tv_address.setText(expert.getAddress());
        tv_money.setText(expert.getMoney() + "");

        avatar_path = expert.getAvatar();

        Bitmap bitmap = ToolSupport.loadAvatar(getActivity(), expert.getAvatar());
        if (bitmap != null)
            im_avatar.setImageBitmap(bitmap);
        dowloadImage(expert.getAvatar());
    }

    private void Init() {
        sessionManager = new SessionManager((Activity) view.getContext());
        im_avatar = view.findViewById(R.id.im_avatar);
        tv_fullname = view.findViewById(R.id.tv_fullname);
        tv_education = view.findViewById(R.id.tv_education);
        tv_field = view.findViewById(R.id.tv_field);
        tv_email = view.findViewById(R.id.tv_email);
        tv_address = view.findViewById(R.id.tv_address);
        tv_money = view.findViewById(R.id.tv_money);
        tv_socuocthaoluan = view.findViewById(R.id.tv_socuocthaoluan);
        tv_saoTB = view.findViewById(R.id.tv_saoTB);
        expert = sessionManager.getExpert();
    }

    private int REQUEST_CODE_EDIT_INFORMATION = 101;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_EDIT_INFORMATION) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    bundle_update = data.getExtras();
                    JSONObject json_update = new JSONObject();
                    json_update.put("account", sessionManager.getAccount());
                    json_update.put("name", bundle_update.getString("name"));
                    json_update.put("address", bundle_update.getString("address"));
                    json_update.put("email", bundle_update.getString("email"));
                    Bitmap bitmap = ToolSupport.loadImageFromStorage(bundle_update.getString("avatar_path"));
                    upload(json_update, bitmap);

                } catch (JSONException ignored) {
                }
            }
        }
    }


    private void upload(final JSONObject json_update, final Bitmap bitmap) {
        if (bitmap == null) return;
        final String filename = "avatar_" + sessionManager.getAccount() + ".png";
        final StorageReference mountainsRef = mStorageRef.child(filename);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mountainsRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                try {
                    json_update.put("avatar_firebase_path", filename);
                    bundle_update.putString("avatar_firebase_path", filename);
                    mSocket.emit("client-to-update-data", json_update.toString(), sessionManager.getType());
                    ToolSupport.saveAvatar(bitmap, getActivity(), filename);
                } catch (Exception ignored) {
                }
            }
        });
    }


    private Emitter.Listener callback_logout = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity) view.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        String NoiDung = data.getString("ketqua");
                        if ("true".equals(NoiDung)) {
                            sessionManager.logout();
                        } else {
                            ToastNew.showToast(getActivity(), "Đăng xuất thất bại!", Toast.LENGTH_LONG);
                        }
                    } catch (JSONException ignored) { }
                }
            });
        }
    };


    private Emitter.Listener callback_update_information = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity) view.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        String NoiDung;
                        NoiDung = data.getString("status");
                        if ("1".equals(NoiDung)) {
                            if (bundle_update == null) return;

                            String fullname = bundle_update.getString("name", "");
                            String address = bundle_update.getString("address", "");
                            String email = bundle_update.getString("email", "");
                            String avatar = bundle_update.getString("avatar_path", "");
                            Bitmap bitmap = ToolSupport.loadImageFromStorage(avatar);

                            if (bitmap!=null)
                                im_avatar.setImageBitmap(bitmap);
                            tv_address.setText(address);
                            tv_email.setText(email);
                            tv_fullname.setText(fullname);

                            Expert update_expert = sessionManager.getExpert();
                            update_expert.setFullName(fullname);
                            update_expert.setEmail(email);
                            update_expert.setAddress(address);
                            update_expert.setAvatar(bundle_update.getString("avatar_firebase_path", ""));

                            sessionManager.createSession(update_expert);
                        } else {
                        }

                    } catch (JSONException ignored) { }

                }
            });
        }
    };

    private void dowloadImage(final String name) {
        if (name == null || name.isEmpty()) return;
        StorageReference islandRef = mStorageRef.child(name);
        final long ONE_MEGABYTE = 1024 * 1024 * 100;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = ToolSupport.convertByteArrayToBitmap(bytes);
                im_avatar.setImageBitmap(bitmap);
                ToolSupport.saveAvatar(bitmap, getActivity(), name);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }

    public void UpdateMoney(int balance) {
        tv_money.setText(balance + "");
    }

    public void UpdateSoCuocThaoLuan(int SoCuocThaoLuan) {
        tv_socuocthaoluan.setText(SoCuocThaoLuan + "");
    }

    public void UpdateSoSaoTB(float SaoTB) {
        tv_saoTB.setText(Math.round(SaoTB * 100) / 100f + "");
    }
}