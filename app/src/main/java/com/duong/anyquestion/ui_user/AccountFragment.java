package com.duong.anyquestion.ui_user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.duong.anyquestion.ChangePasswordActivity;
import com.duong.anyquestion.NapTienActivity;
import com.duong.anyquestion.R;
import com.duong.anyquestion.EditInfomationActivity;
import com.duong.anyquestion.Tool.ToolSupport;
import com.duong.anyquestion.classes.ConnectThread;
import com.duong.anyquestion.SecurityActivity;
import com.duong.anyquestion.classes.Expert;
import com.duong.anyquestion.classes.SessionManager;
import com.duong.anyquestion.classes.ToastNew;
import com.duong.anyquestion.classes.User;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {

    private SessionManager sessionManager;
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    private Socket mSocket = ConnectThread.getInstance().getSocket();
    private Bundle bundle_update;
    private TextView tv_fullname, tv_email, tv_address, tv_money;
    private CircleImageView im_avatar;
    private View view;
    private User user;
    private String avatar_path = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_account_user, container, false);

        Init();
        sessionManager.checkLogin();
        ShowThongTinNguoiDung();
        Event();

        mSocket.on("ketqua-logout", callback_logout);
        mSocket.on("server-to-update-status", callback_update_information);
        return view;
    }

    private void Event() {
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

        Button btn_change_password = view.findViewById(R.id.btn_change_password);
        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        Button btn_logout = view.findViewById(R.id.btn_dangxuat);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSocket.connected()) {
                    mSocket.emit("logout", "user");
                } else {
                    ToastNew.showToast(getActivity(), "Máy chủ ngắt kết nối!", Toast.LENGTH_LONG);
                }
            }
        });


        Button btn_security =view.findViewById(R.id.btn_security);
        btn_security.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), SecurityActivity.class);
                startActivityForResult(intent, REQUEST_CODE_EDIT_INFORMATION);

            }
        });


        Button btn_nap_tien = view.findViewById(R.id.btn_nap_tien);
        btn_nap_tien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NapTienActivity.class);
                startActivityForResult(intent, REQUEST_CODE_EDIT_INFORMATION);
            }
        });

    }

    private void ShowThongTinNguoiDung() {
        tv_fullname.setText(user.getFullName());
        tv_email.setText(user.getEmail());
        tv_address.setText(user.getAddress());
        tv_money.setText(user.getMoney() + "");

        avatar_path = user.getAvatar();

        Bitmap bitmap = ToolSupport.loadAvatar(getActivity(), user.getAvatar());
        if (bitmap != null)
            im_avatar.setImageBitmap(bitmap);
        dowloadImage(user.getAvatar());
    }

    private void Init() {
        sessionManager = new SessionManager((Activity) view.getContext());
        user = sessionManager.getUser();
        im_avatar = view.findViewById(R.id.im_avatar);
        tv_fullname = view.findViewById(R.id.tv_fullname);
        tv_email = view.findViewById(R.id.tv_email);
        tv_address = view.findViewById(R.id.tv_address);
        tv_money = view.findViewById(R.id.tv_money);

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

                    Log.d("test", bundle_update.getString("avatar_path"));

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
                            Toast.makeText(getActivity(), "Đăng xuất thất bại!", Toast.LENGTH_LONG).show();
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

                            User update_user = sessionManager.getUser();
                            update_user.setFullName(fullname);
                            update_user.setEmail(email);
                            update_user.setAddress(address);
                            update_user.setAvatar(bundle_update.getString("avatar_firebase_path", ""));

                            sessionManager.createSession(update_user);
                        } else {
                        }

                    } catch (JSONException ignored) { }

                }
            });
        }
    };


    public void UpdateMoney(int balance) {
        tv_money.setText(balance + "");
    }

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

}