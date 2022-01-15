package com.binus.takenote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.binus.takenote.model.Note;
import com.binus.takenote.model.NoteDB;
import com.binus.takenote.model.ObjectTypeInfoHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.huawei.agconnect.AGCRoutePolicy;
import com.huawei.agconnect.AGConnectInstance;
import com.huawei.agconnect.AGConnectOptionsBuilder;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.cloud.database.AGConnectCloudDB;
import com.huawei.agconnect.cloud.database.CloudDBZone;
import com.huawei.agconnect.cloud.database.CloudDBZoneConfig;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    private TextView tvStatus, backBtn;
    private EditText etTitle, etDetail;
    private static final String TAG = "DetailActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences sharedPreferences;
    Note note = null;
    ImageView icDelete;

    AGConnectCloudDB mCloudDB;
    CloudDBZoneConfig mConfig;
    CloudDBZone mCloudDBZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        init();
        setText();
    }

    private void init(){
        tvStatus = findViewById(R.id.tv_status);
        etTitle = findViewById(R.id.et_title);
        etDetail = findViewById(R.id.et_detail);
        backBtn = findViewById(R.id.back_btn);
        icDelete = findViewById(R.id.ic_delete);
        initDatabase();

        sharedPreferences = this.getSharedPreferences("UserId", Context.MODE_PRIVATE);

        SimpleDateFormat postFormater = new SimpleDateFormat("MMM dd, yyyy");

        note = getIntent().getParcelableExtra("note");
        if (note != null){
            String newDateStr = postFormater.format(note.getLastEdited());
            etTitle.setText(note.getTitle());
            etDetail.setText(note.getContent());
            int length = note.getContent().trim()
                    .replace(" ", "")
                    .replace("\n", "").length();
            tvStatus.setText(newDateStr + " | " + length + " character(s)");
        } else {
            String newDateStr = postFormater.format(new Date());
            tvStatus.setText(newDateStr + " | " + "0 character(s)");
        }

        backBtn.setOnClickListener(v -> {
            updateNoteData();
            startActivity(new Intent(DetailActivity.this, HomeActivity.class))         ;
            finish();
        });

        icDelete.setOnClickListener(v -> {
            deleteNote();
        });
    }

    private void initDatabase() {
        AGConnectCloudDB.initialize(this);
        AGConnectInstance instance = AGConnectInstance.buildInstance(new AGConnectOptionsBuilder().setRoutePolicy(AGCRoutePolicy.SINGAPORE).build(this));
        mCloudDB = AGConnectCloudDB.getInstance(instance, AGConnectAuth.getInstance());
        try {
            mCloudDB.createObjectType(ObjectTypeInfoHelper.getObjectTypeInfo());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mConfig = new CloudDBZoneConfig("Takenote",
                CloudDBZoneConfig.CloudDBZoneSyncProperty.CLOUDDBZONE_CLOUD_CACHE,
                CloudDBZoneConfig.CloudDBZoneAccessProperty.CLOUDDBZONE_PUBLIC);

        mConfig.setPersistenceEnabled(true);
        Task<CloudDBZone> openDBZoneTask = mCloudDB.openCloudDBZone2(mConfig, true);
        openDBZoneTask.addOnSuccessListener(cloudDBZone -> {
            Log.i(TAG, "open cloudDBZone success");
            mCloudDBZone = cloudDBZone;
        }).addOnFailureListener(e -> Log.w(TAG, "open cloudDBZone failed for " + e.getMessage()));
    }

    public void updateNoteData() {
        String id = getIntent().getStringExtra("noteId");
        String title = etTitle.getText().toString();
        String content = etDetail.getText().toString();
        Date date = new Date();

        NoteDB n = new NoteDB();
        n.setId(id);
        n.setUserId(sharedPreferences.getString("id", null));
        n.setTitle(title);
        n.setContent(content);
        n.setDate(date);

        if (mCloudDBZone == null) {
            Log.w(TAG, "CloudDBZone is null, try re-open it");
            return;
        }

        Task<Integer> upsertTask = mCloudDBZone.executeUpsert(n);
        upsertTask.addOnSuccessListener(cloudDBZoneResult -> Log.i(TAG, "Upsert " + cloudDBZoneResult + " records"))
                .addOnFailureListener(e -> Log.i(TAG, "Gagal karena "+  e.getMessage()));
    }

    private void deleteNote(){
        if (mCloudDBZone == null) {
            Log.w(TAG, "CloudDBZone is null, try re-open it");
            return;
        }

        NoteDB n = new NoteDB();
        n.setId(note.getId());
        Log.i(TAG, "id = " +n.getId());

        Task<Integer> deleteTask = mCloudDBZone.executeDelete(n);
        deleteTask.addOnSuccessListener(integer -> {
            startActivity(new Intent(DetailActivity.this, HomeActivity.class));
            finish();
        }).addOnFailureListener(e -> {
            Log.i(TAG, "Gagal karena" + e.getMessage());
            startActivity(new Intent(DetailActivity.this, HomeActivity.class));
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        updateNoteData();
        Intent i = new Intent(DetailActivity.this, HomeActivity.class);
        startActivity(i);
        finish();
    }

    private void setText(){
        etDetail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                SimpleDateFormat postFormater = new SimpleDateFormat("MMM dd, yyyy");
                String newDateStr = postFormater.format(new Date());
                int length = editable.toString().trim()
                        .replace(" ", "")
                        .replace("\n", "").length();
                tvStatus.setText(newDateStr + " | " + length + " character(s)");
            }
        });
    }

}