package com.binus.takenote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.binus.takenote.model.Note;
import com.binus.takenote.model.NoteDB;
import com.binus.takenote.model.ObjectTypeInfoHelper;
import com.huawei.agconnect.AGCRoutePolicy;
import com.huawei.agconnect.AGConnectInstance;
import com.huawei.agconnect.AGConnectOptionsBuilder;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.cloud.database.AGConnectCloudDB;
import com.huawei.agconnect.cloud.database.CloudDBZone;
import com.huawei.agconnect.cloud.database.CloudDBZoneConfig;
import com.huawei.agconnect.cloud.database.Text;
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

    private TextView tvStatus, backBtn, btnDark, btnPurple, btnOrange, btnYellow;
    private EditText etTitle, etDetail;
    private static final String TAG = "DetailActivity";
    private LinearLayout themeList;
    private int themeCode, titleColor, contentColor;
    private RelativeLayout background;
    SharedPreferences sharedPreferences;
    Note note = null;
    ImageView icDelete, icBrush, icCopy;

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
        icBrush = findViewById(R.id.ic_brush);
        icCopy = findViewById(R.id.ic_copy);
        themeList = findViewById(R.id.theme_list);
        btnDark = createThemeButton(R.id.btn_dark, 1);
        btnPurple = createThemeButton(R.id.btn_purple, 2);
        btnOrange = createThemeButton(R.id.btn_orange, 3);
        btnYellow = createThemeButton(R.id.btn_yellow, 4);
        background = findViewById(R.id.background);

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
            themeCode = note.getColor();
        } else {
            String newDateStr = postFormater.format(new Date());
            tvStatus.setText(newDateStr + " | " + "0 character(s)");
            themeCode = 1;
        }

        changeTheme();

        backBtn.setOnClickListener(v -> {
            updateNoteData();
            startActivity(new Intent(DetailActivity.this, HomeActivity.class));
            finish();
        });

        icDelete.setOnClickListener(v -> {
            deleteNote();
        });

        icBrush.setOnClickListener(v -> {
            themeList.setVisibility(themeList.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        });

        icCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied Text", "Header: " + etTitle.getText().toString() + "\n"
            + "Content:\n" + etDetail.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(DetailActivity.this, "Text Copied!", Toast.LENGTH_SHORT).show();
        });

    }

    private TextView createThemeButton(int buttonId, int themeNumber){
        TextView temp = findViewById(buttonId);
        temp.setOnClickListener(v -> {
            themeCode = themeNumber;
            themeList.setVisibility(View.GONE);
            changeTheme();
        });
        return temp;
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
        n.setColor(themeCode);

        if (mCloudDBZone == null) {
            Log.w(TAG, "CloudDBZone is null, try re-open it");
            return;
        }

        Task<Integer> upsertTask = mCloudDBZone.executeUpsert(n);
        pushNotification("Successfully Update Note", "Don't worry, your progress already been saved!");
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
            pushNotification("Successfully Delete Note", "Don't worry, your note is deleted!");
            startActivity(new Intent(DetailActivity.this, HomeActivity.class));
            finish();
        }).addOnFailureListener(e -> {
            Log.i(TAG, "Gagal karena" + e.getMessage());
            pushNotification("Failed to Delete Note", "Sorry, we can't deleted your note. Please try again!");
            startActivity(new Intent(DetailActivity.this, HomeActivity.class));
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        updateNoteData();
        pushNotification("Successfully Update Note", "Don't worry, your progress already been saved!");
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

    private void pushNotification(String title, String content){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "id";
        String channelName = "name";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.icon)
                .setContentText(content);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(1, builder.build());
    }

    private void changeTheme(){
        int color = -1;
        switch (themeCode){
            case 1:
                color = R.color.secondary;
                titleColor = R.color.gray;
                contentColor = R.color.light_gray;
                break;
            case 2:
                color = R.color.primaryPurple;
                titleColor = R.color.gray;
                contentColor = R.color.gray;
                break;
            case 3:
                color = R.color.primaryOrange;
                titleColor = R.color.light_gray;
                contentColor = R.color.light_gray;
                break;
            default:
                color = R.color.primaryYellow;
                titleColor = R.color.light_gray;
                contentColor = R.color.light_gray;
                break;
        }

        titleColor = ContextCompat.getColor(this, titleColor);
        contentColor = ContextCompat.getColor(this, contentColor);

        backBtn.setTextColor(contentColor);
        icBrush.setColorFilter(contentColor);
        icDelete.setColorFilter(contentColor);
        tvStatus.setTextColor(contentColor);

        etTitle.getBackground().setTint(titleColor);
        etTitle.setHintTextColor(titleColor);
        etTitle.setTextColor(titleColor);

        etDetail.setHintTextColor(contentColor);
        etDetail.setTextColor(contentColor);

        background.setBackgroundColor(ContextCompat.getColor(this, color));
    }

}