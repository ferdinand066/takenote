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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        HwAds.init(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        notes = new ArrayList<>();
        noteList = findViewById(R.id.noteList);

        sharedPreferences = this.getSharedPreferences("UserId", Context.MODE_PRIVATE);

        getData();

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

    private void getData(){
        db.collection(sharedPreferences.getString("id", null))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Timestamp timestamp = (Timestamp) document.get("lastEdited");
                                notes.add(new Note(document.get("id").toString(),
                                        document.get("title").toString(),
                                        document.get("content").toString(),
                                        timestamp.toDate()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        adapter = new NoteAdapter(notes, HomeActivity.this);
                        noteList.setAdapter(adapter);
                        noteList.setLayoutManager(new GridLayoutManager(this, 2));
                    } else {
                        Log.w("Home", "Error getting documents.", task.getException());
                    }
                });
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
        startActivity(i);
        finish();
    }


}