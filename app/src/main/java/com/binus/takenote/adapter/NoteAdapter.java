package com.binus.takenote.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.binus.takenote.DetailActivity;
import com.binus.takenote.HomeActivity;
import com.binus.takenote.R;
import com.binus.takenote.model.Note;

import java.util.ArrayList;

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

        holder.titleLbl.setText(note.getTitle());
        holder.contentLbl.setText(note.getContent());
        holder.dateLbl.setText(note.getLastEdited().toString());
        holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, DetailActivity.class)));
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
