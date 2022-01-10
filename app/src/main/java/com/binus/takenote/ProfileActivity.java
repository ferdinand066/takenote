package com.binus.takenote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    TextView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_profile:
                startActivity(new Intent(this, ProfileActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}