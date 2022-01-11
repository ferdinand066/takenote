package com.binus.takenote.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.binus.takenote.DetailActivity;
import com.binus.takenote.HomeActivity;
import com.binus.takenote.R;
import com.binus.takenote.model.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>{

    private ArrayList<Note> notes;
    private Context context;

    public NoteAdapter(ArrayList<Note> notes, Context context) {
        this.notes = notes;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder holder, int position) {
        Note note = notes.get(position);
        SimpleDateFormat postFormater = new SimpleDateFormat("MMM dd, yyyy");
        String newDateStr = postFormater.format(note.getLastEdited());
        holder.titleLbl.setText(note.getTitle());
        holder.contentLbl.setText(note.getContent());
        holder.dateLbl.setText(newDateStr);
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, DetailActivity.class);
            i.putExtra("noteId", note.getId());
            i.putExtra("note", note);
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleLbl, contentLbl, dateLbl;

        public ViewHolder(View itemView) {
            super(itemView);

            titleLbl = itemView.findViewById(R.id.titleLbl);
            contentLbl = itemView.findViewById(R.id.contentLbl);
            dateLbl = itemView.findViewById(R.id.dateLbl);
        }
    }
}
