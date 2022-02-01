package com.binus.takenote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.binus.takenote.adapter.ActivityAdapter;
import com.binus.takenote.adapter.NoteAdapter;
import com.binus.takenote.model.Activity;
import com.binus.takenote.model.ActivityDB;
import com.binus.takenote.model.Note;
import com.binus.takenote.model.NoteDB;
import com.binus.takenote.model.ObjectTypeInfoHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.huawei.agconnect.AGCRoutePolicy;
import com.huawei.agconnect.AGConnectInstance;
import com.huawei.agconnect.AGConnectOptionsBuilder;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.cloud.database.AGConnectCloudDB;
import com.huawei.agconnect.cloud.database.CloudDBZone;
import com.huawei.agconnect.cloud.database.CloudDBZoneConfig;
import com.huawei.agconnect.cloud.database.CloudDBZoneObjectList;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;
import com.huawei.agconnect.cloud.database.CloudDBZoneSnapshot;
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException;
import com.huawei.hmf.tasks.Task;

import java.util.ArrayList;

public class CalendarActivity extends AppCompatActivity {
    private static final String TAG = "CalendarActivity";

    ActivityAdapter adapter;
    ArrayList<Activity> activities;
    RecyclerView activityRecyclerView;
    FloatingActionButton fab;

    AGConnectCloudDB mCloudDB;
    CloudDBZoneConfig mConfig;
    CloudDBZone mCloudDBZone;
    SharedPreferences sharedPreferences;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activities = new ArrayList<>();
        activityRecyclerView = findViewById(R.id.activityRecyclerView);
        fab = findViewById(R.id.btn_add);

        sharedPreferences = this.getSharedPreferences("UserId", Context.MODE_PRIVATE);
        initDatabase();
        fab.setOnClickListener(v -> {
            startActivity(new Intent(this, AddCalendarActivity.class));
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
            getCalendarData();
        }).addOnFailureListener(e -> Log.w(TAG, "open cloudDBZone failed for " + e.getMessage()));
    }

    private void getCalendarData(){
        if (mCloudDBZone == null) {
            Log.w(TAG, "CloudDBZone is null, try re-open it");
            return;
        }

        Task<CloudDBZoneSnapshot<ActivityDB>> queryTask = mCloudDBZone.executeQuery(
                CloudDBZoneQuery.where(ActivityDB.class).orderByAsc("date").equalTo("userId", sharedPreferences.getString("id", null)),
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
        queryTask.addOnSuccessListener(snapshot -> processQueryResult(snapshot))
                .addOnFailureListener(e -> Log.i(TAG, "Gagal karena " + e.getMessage()));
    }

    public void deleteActivity(Activity activity){
        if (mCloudDBZone == null) {
            Log.w(TAG, "CloudDBZone is null, try re-open it");
            return;
        }

        ActivityDB a = new ActivityDB();
        a.setId(activity.getId());

        Task<Integer> deleteTask = mCloudDBZone.executeDelete(a);
        deleteTask.addOnSuccessListener(integer -> {
            Toast.makeText(this, "Activity Deleted!", Toast.LENGTH_SHORT).show();
            activities.remove(activity);
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Log.i(TAG, "Gagal karena" + e.getMessage());
        });
    }

    private void processQueryResult(CloudDBZoneSnapshot<ActivityDB> snapshot) {

        CloudDBZoneObjectList<ActivityDB> c = snapshot.getSnapshotObjects();
        ArrayList<ActivityDB> calendarList = new ArrayList<>();
        try {
            while (c.hasNext()) {
                ActivityDB a = c.next();
                activities.add( new Activity(a.getId(), a.getTitle(), a.getDescription(), a.getDate()));
            }
        } catch (AGConnectCloudDBException e) {
            Log.i(TAG, "processQueryResult: " + e.getMessage());
        }
        snapshot.release();
        adapter = new ActivityAdapter(activities, CalendarActivity.this);
        activityRecyclerView.setAdapter(adapter);
        activityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_profile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.nav_calendar:
                startActivity(new Intent(this, CalendarActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}