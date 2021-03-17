package com.example.habitstracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitstracker.R;
import com.example.habitstracker.models.Reminder;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    Context context;
    ArrayList<Reminder> reminders;

    public ReminderAdapter(Context context, ArrayList<Reminder> reminders){
        this.context = context;
        this.reminders = reminders;
    }

    @NonNull
    @Override
    public ReminderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderAdapter.ViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
//        holder.txtTitle.setText(reminder.getTitle());
//        holder.txtDate.setText(reminder.getStrDate());
//        holder.txtTime.setText(reminder.getStrTime());
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.txtTitle)
//        TextView txtTitle;
//        @BindView(R.id.txtDate)
//        TextView txtDate;
//        @BindView(R.id.txtTime)
//        TextView txtTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
           // ButterKnife.bind(this,itemView);
        }
    }
}
