package com.example.habitstracker_verion.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitstracker_verion.R;
import com.example.habitstracker_verion.models.Reminder;

import java.util.ArrayList;

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
