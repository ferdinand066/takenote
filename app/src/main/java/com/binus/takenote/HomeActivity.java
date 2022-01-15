package com.binus.takenote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.binus.takenote.adapter.NoteAdapter;
import com.binus.takenote.model.Note;
import com.binus.takenote.model.NoteDB;
import com.binus.takenote.model.ObjectTypeInfoHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.BannerAdSize;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.banner.BannerView;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.service.AccountAuthService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity {

    Toolbar toolbar;
    NoteAdapter adapter;
    ArrayList<Note> notes;
    RecyclerView noteList;
    BannerView bannerView;
    SharedPreferences sharedPreferences;
    FloatingActionButton btnAdd;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    AccountAuthService authService;
    AccountAuthParams authParams;
    private static final String TAG = "HomeActivity";

    AGConnectCloudDB mCloudDB;
    CloudDBZoneConfig mConfig;
    CloudDBZone mCloudDBZone;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        HwAds.init(this);
        initDatabase();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        notes = new ArrayList<>();
        noteList = findViewById(R.id.noteList);

        sharedPreferences = this.getSharedPreferences("UserId", Context.MODE_PRIVATE);

        bannerView = new BannerView(this);
        bannerView.setAdId("testw6vs28auh3");
        bannerView.setBannerAdSize(BannerAdSize.BANNER_SIZE_SMART);
        bannerView.setBannerRefresh(60);
        AdParam adParam = new AdParam.Builder().build();
        bannerView.loadAd(adParam);

        LinearLayout rootView = findViewById(R.id.view_container);
        rootView.addView(bannerView);

        btnAdd = findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), DetailActivity.class);
            i.putExtra("noteId", UUID.randomUUID().toString());
            startActivity(i);
            finish();
        });

        authParams = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setEmail().setAuthorizationCode().createParams();
        authService = AccountAuthManager.getService(this, authParams);
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
            getNoteData();
        }).addOnFailureListener(e -> Log.w(TAG, "open cloudDBZone failed for " + e.getMessage()));
    }

    private void getNoteData(){
        if (mCloudDBZone == null) {
            Log.w(TAG, "CloudDBZone is null, try re-open it");
            return;
        }

        Task<CloudDBZoneSnapshot<NoteDB>> queryTask = mCloudDBZone.executeQuery(
                CloudDBZoneQuery.where(NoteDB.class).orderByDesc("date"),
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
        queryTask.addOnSuccessListener(snapshot -> processQueryResult(snapshot))
                .addOnFailureListener(e -> Log.i(TAG, "Gagal karena " + e.getMessage()));
    }

    private void processQueryResult(CloudDBZoneSnapshot<NoteDB> snapshot) {

        CloudDBZoneObjectList<NoteDB> c = snapshot.getSnapshotObjects();
        ArrayList<NoteDB> bookInfoList = new ArrayList<>();
        try {
            while (c.hasNext()) {
                NoteDB n = c.next();
                bookInfoList.add(n);
                if(n.getUserId().equals(sharedPreferences.getString("id", null))){
                    notes.add( new Note(n.getId(), n.getTitle(), n.getContent(), n.getDate()));
                }
            }
        } catch (AGConnectCloudDBException e) {
            Log.i(TAG, "processQueryResult: " + e.getMessage());
        }
        snapshot.release();
        adapter = new NoteAdapter(notes, HomeActivity.this);
        noteList.setAdapter(adapter);
        noteList.setLayoutManager(new GridLayoutManager(this, 2));
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
            case R.id.nav_logout:
                logout();
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void logout(){
        Task<Void> signOutTask = authService.signOut();
        signOutTask.addOnCompleteListener(task -> {
            Log.i(TAG, "signOut complete");
        });
        signOutTask.addOnFailureListener(e -> {
            Log.i(TAG, "signout failed"+e.getMessage());
        });
        Intent i = new Intent(HomeActivity.this, LoginActivity.class);
        SharedPreferences sharedPreferences = this.getSharedPreferences("UserId", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

        startActivity(i);
        finish();
    }


}