package com.binus.takenote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;

public class LoginActivity extends AppCompatActivity {
    private AccountAuthService mAuthService;
    private AccountAuthParams mAuthParam;
    // Define the request code for signInIntent.
    private static final int REQUEST_CODE_SIGN_IN = 1000;
    // Define the log tag.
    private static final String TAG = "Account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.HuaweiIdAuthButton).setOnClickListener(v -> {
            SignInUsingHwId();
        });
    }

    private void SignInUsingHwId() {
        // If your app needs to obtain the user's email address, call setEmail() , similarly you  need to acess ID token or authorisation code setIDToken() and setAuthorisationCode()
        mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setEmail()
                .createParams();
        mAuthService = AccountAuthManager.getService(this, mAuthParam);
        Intent signInIntent = mAuthService.getSignInIntent();
        // startActivityForResult() method, we can get result from another activity
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
    }

    // startActivityForResult method, requires a result from the second activity (activity to be invoked).
    //In such case, we need to override the onActivityResult method that is invoked automatically when second activity returns result.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // requestcode is checked to make that the activity that invoked passed the result.This can be any code

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Log.i(TAG, "onActivitResult of sigInInIntent, request code: " + REQUEST_CODE_SIGN_IN);
            Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
            if (authAccountTask.isSuccessful()) {
                // The sign-in is successful, and the authAccount object that contains the HUAWEI ID information is obtained.
                AuthAccount authAccount = authAccountTask.getResult();
                Log.i(TAG, "display name:" + authAccount.getDisplayName());
                Log.i(TAG, "photo uri string:" + authAccount.getAvatarUriString());
                Log.i(TAG, "photo uri:" + authAccount.getAvatarUri());
                Log.i(TAG, "email:" + authAccount.getEmail());
                Log.i(TAG, "openid:" + authAccount.getOpenId());
                Log.i(TAG, "unionid:" + authAccount.getAuthorizationCode());
                Log.i(TAG, "agerange:" + authAccount.getAgeRange());

// when the user login sucesfully , i will get all the details and i am passing all to the next activity via the intent .
                Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                intent.putExtra("name", authAccount.getDisplayName());
                intent.putExtra("email", authAccount.getEmail());
                intent.putExtra("photo_uri", authAccount.getAvatarUriString());
                intent.putExtra("auth", authAccount.getAuthorizationCode());

                startActivity(intent);
            } else {
                // The sign-in fails. Find the failure cause from the status code. For more information, please refer to the "Error Codes" section in the API Reference.
                Log.e(TAG, "sign in failed : " + ((ApiException) authAccountTask.getException()).getStatusCode());
            }
        }
    }
}