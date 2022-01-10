package com.binus.takenote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    private TextView tv_status, backBtn;
    private EditText et_title, et_detail;
    private static final String TAG = "DetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        init();
        setText();
    }

    private void init(){
        tv_status = findViewById(R.id.tv_status);
        et_title = findViewById(R.id.et_title);
        et_detail = findViewById(R.id.et_detail);
        backBtn = findViewById(R.id.back_btn);

        SimpleDateFormat postFormater = new SimpleDateFormat("MMM dd, yyyy");
        String newDateStr = postFormater.format(new Date());
        tv_status.setText(newDateStr + " | " + "0 character(s)");

        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(DetailActivity.this, HomeActivity.class));
        });
    }

    private void setText(){
        et_detail.addTextChangedListener(new TextWatcher() {
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
                tv_status.setText(newDateStr + " | " + length + " character(s)");
            }
        });
    }
}