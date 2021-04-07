package com.example.habitstracker_verion.views;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.habitstracker_verion.LoginScreenActivity;
import com.example.habitstracker_verion.R;
import com.example.habitstracker_verion.adapters.EntryListAdapter;
import com.example.habitstracker_verion.adapters.TrackEntriesAdapter;
import com.example.habitstracker_verion.models.Entry;
import com.example.habitstracker_verion.models.FirebaseEntryParam;
import com.example.habitstracker_verion.models.FirebaseParam;
import com.example.habitstracker_verion.models.Track;
import com.example.habitstracker_verion.models.Unit;
import com.example.habitstracker_verion.utils.AppUtils;
import com.example.habitstracker_verion.utils.BottomSheetBehaviorRecyclerManager;
import com.example.habitstracker_verion.utils.ClaimsXAxisValueFormatter;
import com.example.habitstracker_verion.utils.Constants;
import com.example.habitstracker_verion.utils.ICustomBottomSheetBehavior;
import com.example.habitstracker_verion.utils.RealmManager;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.example.habitstracker_verion.utils.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hendraanggrian.recyclerview.widget.ExpandableRecyclerView;
import com.qhutch.bottomsheetlayout.BottomSheetLayout;

import java.io.Serializable;
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
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class DashboardActivity extends AppCompatActivity implements OnChartValueSelectedListener, View.OnClickListener, TrackEntriesAdapter.onChartClickListener, EntryListAdapter.onDeleteListner{
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.flotingAddEntry)
    FloatingActionButton flotingAddEntry;
    @BindView(R.id.rlFullGraph)
    RelativeLayout rlFullGraph;
    @BindView(R.id.lineChartFull)
    LineChart lineChartFull;
    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;
    //    @BindView(R.id.bottom_sheet2)
//    CoordinatorLayout bottom_sheet2;
    @BindView(R.id.llBottomSheet)
    LinearLayout llBottomSheet;
    @BindView(R.id.rvEntries)
    RecyclerView rvEntries;
    @BindView(R.id.imgArraows)
    ImageView imgArraows;
    @BindView(R.id.bottom_sheet_layout)
    BottomSheetLayout bottom_sheet_layout;
    @BindView(R.id.txtTitle)
    TextView txtTitle;
    String[] unitsArray;
    Realm mRealm;
    RealmResults<Track> tracks;
    ArrayList<Track> lstTracks = new ArrayList<>();
    TrackEntriesAdapter adapter;
    RecyclerView.Adapter myAdapter;
    // BottomSheetBehavior sheetBehavior;
    EntryListAdapter listAdapter;
    Track track;
    String color;
    private DatabaseReference mDatabase;

    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private View mBottomSheetView;

    private CoordinatorLayout mParent;


    private RecyclerView mBottomSheetRecyclerRight;
    private LinearLayoutManager mLayoutManagerRight;
   // private RecyclerAdapter mAdapterRight;

    private RecyclerView mBottomSheetRecyclerLeft;
    private LinearLayoutManager mLayoutManagerLeft;
   // private RecyclerAdapter mAdapterLeft;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    public static final String LIST_OF_SORTED_DATA_ID = "json_list_sorted_data_id";
    public final static String PREFERENCE_FILE = "preference_file";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent.hasExtra("exit")) {
            // finish immediately
            finish();
            finishAffinity();
            return;
        }

        setAppTheme();
        getInit();
        addUnits();
        getTracks();
        setTrackAdapter();
        setEvents();
       // findScrollingChild(rvEntries);
    }

    private void setAppTheme() {
        color = AppUtils.getStringPreference(this, Constants.themeColor);
        toolbar.setBackgroundColor(Color.parseColor(color));
        flotingAddEntry.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
    }
    private void getInitBottomSheet() {

        mBottomSheetView.setVisibility(View.VISIBLE);
        mBottomSheetRecyclerLeft = (RecyclerView) findViewById(R.id.btm_recyclerview_left);
        mLayoutManagerLeft = new LinearLayoutManager(this);
        mBottomSheetRecyclerLeft.setLayoutManager(mLayoutManagerLeft);

        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheetView);
        mBottomSheetBehavior.setHideable(false);

        mBottomSheetBehavior.setPeekHeight(150);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
       // mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    //  mBottomSheetBehavior.setPeekHeight(0);
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
            }
        });

        mBottomSheetRecyclerLeft.setAdapter(listAdapter);

        //helper to rule scrolls
        BottomSheetBehaviorRecyclerManager manager = new BottomSheetBehaviorRecyclerManager(mParent, mBottomSheetBehavior, mBottomSheetView);
        manager.addControl(mBottomSheetRecyclerLeft);
        manager.create();
    }

    public void toggleBottomSheet() {
        bottom_sheet_layout.toggle();
        if (bottom_sheet_layout.isExpended()) {
            bottom_sheet_layout.collapse();
            imgArraows.setImageDrawable(getDrawable(R.drawable.ic_up));
        } else {
            bottom_sheet_layout.expand();
            imgArraows.setImageDrawable(getDrawable(R.drawable.ic_down));
        }
    }

    private void setEvents() {
    }

    private void setTrackAdapter() {
        lstTracks = getSortedList();
        mRealm = Realm.getDefaultInstance();
        adapter = new TrackEntriesAdapter(this, this, lstTracks, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<Track> getSortedList(){
        ArrayList<Track> sortedCustomers = new ArrayList<Track>();

        //get the JSON array of the ordered of sorted customers
        String jsonListOfSortedCustomerId = mSharedPreferences.getString(LIST_OF_SORTED_DATA_ID, "");

        //check for null
        if (!jsonListOfSortedCustomerId.isEmpty()){

            //convert JSON array into a List<Long>
            Gson gson = new Gson();
            List<Long> listOfSortedCustomersId = gson.fromJson
                    (jsonListOfSortedCustomerId, new TypeToken<List<Long>>(){}.getType());

            //build sorted list
            if (listOfSortedCustomersId != null && listOfSortedCustomersId.size() > 0){
                for (Long id: listOfSortedCustomersId){
                    for (Track track: lstTracks){
                        if (track.getId() == id){
                            sortedCustomers.add(track);
                            lstTracks.remove(track);
                            break;
                        }
                    }
                }
            }

            //if there are still customers that were not in the sorted list
            //maybe they were added after the last drag and drop
            //add them to the sorted list
            if (lstTracks.size() > 0){
                sortedCustomers.addAll(lstTracks);
            }

            return sortedCustomers;
        }else {
            return lstTracks;
        }
    }

    private void getTracks() {
        mRealm = RealmManager.getInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                tracks = realm.where(Track.class).sort("orderPosition", Sort.ASCENDING).findAll();
                lstTracks.addAll(tracks);
                addinFirebase(tracks);
            }
        });
    }

    private void addinFirebase(RealmResults<Track> tracks) {
        for (int i = 0; i < tracks.size(); i++) {
            Log.e("ORDER", tracks.get(i).getName() + "  " + tracks.get(i).getOrderPosition() + "");
            ArrayList<FirebaseEntryParam> entryParams = new ArrayList<>();
            Track track = tracks.get(i);
            for (int j = 0; j < track.getEntries().size(); j++) {
                Entry entry = track.getEntries().get(j);
                FirebaseEntryParam param = new FirebaseEntryParam();
                param.setDate(entry.getDate());
                param.setValue(entry.getValue());
                entryParams.add(param);
            }

            String userId = AppUtils.getStringPreference(this, Constants.UserId);
            FirebaseParam param = new FirebaseParam();
            if (entryParams.size() > 0){
                param.setEntriesAvalable(true);
            } else {
                param.setEntriesAvalable(false);
            }
            param.setEntries(entryParams);
            param.setUid(userId);
            param.setUnit(track.getUnit());
            param.setName(track.getName());
            param.setTrackId(track.getId() + "");
            param.setColor(track.getColor());
            mDatabase.child("Tracks").child(userId).child(String.valueOf(track.getId())).setValue(param);
            AppUtils.setLongPreference(DashboardActivity.this,Constants.DB_UPDATED,AppUtils.getCurrentTime());
        }
    }

    private void getInit() {
        mRealm = RealmManager.getInstance();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.inflateMenu(R.menu.darshboard_options);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        unitsArray = getResources().getStringArray(R.array.unit_array);
        flotingAddEntry.setOnClickListener(this);
//        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rvEntries.setLayoutManager(new LinearLayoutManager(this));
        llBottomSheet.setOnClickListener(this);
        txtTitle.setText("Habits Tracker");
        rvEntries.setNestedScrollingEnabled(false);
        mParent = (CoordinatorLayout) findViewById(R.id.parent_container);
        mParent.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        mBottomSheetView = findViewById(R.id.main_bottomsheet);

        mBottomSheetView.setVisibility(View.GONE);

        mSharedPreferences = this.getApplicationContext()
                .getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

    }

    @Override
    public void onValueSelected(com.github.mikephil.charting.data.Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {
    }

    private void addUnits() {

        Realm realm = null;
        try {
            realm =RealmManager.getInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    try {
                        boolean isUnitAdded = AppUtils.getBooleanPreference(DashboardActivity.this, Constants.isUnitAdded);

                        if (!isUnitAdded) {
                            for (int i = 0; i < unitsArray.length; i++) {
                                Unit unit = realm.createObject(Unit.class);
                                // Unit unit = new Unit();
                               String strUnit = unitsArray[i];
                               strUnit = strUnit.replace(".", "");
                                unit.setName(strUnit);
                                unit.setDeletable(false);
                                realm.copyFromRealm(unit);
                            }

                            AppUtils.setBooleanPreference(DashboardActivity.this, Constants.isUnitAdded, true);
                        }

                    } catch (RealmPrimaryKeyConstraintException e) {
                        Toast.makeText(getApplicationContext(), "Primary Key exists, Press Update instead", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } finally {
            RealmManager.closeInstance();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.darshboard_options, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuEditParameter:
                Intent intent = new Intent(DashboardActivity.this, AddTrackActivity.class);
                startActivity(intent);
                break;
            case R.id.menuSetReminder:
                Intent intent1 = new Intent(DashboardActivity.this, ReminderActivity.class);
                startActivity(intent1);
                break;
            case R.id.menuSettings:
                mRealm.close();
                Intent intent2 = new Intent(DashboardActivity.this, SettingActivity.class);
                startActivity(intent2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       RealmManager.closeInstance();
       mRealm.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flotingAddEntry:
                Intent intent = new Intent(DashboardActivity.this, AddEntryActivity.class);
                intent.setAction(Constants.PLUS_BUTTON);
                startActivity(intent);
                break;
            case R.id.llBottomSheet:
                toggleBottomSheet();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRealm = RealmManager.getInstance();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
       // finish();
        if (rlFullGraph.getVisibility() == View.VISIBLE) {
            if (bottom_sheet_layout.isExpended()) {
                bottom_sheet_layout.collapse();
            } else {
                txtTitle.setText("Habits Tracker");
                startActivity(getIntent());
               // finish();
            }
        } else {
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onChartClick(Track track) {
        this.track = track;
        Intent intent = new Intent(DashboardActivity.this,FullGraphActivity.class);
        intent.putExtra(Constants.trackId,track.getId());
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setTrackDetails(Track track,boolean isSort) {
        List<Long> dates = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        ArrayList<Entry> entries = new ArrayList<>();
        entries.addAll(track.getEntries());
        Collections.reverse(entries);

        listAdapter = new EntryListAdapter(this,entries, track.getEntries(), this);
        rvEntries.setAdapter(listAdapter);

        getInitBottomSheet();
        for (int i = 0; i < track.getEntries().size(); i++) {
            Entry entry = track.getEntries().get(i);
            dates.add(entry.getDate());
            values.add(entry.getValue());
        }

        if (isSort) {
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

                        // Collections.reverse(tracks);
                    }
                });
            } finally {
                if (realm != null) {
                    RealmManager.closeInstance();
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
        volumeReportChart.getLegend().setEnabled(false);
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
            volumeReportChart.getLegend().setEnabled(false);
            volumeReportChart.setData(data);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onDeleteEntry() {
        bottom_sheet_layout.toggle();
        if (bottom_sheet_layout.isExpended()) {
            bottom_sheet_layout.collapse();
            imgArraows.setImageDrawable(getDrawable(R.drawable.ic_up));
        }
        lineChartFull.highlightValue(null);
        setTrackDetails(track,false);
    }

    @Override
    protected void onPause() {
        super.onPause();
     //   mRealm.close();
    }


}