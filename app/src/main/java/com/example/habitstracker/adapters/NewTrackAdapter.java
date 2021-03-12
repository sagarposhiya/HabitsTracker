package com.example.habitstracker.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitstracker.R;
import com.example.habitstracker.models.Track;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewTrackAdapter extends RecyclerView.Adapter<NewTrackAdapter.ViewHolder> {

    Activity context;
    ArrayList<Track> tracks;
    TrackAddAdapter.onSavedListener listener;
    ArrayList<String> units;

    public NewTrackAdapter(Activity context, ArrayList<Track> tracks, ArrayList<String> units){
        this.context = context;
        this.tracks = tracks;
        this.units = units;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_track,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Track track = tracks.get(position);
        if (track.isNew()){
            holder.edTrackName.setText("");

            UnitAdapter unitAdapter = new UnitAdapter(context,
                    R.layout.item_unit, R.id.title, units);

            holder.spinnerUnit.setAdapter(unitAdapter);
        } else {
            holder.edTrackName.setText(track.getName());

            UnitAdapter unitAdapter = new UnitAdapter(context,
                    R.layout.item_unit, R.id.title, units);

            holder.spinnerUnit.setAdapter(unitAdapter);
        }
    }

    public void updateList(ArrayList<Track> results){
        this.tracks.clear();
        this.tracks = results;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.edTrackName)
        EditText edTrackName;
        @BindView(R.id.spinnerUnit)
        Spinner spinnerUnit;
        @BindView(R.id.imgClose)
        ImageView imgClose;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
