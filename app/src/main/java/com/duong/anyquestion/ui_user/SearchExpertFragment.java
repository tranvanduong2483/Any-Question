package com.duong.anyquestion.ui_user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.duong.anyquestion.LoadingSearchExpertActivity;
import com.duong.anyquestion.R;
import com.duong.anyquestion.Tool.ToolSupport;
import com.duong.anyquestion.classes.ConnectThread;
import com.duong.anyquestion.classes.Field;
import com.duong.anyquestion.classes.Question;
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class SearchExpertFragment extends Fragment {

    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    private Socket mSocket = ConnectThread.getInstance().getSocket();
    private EditText edt_tille, edt_note;
    private TextView tv_money_du, tv_cost;
    private final int REQUEST_TAKE_PHOTO = 123;
    private final int REQUEST_CHOOSE_PHOTO = 132;
    private View view;
    private ImageView iv_image;
    private String question_filename = null;
    private ArrayList<Field> array_field;
    private ArrayAdapter<Field> adapter_field;
    private Spinner spn_field;
    private ProgressBar pb_loading_field;
    private   Button btn_search_expert;
    private SessionManager sessionManager;
    private ProgressBar pb_loading;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_expert, container, false);

        sessionManager = new SessionManager(getActivity());

        edt_tille = view.findViewById(R.id.edt_tittle);
        edt_note = view.findViewById(R.id.edt_note);
        tv_money_du = view.findViewById(R.id.tv_money_du);
        tv_cost = view.findViewById(R.id.tv_cost);

        iv_image = view.findViewById(R.id.iv_image);
        spn_field =view.findViewById(R.id.spn_field);
        pb_loading = view.findViewById(R.id.pb_loading);
        pb_loading_field= view.findViewById(R.id.pb_loading_field);
        spn_field.setVisibility(View.GONE);

        int cost = 15000;
        tv_cost.setText(cost + "");
        int blance = sessionManager.getUser().getMoney();
        UpdateMoney(blance);

        Bitmap avatar_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.math_example);
        avatar_bitmap = ToolSupport.resize(avatar_bitmap, 300, 300);
        iv_image.setImageBitmap(ToolSupport.BitmapWithRoundedCorners(avatar_bitmap));



        array_field=new ArrayList<>();
        array_field.add(new Field(-1, "Chọn lĩnh vực"));

        adapter_field = new ArrayAdapter<Field>(view.getContext(), android.R.layout.simple_spinner_item, array_field){
                @Override
                public boolean isEnabled(int position) {
                    if (position == 0) {
                        return false;
                    } else {
                        return true;
                    }
                }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spn_field.setAdapter(adapter_field);

         btn_search_expert = view.findViewById(R.id.btn_search_expert);
        btn_search_expert.setEnabled(false);
        btn_search_expert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tittle = edt_tille.getText() + "";
                int field_id = array_field.get(spn_field.getSelectedItemPosition()).getField_id();
                String note = edt_note.getText() + "";
                int money = Integer.parseInt(tv_money_du.getText().toString());
                int cost = Integer.parseInt(tv_cost.getText().toString());


                SessionManager sessionManager = new SessionManager(getActivity());

                Question question = new Question(field_id, tittle, question_filename, note, cost, sessionManager.getAccount());

                if (money < 0) {
                    ToastNew.showToast(getActivity(), "Số dư còn lại không đủ!", Toast.LENGTH_SHORT);
                    return;
                }

                if (question_filename == null) {
                    ToastNew.showToast(getActivity(), "Thiếu ảnh", Toast.LENGTH_SHORT);
                    return;
                }

                if (tittle.isEmpty() ||spn_field.getSelectedItemPosition()==0 ) {
                    ToastNew.showToast(getActivity(), "Thiếu thông tin", Toast.LENGTH_SHORT);
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putSerializable("question", question);
                Intent intent = new Intent(view.getContext(), LoadingSearchExpertActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        Button btn_clear = view.findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_tille.setText("");
                edt_note.setText("");
                iv_image.setImageResource(R.drawable.math_example);
                question_filename = null;
                spn_field.setSelection(0);
            }
        });

        Button btn_upload = view.findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CHOOSE_PHOTO);

            }
        });

        Button btn_camera = view.findViewById(R.id.btn_camera);
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
            }
        });


        tv_cost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCost();
            }
        });

        mSocket.on("server-sent-field",callback_get_field);
        setGetFiled();
        return view;
    }

    private void changeCost() {
        int cost = Integer.parseInt(tv_cost.getText().toString());
        cost += 5000;
        if (cost > 100000) cost = 15000;
        tv_cost.setText(cost + "");
        UpdateMoney(sessionManager.getUser().getMoney());
    }


    private void setGetFiled() {
        mSocket.emit("client-get-field", "client-get-field *****");

        TimerTask timertaks = new TimerTask() {
            @Override
            public void run() {
                if (array_field.isEmpty() || array_field.size()==1) {

                    if (mSocket.connected())
                        mSocket.emit("client-get-field", "client-get-field *****");
                }
            }
        };

        long delay = 3000L;
        Timer timer = new Timer("Timer");
        timer.schedule(timertaks, 0, delay);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) return;

        if (requestCode == REQUEST_CHOOSE_PHOTO && resultCode == Activity.RESULT_OK) {
            try {
                Uri uri = data.getData();
                InputStream is = view.getContext().getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                question_filename = null;
                iv_image.setVisibility(View.INVISIBLE);
                pb_loading.setVisibility(View.VISIBLE);
                upload(bitmap);

            } catch (Exception ignored) {
            }
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            if (data.getExtras() == null) return;
            question_filename = null;
            iv_image.setVisibility(View.INVISIBLE);
            pb_loading.setVisibility(View.VISIBLE);
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            upload(bitmap);
        }

    }


    private void upload(final Bitmap bitmap) {
        if (bitmap == null) return;
        Calendar calendar = Calendar.getInstance();
        final String filename = sessionManager.getAccount() + "_question_images_" + +calendar.getTimeInMillis() + ".png";
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
                question_filename = filename;
                iv_image.setVisibility(View.VISIBLE);
                pb_loading.setVisibility(View.GONE);
                iv_image.setImageBitmap(ToolSupport.BitmapWithRoundedCorners(bitmap));
            }
        });
    }



    private Emitter.Listener callback_get_field = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Activity activity = getActivity();
            if (activity==null) return;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        array_field.clear();
                        array_field.add(new Field(-1, "Chọn lĩnh vực"));
                        JSONArray jsonArray = (JSONArray) args[0];
                        Gson gson = new Gson();
                        for (int i=0; i<jsonArray.length(); i++){
                            Field field = gson.fromJson( jsonArray.get(i).toString(),Field.class);
                            array_field.add(field);
                        }
                        adapter_field.notifyDataSetChanged();

                        if (array_field.size()!=1 && array_field.size()!=0){
                            pb_loading_field.setVisibility(View.GONE);
                            spn_field.setVisibility(View.VISIBLE);
                            btn_search_expert.setEnabled(true);
                        }

                    } catch (Exception ignored) { }

                }
            });
        }
    };


    public void UpdateMoney(int balance) {
        int cost = Integer.parseInt(tv_cost.getText().toString());
        tv_money_du.setText((balance - cost) + "");
    }


}

