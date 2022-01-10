package com.binus.takenote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.binus.takenote.adapter.NoteAdapter;
import com.binus.takenote.model.Note;

import java.util.ArrayList;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    Toolbar toolbar;
    NoteAdapter adapter;
    ArrayList<Note> notes;
    RecyclerView noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        notes = new ArrayList<>();
        noteList = findViewById(R.id.noteList);

        notes.add(new Note("asd", "asd", new Date()));
        notes.add(new Note("asd", "asd", new Date()));
        notes.add(new Note("asd", "asd", new Date()));
        notes.add(new Note("asd", "asd", new Date()));
        notes.add(new Note("asd", "asd", new Date()));
        notes.add(new Note("asd", "asd", new Date()));


        adapter = new NoteAdapter(notes, HomeActivity.this);
        noteList.setAdapter(adapter);
        noteList.setLayoutManager(new GridLayoutManager(this, 2));
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
        }

        return super.onOptionsItemSelected(item);
    }

}