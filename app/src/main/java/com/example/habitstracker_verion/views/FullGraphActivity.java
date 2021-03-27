package com.example.habitstracker_verion.views;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.habitstracker_verion.R;
import com.example.habitstracker_verion.adapters.EntryListAdapter;
import com.example.habitstracker_verion.models.Entry;
import com.example.habitstracker_verion.models.Track;
import com.example.habitstracker_verion.utils.AppUtils;
import com.example.habitstracker_verion.utils.BottomSheetBehavior;
import com.example.habitstracker_verion.utils.BottomSheetBehaviorRecyclerManager;
import com.example.habitstracker_verion.utils.ClaimsXAxisValueFormatter;
import com.example.habitstracker_verion.utils.Constants;
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
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.Sort;

public class FullGraphActivity extends AppCompatActivity implements EntryListAdapter.onDeleteListner {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.lineChartFull)
    LineChart lineChartFull;
    @BindView(R.id.imgArraows)
    ImageView imgArraows;
    @BindView(R.id.txtTitle)
    TextView txtTitle;
    Realm mRealm;
    Track track;
    int trackId;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private View mBottomSheetView;
    private CoordinatorLayout mParent;
    private RecyclerView mBottomSheetRecyclerLeft;
    private LinearLayoutManager mLayoutManagerLeft;
    String color;
    EntryListAdapter listAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_graph);
        ButterKnife.bind(this);
        setAppTheme();
        getInit();

    }

    private void setAppTheme() {
        color = AppUtils.getStringPreference(this, Constants.themeColor);
        toolbar.setBackgroundColor(Color.parseColor(color));
       // btnDone.setBackgroundColor(Color.parseColor(color));
    }

    private void getInit() {
        mRealm = Realm.getDefaultInstance();
        trackId = getIntent().getIntExtra(Constants.trackId,000);
        getTrack();
        txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(FullGraphActivity.this,DashboardActivity.class);
//                startActivity(intent);
//                finish();
                onBackPressed();
            }
        });
    }

    private void getTrack() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void execute(Realm realm) {
                track = realm.where(Track.class).equalTo("id",trackId).findFirst();
                txtTitle.setText(track.getName());
                setTrackDetails(track,false);
            }
        });
    }

    private void getInitBottomSheet() {

        mParent = (CoordinatorLayout) findViewById(R.id.parent_container);
        mParent.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        mBottomSheetView = findViewById(R.id.main_bottomsheet);

        mBottomSheetView.setVisibility(View.VISIBLE);
        mBottomSheetRecyclerLeft = (RecyclerView) findViewById(R.id.btm_recyclerview_left);
        mLayoutManagerLeft = new LinearLayoutManager(this);
        mBottomSheetRecyclerLeft.setLayoutManager(mLayoutManagerLeft);

        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheetView);
      //  mBottomSheetBehavior.setHideable(false);

        mBottomSheetBehavior.setPeekHeight(150);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        mBottomSheetBehavior.setHideable(false);
        // mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {

                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
            }
        });

        mBottomSheetRecyclerLeft.setAdapter(listAdapter);

//        mAdapterLeft.update(modelsLeft);
//        mAdapterRight.update(modelsRight);

        //helper to rule scrolls
        BottomSheetBehaviorRecyclerManager manager = new BottomSheetBehaviorRecyclerManager(mParent, mBottomSheetBehavior, mBottomSheetView);
        manager.addControl(mBottomSheetRecyclerLeft);
        // manager.addControl(mBottomSheetRecyclerRight);
        manager.create();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setTrackDetails(Track track, boolean isSort) {
        List<Long> dates = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        ArrayList<Entry> entries = new ArrayList<>();
        entries.addAll(track.getEntries());
        Collections.reverse(entries);

        listAdapter = new EntryListAdapter(this,entries, track.getEntries(), this);
        mBottomSheetRecyclerLeft = (RecyclerView) findViewById(R.id.btm_recyclerview_left);
        mLayoutManagerLeft = new LinearLayoutManager(this);
        mBottomSheetRecyclerLeft.setLayoutManager(mLayoutManagerLeft);
        mBottomSheetRecyclerLeft.setAdapter(listAdapter);
       // getInitBottomSheet();

        for (int i = 0; i < track.getEntries().size(); i++) {
            Entry entry = track.getEntries().get(i);
            dates.add(entry.getDate());
            values.add(entry.getValue());
        }

        if (isSort) {
            Realm realm = null;
            try {
                realm = Realm.getDefaultInstance();
//            if (realm.isInTransaction()){
//                realm.commitTransaction();
//            }
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

                        // Collections.reverse(tracks);
                    }
                });
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        }

        renderData(dates, values, lineChartFull);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void renderData(List<Long> dates, List<Double> allAmounts, LineChart volumeReportChart) {

        XAxis xAxis = volumeReportChart.getXAxis();
        XAxis.XAxisPosition position = XAxis.XAxisPosition.BOTTOM;
        xAxis.setPosition(position);
        xAxis.enableGridDashedLine(2f, 7f, 0f);

        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawLabels(true);
        xAxis.setDrawLimitLinesBehindData(true);

        LimitLine ll1 = new LimitLine(Float.parseFloat("02220"), "");
        // ll1.setLineColor(getResources().getColor(R.color.colorPrimary));
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(0f, 0f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(15f, "");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(0f, 0f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        ll2.setLineColor(Color.parseColor("#FFFFFF"));

        xAxis.removeAllLimitLines();
        xAxis.setValueFormatter(new ClaimsXAxisValueFormatter());
        xAxis.addLimitLine(ll1);

        YAxis leftAxis = volumeReportChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);

        //  leftAxis.setAxisMinimum(0f);
        leftAxis.enableGridDashedLine(0f, 0f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(false);
        leftAxis.setDrawLabels(true);
        volumeReportChart.getDescription().setEnabled(false);
        Description description = new Description();
        description.setText("");
        description.setTextSize(15f);
        volumeReportChart.getDescription().setPosition(0f, 0f);
        volumeReportChart.setDescription(description);
        volumeReportChart.getAxisRight().setEnabled(false);

        setDataForWeeksWise(allAmounts, volumeReportChart, dates);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setDataForWeeksWise(List<Double> amounts, LineChart volumeReportChart, List<Long> dates) {

        ArrayList<com.github.mikephil.charting.data.Entry> values = new ArrayList<>();
        for (int i = 0; i < amounts.size(); i++) {
            values.add(new com.github.mikephil.charting.data.Entry(Float.parseFloat(String.valueOf(dates.get(i))), amounts.get(i).floatValue()));
//            values.add(new Entry(i, amounts.get(i).floatValue()));
        }

        LineDataSet set1;
        if (volumeReportChart.getData() != null &&
                volumeReportChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) volumeReportChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            volumeReportChart.getData().notifyDataChanged();
            volumeReportChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "");
            set1.setDrawCircles(true);
            set1.setLineWidth(2f);
            set1.setCircleRadius(5f);
            set1.setDrawCircleHole(true);
            set1.setValueTextSize(10f);
            set1.setDrawFilled(true);

            if (Utils.getSDKInt() >= 18) {
                if (track.getColor() != null){
                    Drawable unwrappedDrawable = AppCompatResources.getDrawable(this, R.drawable.gradient);
                    Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                    DrawableCompat.setTint(wrappedDrawable, Color.parseColor(track.getColor()));
                    set1.setFillDrawable(unwrappedDrawable);
                    set1.setCircleColor(Color.parseColor(track.getColor()));
                    set1.setColor(Color.parseColor(track.getColor()));
                } else {
                    Drawable unwrappedDrawable = AppCompatResources.getDrawable(this, R.drawable.gradient);
                    Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                    DrawableCompat.setTint(wrappedDrawable, this.getColor(R.color.colorPrimary));
                    set1.setFillDrawable(unwrappedDrawable);
                    set1.setCircleColor(this.getColor(R.color.colorPrimary));
                    set1.setColor(this.getColor(R.color.colorPrimary));
                }
            } else {
                set1.setFillColor(Color.WHITE);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            LineData data = new LineData(dataSets);

            volumeReportChart.animateY(3000, Easing.EaseOutBack);
            volumeReportChart.setTouchEnabled(true);
            volumeReportChart.setDrawGridBackground(true);
            volumeReportChart.getAxisLeft().setDrawGridLines(true);
            volumeReportChart.getXAxis().setDrawGridLines(true);
            volumeReportChart.setPinchZoom(true);
            volumeReportChart.setData(data);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onDeleteEntry() {
        lineChartFull.highlightValue(null);
        setTrackDetails(track,false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}