package com.example.habitstracker_verion.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitstracker_verion.R;
import com.example.habitstracker_verion.models.Entry;
import com.example.habitstracker_verion.utils.RealmManager;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;

public class EntryListAdapter extends RecyclerView.Adapter<EntryListAdapter.ViewHolder> {

    Context context;
   // RealmList<Entry> list;
    ArrayList<Entry> entries;
    onDeleteListner listner;
    public interface onDeleteListner{
        void onDeleteEntry();
    }
    public EntryListAdapter(Context context, ArrayList<Entry> entries, RealmList<Entry> list, onDeleteListner listner){
        this.context = context;
       // this.list = list;
        this.entries = entries;
        this.listner = listner;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_entry,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Entry entry = entries.get(position);
//        long emissionsMilliSince1970Time = ((long) entry.getDate()) * 1000;
        long emissionsMilliSince1970Time = ((long) entry.getDate());

        // Show time in local version
        SimpleDateFormat format = new SimpleDateFormat("MMM dd, hh:mm a");
        Date timeMilliseconds = new Date(emissionsMilliSince1970Time);

        holder.txtDate.setText(format.format(timeMilliseconds));
        holder.txtValue.setText(String.valueOf(new DecimalFormat("#").format(entry.getValue())));

        holder.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Realm realm = null;
                try {
                    realm = RealmManager.getInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            entries.remove(position);
                            notifyDataSetChanged();
                            entry.deleteFromRealm();
                            listner.onDeleteEntry();
                        }
                    });

                } finally {
                    RealmManager.closeInstance();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return entries.size();


    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txtDate)
        TextView txtDate;
        @BindView(R.id.txtValue)
        TextView txtValue;
        @BindView(R.id.imgClose)
        ImageView imgClose;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
