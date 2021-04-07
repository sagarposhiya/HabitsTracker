package com.example.habitstracker_verion.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitstracker_verion.R;
import com.example.habitstracker_verion.models.Entry;
import com.example.habitstracker_verion.models.Track;
import com.example.habitstracker_verion.utils.AppUtils;
import com.example.habitstracker_verion.utils.DecimalValueFormatter;
import com.example.habitstracker_verion.utils.RealmManager;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class TrackEntriesAdapter extends RecyclerView.Adapter<TrackEntriesAdapter.ViewHolder> {

    Context context;
    Activity activity;
    ArrayList<Track> list;
    onChartClickListener listener;

    public interface onChartClickListener {
        void onChartClick(Track track);
    }

    public TrackEntriesAdapter(Context context, Activity activity, ArrayList<Track> list, onChartClickListener listener) {
        this.context = context;
        this.list = list;
        this.activity = activity;
        this.listener = listener;
    }

    public ArrayList<Track> getTracksFromList() {
        return list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entry, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Track track = list.get(position);
        holder.txtAverage.setText(track.getAverageUnit());
        holder.txtIncrease.setText(track.getIncrement());
        holder.txtParamName.setText(track.getName());
        holder.txtUnit.setText(track.getUnit().replace(".",""));

        holder.txtEntries.setText(track.getEntries().size() + " entries");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (track.getEntries().size() == 0) {
                    Toast.makeText(context, "No Found Entries", Toast.LENGTH_SHORT).show();
                } else {
                    listener.onChartClick(track);
                }
            }
        });

        List<Long> dates = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        for (int i = 0; i < track.getEntries().size(); i++) {
            Entry entry = track.getEntries().get(i);
            dates.add(entry.getDate());
            values.add(entry.getValue());
        }
        Realm realm = null;
        try {
            realm = RealmManager.getInstance();

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Collections.sort(track.getEntries(), new Comparator<Entry>() {
                        DateFormat f = new SimpleDateFormat("dd/MM/yyyy '@'hh:mm a");

                        @Override
                        public int compare(Entry lhs, Entry rhs) {
                            try {
                                return f.parse(AppUtils.getDate(lhs.getDate(), "dd/MM/yyyy '@'hh:mm a")).compareTo(f.parse(AppUtils.getDate(rhs.getDate(), "dd/MM/yyyy '@'hh:mm a")));
                            } catch (ParseException e) {
                                throw new IllegalArgumentException(e);
                            }
                        }
                    });
                }
            });
        } finally {
          // RealmManager.closeInstance();
        }

        if (track.getEntries().size() == 0) {
            holder.txtNoData.setVisibility(View.VISIBLE);
            holder.lineChart.setVisibility(View.GONE);
        } else {
            holder.txtNoData.setVisibility(View.GONE);
            holder.lineChart.setVisibility(View.VISIBLE);
            renderData(dates, values, holder.lineChart, track);

            //Set Avarage
            double sum = 0;
            for (int i = 0; i < track.getEntries().size(); i++) {
                Entry entry = track.getEntries().get(i);
                sum = sum + entry.getValue();
            }

            sum = sum / track.getEntries().size();

            holder.txtAverage.setText("(Avg: " + new DecimalFormat("##.##").format(sum) + ")");

            //set Increment/Decrement

            if (track.getEntries().size() == 1) {
                holder.txtIncrease.setText("+" + track.getEntries().get(0).getValue() + " " + track.getUnit());
            } else {
                Entry entry1 = track.getEntries().get(track.getEntries().size() - 1);
                Entry entry2 = track.getEntries().get(track.getEntries().size() - 2);

                double value = entry1.getValue() - entry2.getValue();

                if (value > 0) {
                    holder.txtIncrease.setText("+" + new DecimalFormat("##.##").format(value) + " " + track.getUnit());
                } else {
                    holder.txtIncrease.setText("    " + new DecimalFormat("##.##").format(value) + " " + track.getUnit());
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void renderData(List<Long> dates, List<Double> allAmounts, LineChart volumeReportChart, Track track) {

        XAxis xAxis = volumeReportChart.getXAxis();
        XAxis.XAxisPosition position = XAxis.XAxisPosition.BOTTOM;
        xAxis.setPosition(position);
        xAxis.enableGridDashedLine(2f, 7f, 0f);
        xAxis.setDrawLabels(false);
        xAxis.setDrawLimitLinesBehindData(true);
        xAxis.setDrawAxisLine(false);

        LimitLine ll1 = new LimitLine(Float.parseFloat("02220"), "");
        if (track.getColor() == null) {
            ll1.setLineColor(context.getColor(R.color.colorPrimary));
        } else {
            ll1.setLineColor(Color.parseColor(track.getColor()));
        }

        ll1.setLineWidth(4f);
        ll1.enableDashedLine(0f, 0f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(35f, "");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(0f, 0f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        ll2.setLineColor(Color.parseColor("#FFFFFF"));

        xAxis.removeAllLimitLines();

        YAxis leftAxis = volumeReportChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);

        leftAxis.setAxisMinimum(0f);
        leftAxis.enableGridDashedLine(0f, 0f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(false);
        leftAxis.setDrawLabels(false);

        volumeReportChart.getDescription().setEnabled(false);
        Description description = new Description();
        description.setText("");
        description.setTextSize(15f);
        volumeReportChart.getDescription().setPosition(0f, 0f);
        volumeReportChart.setDescription(description);
        volumeReportChart.getAxisRight().setEnabled(false);

        setDataForWeeksWise(allAmounts, volumeReportChart, dates, track);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setDataForWeeksWise(List<Double> amounts, LineChart volumeReportChart, List<Long> dates, Track track) {

        ArrayList<com.github.mikephil.charting.data.Entry> values = new ArrayList<>();
        for (int i = 0; i < amounts.size(); i++) {
            values.add(new com.github.mikephil.charting.data.Entry(Float.parseFloat(String.valueOf(dates.get(i))), amounts.get(i).floatValue()));
        }

        LineDataSet set1;
        if (volumeReportChart.getData() != null &&
                volumeReportChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) volumeReportChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            volumeReportChart.getData().notifyDataChanged();
            volumeReportChart.notifyDataSetChanged();
            set1.setValueFormatter(new DecimalValueFormatter());
        } else {
            set1 = new LineDataSet(values, "");
            set1.setDrawCircles(true);
            set1.setLineWidth(2.5f);//line size
            set1.setCircleRadius(5f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(10f);
            set1.setDrawFilled(true);
            set1.setValueFormatter(new DecimalValueFormatter());
            //  set1.setFillDrawable(ContextCompat.getDrawable(context, R.drawable.gradient));

            if (Utils.getSDKInt() >= 18) {

                if (track.getColor() != null) {
                    Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.gradient);
                    Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                    DrawableCompat.setTint(wrappedDrawable, Color.parseColor(track.getColor()));
                    set1.setFillDrawable(unwrappedDrawable);
                    set1.setCircleColor(Color.parseColor(track.getColor()));
                    set1.setColor(Color.parseColor(track.getColor()));
                } else {
//                    Drawable drawable = ContextCompat.getDrawable(context, R.drawable.gradient);
//                    set1.setFillDrawable(drawable);

                    Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.gradient);
                    Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                    DrawableCompat.setTint(wrappedDrawable, context.getColor(R.color.colorPrimary));
                    set1.setFillDrawable(unwrappedDrawable);
                    set1.setCircleColor(context.getColor(R.color.colorPrimary));
                    set1.setColor(context.getColor(R.color.colorPrimary));
                }

            } else {
                set1.setFillColor(Color.WHITE);
            }

            set1.setDrawValues(true);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            LineData data = new LineData(dataSets);
            volumeReportChart.animateY(3000, Easing.EaseOutBack);
            volumeReportChart.setTouchEnabled(false);
            volumeReportChart.setDrawGridBackground(false);
            volumeReportChart.getAxisLeft().setDrawGridLines(false);
            volumeReportChart.getXAxis().setDrawGridLines(false);
            volumeReportChart.getLegend().setEnabled(false);
            volumeReportChart.setData(data);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.txtParamName)
        TextView txtParamName;
        @BindView(R.id.lineChart)
        LineChart lineChart;
        @BindView(R.id.txtEntries)
        TextView txtEntries;
        @BindView(R.id.txtAverage)
        TextView txtAverage;
        @BindView(R.id.txtIncrease)
        TextView txtIncrease;
        @BindView(R.id.txtNoData)
        TextView txtNoData;
        @BindView(R.id.txtUnit)
        TextView txtUnit;

        private int mOriginalHeight = 0;
        private boolean mIsViewExpanded = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }
    }
}
