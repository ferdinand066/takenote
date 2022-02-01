package com.binus.takenote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.binus.takenote.CalendarActivity;
import com.binus.takenote.R;
import com.binus.takenote.model.Activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private ArrayList<Activity> activities;
    private Context context;

    public ActivityAdapter(ArrayList<Activity> activities, Context context) {
        this.activities = activities;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_activity, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.titleLbl.setText(activities.get(position).getTitle());
        holder.descriptionLbl.setText(activities.get(position).getDescription());String myFormat="dd/MM/yyyy";

        String format = "dd/MM/yyyy";
        SimpleDateFormat dateFormat= new SimpleDateFormat(format, Locale.US);
        holder.dateLbl.setText(dateFormat.format(activities.get(position).getDate().getTime()));

        holder.icDelete.setOnClickListener(v -> {
            ((CalendarActivity)context).deleteActivity(activities.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleLbl, descriptionLbl, dateLbl;
        ImageView icDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            titleLbl = itemView.findViewById(R.id.titleLbl);
            descriptionLbl = itemView.findViewById(R.id.descriptionLbl);
            dateLbl = itemView.findViewById(R.id.dateLbl);
            icDelete = itemView.findViewById(R.id.ic_delete);
        }
    }
}
