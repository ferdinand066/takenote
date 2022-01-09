package com.binus.takenote;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.service.AccountAuthService;

public class UserActivity extends AppCompatActivity {
    private static final String TAG = "Useracivity";
    TextView userdetails;
    ImageView profile_photo;
    AccountAuthService authService;
    AccountAuthParams authParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        authParams = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setEmail().setAuthorizationCode().createParams();
        authService = AccountAuthManager.getService(this, authParams);

        userdetails = (TextView) findViewById(R.id.user_info);
        profile_photo= (ImageView)findViewById(R.id.user_photo);
        userdetails.append(getIntent().getStringExtra("name")+"\n" +getIntent().getStringExtra("email")+"\n" /*+getIntent().getStringExtra("auth")*/);

//        Glide.with(UserActivity.this)
//                .load(getIntent().getStringExtra("photo_uri"))
//                .into(profile_photo);

        findViewById(R.id.logout).setOnClickListener(v -> {
            signout();
            finish();
        });


//        findViewById(R.id.mapbutton).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(UserActivity.this , Mapactivity.class);
//                startActivity(intent);
//            }
//        });

        findViewById(R.id.revokeauth).setOnClickListener(v -> {
            // service indicates the AccountAuthService instance generated using the getService method during the sign-in authorization.
            authService.cancelAuthorization().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Processing after a successful authorization cancellation.
                    Log.i(TAG, "onSuccess: ");
                    finish();
                } else {
                    // Handle the exception.
                    Exception exception = task.getException();
                    if (exception instanceof ApiException){
                        int statusCode = ((ApiException) exception).getStatusCode();
                        Log.i(TAG, "onFailure: " + statusCode);
                    }
                }
            });
        });
    }

    public void signout(){
        Task<Void> signOutTask = authService.signOut();
        signOutTask.addOnCompleteListener(task -> {
            Log.i(TAG, "signOut complete");
        });
        signOutTask.addOnFailureListener(e -> {
            Log.i(TAG, "signout failed"+e.getMessage());
        });
    }

}