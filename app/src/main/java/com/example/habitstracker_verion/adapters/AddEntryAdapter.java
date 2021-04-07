package com.example.habitstracker_verion.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;
import com.example.habitstracker_verion.R;
import com.example.habitstracker_verion.models.Track;
import com.example.habitstracker_verion.models.Unit;
import com.github.mikephil.charting.charts.LineChart;
import com.xw.repo.BubbleSeekBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.jvm.functions.Function3;


public class AddEntryAdapter extends RecyclerView.Adapter<AddEntryAdapter.ViewHolder> {

    Context context;
    public static ArrayList<Track> list;
    Function3<Track, LineChart,Integer, Unit> lineChartIntegerUnitFunction;

    public AddEntryAdapter(Context context, ArrayList<Track> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_entry, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Track track = list.get(position);
        holder.txtTrackName.setText(track.getName());
        holder.txtUnit.setText(track.getUnit());
        if (track.getEntries().size() > 0) {
            holder.mtfName.setText(String.valueOf(track.getEntries().get(track.getEntries().size() - 1).getValue()));
        } else {
            holder.mtfName.setText("0");
        }

        holder.bubbleBar.setThumbColor(context.getColor(R.color.colorPrimary));
        holder.bubbleBar.setSecondTrackColor(context.getColor(R.color.colorPrimary));
        holder.bubbleBar.setBubbleColor(context.getColor(R.color.colorPrimary));
        if (track.getEntries().size() > 0 && track.getEntries().get(track.getEntries().size() - 1).getValue() <= 10) {
            holder.bubbleBar.setProgress((int) track.getEntries().get(track.getEntries().size() - 1).getValue());
        }

        if (track.getColor() != null) {
            holder.bubbleBar.setBubbleColor(Color.parseColor(track.getColor()));
            holder.bubbleBar.setThumbColor(Color.parseColor(track.getColor()));
            holder.bubbleBar.setTrackColor(Color.parseColor(track.getColor()));
            holder.bubbleBar.setSecondTrackColor(Color.parseColor(track.getColor()));
        }

        holder.bubbleBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                holder.txtMin.setText(progress+"");
                if (progress != 0.0 ) {
                    holder.mtfName.setText(progress + "");
                }
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });

    }

    public ArrayList<Track> getTracksFromList() {
        return list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.mtfName)
        EditText mtfName;
        @BindView(R.id.txtUnit)
        TextView txtUnit;
        @BindView(R.id.bubbleBar)
        BubbleSeekBar bubbleBar;
        @BindView(R.id.txtMin)
        TextView txtMin;
        @BindView(R.id.txtMax)
        TextView txtMax;
        @BindView(R.id.txtTrackName)
        TextView txtTrackName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
