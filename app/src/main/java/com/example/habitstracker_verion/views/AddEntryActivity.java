package com.example.habitstracker_verion.views;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitstracker_verion.R;
import com.example.habitstracker_verion.adapters.AddEntryAdapter;
import com.example.habitstracker_verion.models.Entry;
import com.example.habitstracker_verion.models.Track;
import com.example.habitstracker_verion.utils.AppUtils;
import com.example.habitstracker_verion.utils.Constants;
import com.example.habitstracker_verion.utils.RealmManager;
import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xw.repo.BubbleSeekBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    @BindView(R.id.imgReminder)
    ImageView imgReminder;
    @BindView(R.id.btnDone)
    Button btnDone;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
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
    String color;
    String action;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    public static final String LIST_OF_SORTED_DATA_ID = "json_list_sorted_data_id";
    public final static String PREFERENCE_FILE = "preference_file";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        FirebaseApp.initializeApp(this);
        ButterKnife.bind(this);
        action = getIntent().getAction();
        setAppTheme();
        getInit();
        setEvents();
        setCurrentDate();
        getTracks();
        setTrackAdapter();
    }

    private void setAppTheme() {
        color = AppUtils.getStringPreference(this, Constants.themeColor);
        toolbar.setBackgroundColor(Color.parseColor(color));
        btnDone.setBackgroundColor(Color.parseColor(color));
    }

    private void setTrackAdapter() {
        lstTracks = getSortedList();
        mRealm = Realm.getDefaultInstance();
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
        SimpleDateFormat format1 = new SimpleDateFormat("MMM dd YYYY  hh:mm");
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
        mRealm = RealmManager.getInstance();
        rvEntry.setLayoutManager(new LinearLayoutManager(this));
        txtToolbarTitle.setOnClickListener(this);
        imgAdd.setOnClickListener(this);
        imgReminder.setOnClickListener(this);
        btnDone.setOnClickListener(this);
        imgClose.setOnClickListener(this);
        dpd = new DatePickerDialog(AddEntryActivity.this);
        dpd.getDatePicker().setMaxDate(new Date().getTime());

        mSharedPreferences = this.getApplicationContext()
                .getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
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
            case R.id.imgReminder:
                Intent intent1 = new Intent(AddEntryActivity.this, ReminderActivity.class);
                startActivity(intent1);
                break;
            case R.id.btnDone:
                addEntries();
                break;
        }
    }

    private void addEntries() {
        ArrayList<Track> added = addEntryAdapter.getTracksFromList();
        ArrayList<Track> newToAdd = new ArrayList<>();
        boolean isChanged = false;
        for (int i = 0; i < added.size(); i++) {
            View view = rvEntry.getChildAt(i);
            EditText nameEditText = view.findViewById(R.id.mtfName);
            String name = nameEditText.getText().toString();

//            if (!TextUtils.isEmpty(name)) {
                isChanged = true;
                final Entry[] entry = new Entry[1];
                final Track[] track = new Track[1];
                Realm realm = null;
                try {
                    realm = RealmManager.getInstance();
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
                }

                realm = RealmManager.getInstance();
                realm.beginTransaction();
                RealmList<Entry> entries = track[0].getEntries();
                entries.add(entry[0]);
                realm.commitTransaction();

        }

            Toast.makeText(this, "Saved Entries", Toast.LENGTH_SHORT).show();
            if (action.equalsIgnoreCase(Constants.NOTIFICATION)) {
                Toast.makeText(this, "Saved Entries", Toast.LENGTH_SHORT).show();
                Intent clearIntent = new Intent(this, DashboardActivity.class);
                clearIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                clearIntent.putExtra("exit", true);
                startActivity(clearIntent);
            } else if (action.equalsIgnoreCase(Constants.PLUS_BUTTON)){
                onBackPressed();
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
       RealmManager.closeInstance();
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
        SimpleDateFormat format1 = new SimpleDateFormat("MMM dd yyyy  hh:mm");
        String formatted = format1.format(nowSelected.getTime());
        txtToolbarTitle.setText("" + formatted);
    }
}