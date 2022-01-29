package com.binus.takenote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.binus.takenote.helper.UserSession;
import com.bumptech.glide.Glide;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.service.AccountAuthService;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "Profile";
    TextView usernameLbl, emailLbl;
    ImageView image;
    AccountAuthService authService;
    AccountAuthParams authParams;
    AGConnectUser currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setSupportActionBar(findViewById(R.id.toolbar));

        image = findViewById(R.id.image);
        usernameLbl = findViewById(R.id.usernameLbl);
        emailLbl = findViewById(R.id.emailLbl);

        authParams = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setEmail().setAuthorizationCode().createParams();
        authService = AccountAuthManager.getService(this, authParams);

        currUser = UserSession.getInstance().getUser();
        String imageUrl = currUser.getPhotoUrl();
        String username = currUser.getDisplayName();
        String email = currUser.getEmail();

        if(email == null || email.isEmpty()){
            email = currUser.getPhone();
        }

        Glide.with(this).load(imageUrl).into(image);
        usernameLbl.setText(username);
        emailLbl.setText(email);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_profile, menu);
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
        Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
        SharedPreferences sharedPreferences = this.getSharedPreferences("UserId", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        UserSession.getInstance().setUser(null);

        startActivity(i);
        finish();
    }
}