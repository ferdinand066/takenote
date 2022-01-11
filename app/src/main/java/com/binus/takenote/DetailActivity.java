package com.binus.takenote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.binus.takenote.model.Note;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    private TextView tvStatus, backBtn;
    private EditText etTitle, etDetail;
    private static final String TAG = "DetailActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences sharedPreferences;
    Note note = null;
    ImageView icDelete;

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
        } else {
            String newDateStr = postFormater.format(new Date());
            tvStatus.setText(newDateStr + " | " + "0 character(s)");
        }

        backBtn.setOnClickListener(v -> {
            updateData();
            startActivity(new Intent(DetailActivity.this, HomeActivity.class))         ;
            finish();
        });

        icDelete.setOnClickListener(v -> {
            deleteData();

        });
    }

    private void updateData(){
        String title = etTitle.getText().toString();
        String content = etDetail.getText().toString();
        Date date = new Date();
        Map<String, Object> note = new HashMap<>();
        note.put("id", getIntent().getStringExtra("noteId"));
        note.put("title", title);
        note.put("content", content);
        note.put("lastEdited", date);

        Log.d("Detail", sharedPreferences.getString("id", null));
        Log.d("Detail", getIntent().getStringExtra("noteId"));

        db.collection(sharedPreferences.getString("id", null))
                .document(getIntent().getStringExtra("noteId")).set(note);
    }

    private void deleteData(){
        if (note != null){
            db.collection(sharedPreferences.getString("id", null))
                    .document(getIntent().getStringExtra("noteId")).delete();
            startActivity(new Intent(DetailActivity.this, HomeActivity.class));
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        updateData();
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

}