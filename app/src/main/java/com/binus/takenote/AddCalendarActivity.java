package com.binus.takenote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.binus.takenote.model.ActivityDB;
import com.binus.takenote.model.NoteDB;
import com.binus.takenote.model.ObjectTypeInfoHelper;
import com.huawei.agconnect.AGCRoutePolicy;
import com.huawei.agconnect.AGConnectInstance;
import com.huawei.agconnect.AGConnectOptionsBuilder;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.cloud.database.AGConnectCloudDB;
import com.huawei.agconnect.cloud.database.CloudDBZone;
import com.huawei.agconnect.cloud.database.CloudDBZoneConfig;
import com.huawei.hmf.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddCalendarActivity extends AppCompatActivity {

    private static final String TAG = "AddCalendar";
    TextView titleTxt, dateTxt, descriptionTxt, backBtn;
    Button addBtn;
    Calendar calendar;
    SharedPreferences sharedPreferences;
    AGConnectCloudDB mCloudDB;
    CloudDBZoneConfig mConfig;
    CloudDBZone mCloudDBZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_calendar);

        titleTxt = findViewById(R.id.titleTxt);
        dateTxt = findViewById(R.id.dateTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        backBtn = findViewById(R.id.back_btn);
        addBtn = findViewById(R.id.addBtn);

        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, CalendarActivity.class));
        });

        sharedPreferences = this.getSharedPreferences("UserId", Context.MODE_PRIVATE);
        initDatabase();
        initializeDatePicker();
        initializeAddBtn();
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

    private void initializeAddBtn(){
        addBtn.setOnClickListener(v -> {
            String id = UUID.randomUUID().toString();
            String title = titleTxt.getText().toString();
            String description = descriptionTxt.getText().toString();
            Date date = new Date(calendar.getTimeInMillis());

            ActivityDB a = new ActivityDB();
            a.setId(id);
            a.setTitle(title);
            a.setDescription(description);
            a.setUserId(sharedPreferences.getString("id", null));
            a.setDate(date);

            if (mCloudDBZone == null) {
                Log.w(TAG, "CloudDBZone is null, try re-open it");
                return;
            }

            Task<Integer> upsertTask = mCloudDBZone.executeUpsert(a);
            upsertTask.addOnSuccessListener(cloudDBZoneResult -> Log.i(TAG, "Upsert " + cloudDBZoneResult + " records"))
                    .addOnFailureListener(e -> Log.i(TAG, "Gagal karena "+  e.getMessage()));

            startActivity(new Intent(this, CalendarActivity.class));
        });
    }

    private void initializeDatePicker(){
        DatePickerDialog.OnDateSetListener listener = (view, year, month, dayOfMonth) -> {
            calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH,month);
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            updateLabel(calendar);
        };

        dateTxt.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(AddCalendarActivity.this, listener, c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();
        });
    }


    private void updateLabel(Calendar calendar){
        String format = "dd/MM/yyyy";
        SimpleDateFormat dateFormat= new SimpleDateFormat(format, Locale.US);
        dateTxt.setText(dateFormat.format(calendar.getTime()));
    }

}
