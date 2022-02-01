package com.binus.takenote.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
    private int layout;

    public NoteAdapter(ArrayList<Note> notes, Context context, int layout) {
        this.notes = notes;
        this.context = context;
        this.layout = layout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

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

        int color = -1;
        int titleColor = -1;
        int contentColor = -1;

        switch (note.getColor()){
            case 1:
                color = R.color.secondary;
                titleColor = R.color.gray;
                contentColor = R.color.light_gray;
                break;
            case 2:
                color = R.color.primaryPurple;
                titleColor = R.color.gray;
                contentColor = R.color.gray;
                break;
            case 3:
                color = R.color.primaryOrange;
                titleColor = R.color.light_gray;
                contentColor = R.color.light_gray;
                break;
            default:
                color = R.color.primaryYellow;
                titleColor = R.color.light_gray;
                contentColor = R.color.light_gray;
                break;
        }

        Drawable background = holder.itemView.getBackground();
        GradientDrawable gradient = (GradientDrawable) background;
        gradient.setColor(ContextCompat.getColor(context, color));

        holder.titleLbl.setTextColor(ContextCompat.getColor(context, titleColor));
        holder.contentLbl.setTextColor(ContextCompat.getColor(context, contentColor));
        holder.dateLbl.setTextColor(ContextCompat.getColor(context, contentColor));

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
