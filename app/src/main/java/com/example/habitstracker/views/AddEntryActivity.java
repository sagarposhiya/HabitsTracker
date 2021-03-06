package com.example.habitstracker.views;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitstracker.R;
import com.example.habitstracker.adapters.AddEntryAdapter;
import com.example.habitstracker.models.Entry;
import com.example.habitstracker.models.Track;
import com.google.firebase.FirebaseApp;
import com.xw.repo.BubbleSeekBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class AddEntryActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    @BindView(R.id.txtDate)
    TextView txtDate;
    @BindView(R.id.rvEntry)
    RecyclerView rvEntry;
    @BindView(R.id.imgAdd)
    ImageView imgAdd;
    @BindView(R.id.imgClose)
    ImageView imgClose;
    @BindView(R.id.txtToolbarTitle)
    TextView txtToolbarTitle;
    DatePickerDialog dpd;
    Realm mRealm;
    RealmResults<Track> tracks;
    ArrayList<Track> lstTracks = new ArrayList<>();
    AddEntryAdapter addEntryAdapter;
    Calendar nowSelected;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    String formatted;
    int day, month, year, hour, minute;
    int myday, myMonth, myYear, myHour, myMinute;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        FirebaseApp.initializeApp(this);
        ButterKnife.bind(this);
        getInit();
        setEvents();
        setCurrentDate();
        getTracks();
        setTrackAdapter();
    }

    private void setTrackAdapter() {
        addEntryAdapter = new AddEntryAdapter(this, lstTracks);
        rvEntry.setAdapter(addEntryAdapter);
        rvEntry.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                ArrayList<Track> added = addEntryAdapter.getTracksFromList();
                for (int i = 0; i <added.size() ; i++) {
                    View view = rvEntry.getChildAt(i);
                    BubbleSeekBar bubbleSeekBar = (BubbleSeekBar) view.findViewById(R.id.bubbleBar);
                    bubbleSeekBar.correctOffsetWhenContainerOnScrolling();
                }
            }
        });
    }

    private void getTracks() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                tracks = realm.where(Track.class).sort("orderPosition", Sort.ASCENDING).findAll();
                lstTracks.addAll(tracks);
            }
        });
    }

    private void setCurrentDate() {
        nowSelected = Calendar.getInstance();
        //  now.set(year, month - 1, dayOfMonth, 0, 0);
        // SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format1 = new SimpleDateFormat("MMM dd YYYY hh:mm");
        String formatted = format1.format(nowSelected.getTime());
        txtToolbarTitle.setText("" + formatted);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setEvents() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(AddEntryActivity.this, AddEntryActivity.this, year, month, day);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getInit() {
        mRealm = Realm.getDefaultInstance();
        rvEntry.setLayoutManager(new LinearLayoutManager(this));
        txtToolbarTitle.setOnClickListener(this);
        imgAdd.setOnClickListener(this);
        imgClose.setOnClickListener(this);
        dpd = new DatePickerDialog(AddEntryActivity.this);
        dpd.getDatePicker().setMaxDate(new Date().getTime());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtToolbarTitle:
                openDatePicker();
                break;
            case R.id.imgAdd:
                addEntries();
                break;
            case R.id.imgClose:
                onBackPressed();
                break;
        }
    }

    private void addEntries() {
        ArrayList<Track> added = addEntryAdapter.getTracksFromList();
        ArrayList<Track> newToAdd = new ArrayList<>();
        for (int i = 0; i < added.size(); i++) {
            View view = rvEntry.getChildAt(i);
            EditText nameEditText = view.findViewById(R.id.mtfName);
            String name = nameEditText.getText().toString();

            if (!TextUtils.isEmpty(name)) {
                final Entry[] entry = new Entry[1];
                final Track[] track = new Track[1];
                Realm realm = null;
                try {
                    realm = Realm.getDefaultInstance();
                    int finalI = i;
                    int finalI1 = i;
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            track[0] = realm.where(Track.class)
                                    .equalTo("id", added.get(finalI1).getId())
                                    .findFirst();

                            RealmList<Entry> entries = track[0].getEntries();

                            entry[0] = realm.createObject(Entry.class, getNextKey());
                            entry[0].setParamName(track[0].getName());
                            entry[0].setDate(nowSelected.getTime().getTime());
                            entry[0].setValue(Double.parseDouble(name));

                        }
                    });

                } finally {
                    if (realm != null) {
                        realm.close();
                        onBackPressed();
                    }
                }

//                if (track[0].isValueChanged()) {
                realm.beginTransaction();
                RealmList<Entry> entries = track[0].getEntries();
                entries.add(entry[0]);
                realm.commitTransaction();
//                }
            }
        }
    }

    public int getNextKey() {
        try {
            Number number = mRealm.where(Entry.class).max("id");
            if (number != null) {
                return number.intValue() + 1;
            } else {
                return 0;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }

    public boolean checkIfExists(int id) {

        RealmQuery<Track> query = mRealm.where(Track.class)
                .equalTo("id", id);

        return query.count() != 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void openDatePicker() {
//        dpd.show();
        datePickerDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myday = dayOfMonth;
        myMonth = month;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        timePickerDialog = new TimePickerDialog(AddEntryActivity.this, AddEntryActivity.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;
        nowSelected = Calendar.getInstance();
        nowSelected.set(myYear, myMonth, myday, myHour, myMinute);
        SimpleDateFormat format1 = new SimpleDateFormat("MMM dd yyyy hh:mm");
        String formatted = format1.format(nowSelected.getTime());
        txtToolbarTitle.setText("" + formatted);
    }
}