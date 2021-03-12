package com.example.habitstracker.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitstracker.R;
import com.example.habitstracker.models.Entry;
import com.example.habitstracker.models.Track;
import com.example.habitstracker.utils.AppUtils;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.Utils;
import com.hendraanggrian.recyclerview.widget.ExpandableItem;
import com.hendraanggrian.recyclerview.widget.ExpandableRecyclerView;

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

public class MyAdapter extends ExpandableRecyclerView.Adapter<MyAdapter.ViewHolder>{

    Context context;
    ArrayList<Track> list;

    public MyAdapter(@NonNull LinearLayoutManager layoutManager, Context context, ArrayList<Track> list) {
        super(layoutManager);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dummy, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Track track = list.get(position);
        holder.txtAverage.setText(track.getAverageUnit());
        holder.txtIncrease.setText(track.getIncrement());
        holder.txtParamName.setText(track.getName());
        holder.txtUnit.setText(track.getUnit());
        //        holder.txtEntries.setText(track.getEntries().size());


        holder.txtEntries.setText(track.getEntries().size() + " entries");

        List<String> dates = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        for (int i = 0; i < track.getEntries().size(); i++) {
            Entry entry = track.getEntries().get(i);
            dates.add(String.valueOf(entry.getDate()));
            values.add(entry.getValue());
        }
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();

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
            if (realm != null) {
                realm.close();
            }
        }


        if (track.getEntries().size() == 0) {
            holder.txtNoData.setVisibility(View.VISIBLE);
            holder.lineChart.setVisibility(View.GONE);
        } else {
            holder.txtNoData.setVisibility(View.GONE);
            holder.lineChart.setVisibility(View.VISIBLE);
            renderData(dates, values, holder.lineChart);
            renderData(dates,values,holder.lineChartContent);

            //Set Avarage
            double sum = 0;
            for (int i = 0; i < track.getEntries().size() ; i++) {
                Entry entry = track.getEntries().get(i);
                sum = sum + entry.getValue();
            }

            sum = sum/track.getEntries().size();

            holder.txtAverage.setText("Av: " + new DecimalFormat("##.##").format(sum) + " " + track.getUnit());

            //set Increment/Decrement

            if (track.getEntries().size() == 1){
                holder.txtIncrease.setText("+" + track.getEntries().get(0).getValue() + " " + track.getUnit());
            } else {
                Entry entry1 =  track.getEntries().get(track.getEntries().size() - 1);
                Entry entry2 =  track.getEntries().get(track.getEntries().size() - 2);

                double value = entry1.getValue() - entry2.getValue();

                if (value > 0){
                    holder.txtIncrease.setText("+" + new DecimalFormat("##.##").format(value) + " " + track.getUnit());
                } else {
                    holder.txtIncrease.setText("    " + new DecimalFormat("##.##").format(value) + " " + track.getUnit());
                }
            }
        }
    }

    public void renderData(List<String> dates, List<Double> allAmounts, LineChart volumeReportChart) {

//        final ArrayList<String> xAxisLabel = new ArrayList<>();
//        xAxisLabel.add("1");
//        xAxisLabel.add("7");
//        xAxisLabel.add("14");
//        xAxisLabel.add("21");
//        xAxisLabel.add("28");
//        xAxisLabel.add("30");

        XAxis xAxis = volumeReportChart.getXAxis();
        XAxis.XAxisPosition position = XAxis.XAxisPosition.BOTTOM;
        xAxis.setPosition(position);
        xAxis.enableGridDashedLine(2f, 7f, 0f);
//        xAxis.setAxisMaximum(5f);
//        xAxis.setAxisMinimum(0f);
//      //  xAxis.setLabelCount(6, true);
//        xAxis.setGranularityEnabled(true);
//        xAxis.setGranularity(7f);
//        xAxis.setLabelRotationAngle(315f);

        //xAxis.setValueFormatter(new ClaimsXAxisValueFormatter(dates));
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawLabels(false);
        xAxis.setDrawLimitLinesBehindData(true);

        LimitLine ll1 = new LimitLine(Float.parseFloat("02220"), "");
        //ll1.setLineColor(getResources().getColor(R.color.greyish_brown));
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(35f, "");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        ll2.setLineColor(Color.parseColor("#FFFFFF"));

        xAxis.removeAllLimitLines();
        xAxis.addLimitLine(ll1);
        xAxis.addLimitLine(ll2);

        YAxis leftAxis = volumeReportChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        //leftAxis.addLimitLine(ll1);
        //leftAxis.addLimitLine(ll2);

        //leftAxis.setAxisMaximum(findMaximumValueInList(allAmounts).floatValue() + 100f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(false);
        leftAxis.setDrawLabels(false);
        //XAxis xAxis = mBarChart.getXAxis();
        //leftAxis.setValueFormatter(new ClaimsYAxisValueFormatter());

        volumeReportChart.getDescription().setEnabled(true);
        Description description = new Description();
        // description.setText(UISetters.getFullMonthName());//commented for weekly reporting
        description.setText("");
        description.setTextSize(15f);
        volumeReportChart.getDescription().setPosition(0f, 0f);
        volumeReportChart.setDescription(description);
        volumeReportChart.getAxisRight().setEnabled(false);

        //setData()-- allAmounts is data to display;
        setDataForWeeksWise(allAmounts, volumeReportChart, dates);

    }

    private void setDataForWeeksWise(List<Double> amounts, LineChart volumeReportChart, List<String> dates) {

        ArrayList<com.github.mikephil.charting.data.Entry> values = new ArrayList<>();
        for (int i = 0; i < amounts.size(); i++) {
            values.add(new com.github.mikephil.charting.data.Entry(i, amounts.get(i).floatValue()));
        }

        LineDataSet set1;
        if (volumeReportChart.getData() != null &&
                volumeReportChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) volumeReportChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            volumeReportChart.getData().notifyDataChanged();
            volumeReportChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values,"");
            set1.setDrawCircles(true);
//            set1.enableDashedLine(10f, 0f, 0f);
//            set1.enableDashedHighlightLine(10f, 0f, 0f);
//            set1.setColor(context.getResources().getColor(R.color.toolBarColor));
//            set1.setCircleColor(getResources().getColor(R.color.toolBarColor));
            set1.setLineWidth(2f);//line size
            set1.setCircleRadius(5f);
            set1.setDrawCircleHole(true);
            set1.setValueTextSize(10f);
            set1.setDrawFilled(true);
//            set1.setFormLineWidth(5f);
//            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
//            set1.setFormSize(5.f);
            set1.setDrawFilled(true);
            // set1.setFillDrawable(ContextCompat.getDrawabl e(context, R.drawable.gradient));

            if (Utils.getSDKInt() >= 18) {
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.gradient);
                set1.setFillDrawable(drawable);
                set1.setFillColor(Color.WHITE);

            } else {
                set1.setFillColor(Color.WHITE);
            }
            set1.setDrawValues(true);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            LineData data = new LineData(dataSets);

            // data.setValueFormatter(new MyYAxisValueFormatter());
//            volumeReportChart.setScaleXEnabled(false);
//            volumeReportChart.setScaleYEnabled(false);
            volumeReportChart.animateY(3000, Easing.EaseOutBack);
//            volumeReportChart.setAutoScaleMinMaxEnabled(true);
//            volumeReportChart.setScaleEnabled(false);
            volumeReportChart.setDrawGridBackground(false);
            volumeReportChart.getAxisLeft().setDrawGridLines(false);
            volumeReportChart.getXAxis().setDrawGridLines(false);
            volumeReportChart.setTouchEnabled(false);
            volumeReportChart.setData(data);

            volumeReportChart.setOnChartGestureListener(new OnChartGestureListener() {
                @Override
                public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

                }

                @Override
                public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

                }

                @Override
                public void onChartLongPressed(MotionEvent me) {

                }

                @Override
                public void onChartDoubleTapped(MotionEvent me) {

                }

                @Override
                public void onChartSingleTapped(MotionEvent me) {

                }

                @Override
                public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

                }

                @Override
                public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

                }

                @Override
                public void onChartTranslate(MotionEvent me, float dX, float dY) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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
        @BindView(R.id.row)
        ExpandableItem row;
        @BindView(R.id.lineChartContent)
        LineChart lineChartContent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
