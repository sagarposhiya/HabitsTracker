package com.example.habitstracker_verion.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.habitstracker_verion.R;
import com.example.habitstracker_verion.adapters.AlarmsAdapter;
import com.example.habitstracker_verion.adapters.ReminderAdapter;
import com.example.habitstracker_verion.models.Alarm;
import com.example.habitstracker_verion.models.Reminder;
import com.example.habitstracker_verion.receivers.LoadAlarmsReceiver;
import com.example.habitstracker_verion.receivers.LoadAlarmsService;
import com.example.habitstracker_verion.utils.AlarmUtils;
import com.example.habitstracker_verion.utils.AppUtils;
import com.example.habitstracker_verion.utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.example.habitstracker_verion.views.AddEditAlarmActivity.ADD_ALARM;
import static com.example.habitstracker_verion.views.AddEditAlarmActivity.buildAddEditAlarmActivityIntent;

public class ReminderActivity extends AppCompatActivity implements View.OnClickListener, LoadAlarmsReceiver.OnAlarmsLoadedListener {

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.flotingAddReminder)
    FloatingActionButton flotingAddReminder;
    @BindView(R.id.rvReminders)
    RecyclerView rvReminders;
    private LoadAlarmsReceiver mReceiver;
    Realm mRealm;
    RealmResults<Reminder> reminders;
    ArrayList<Reminder> lstReminders = new ArrayList<>();
    ReminderAdapter adapter;
    private AlarmsAdapter mAdapter;
    String color;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        ButterKnife.bind(this);
        setAppTheme();
        mReceiver = new LoadAlarmsReceiver(this);
        getInit();
       // getReminder();
        setReminderAdapter();
    }

    private void setAppTheme() {
        color = AppUtils.getStringPreference(this, Constants.themeColor);
        toolbar.setBackgroundColor(Color.parseColor(color));
        //  btnDone.setBackgroundColor(Color.parseColor(color));
        flotingAddReminder.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        final IntentFilter filter = new IntentFilter(LoadAlarmsService.ACTION_COMPLETE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
        LoadAlarmsService.launchLoadAlarmsService(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    private void setReminderAdapter() {
       // adapter = new ReminderAdapter(this, lstReminders);
        mAdapter = new AlarmsAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //myAdapter = new MyAdapter(layoutManager,this,lstTracks);
        rvReminders.setLayoutManager(layoutManager);
        // exRvList.setAdapter(myAdapter);
        rvReminders.setAdapter(mAdapter);
    }

    private void getInit() {
        mRealm = Realm.getDefaultInstance();
        imgBack.setOnClickListener(this);

        flotingAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(ReminderActivity.this,AddReminderActivity.class));
                AlarmUtils.checkAlarmPermissions(ReminderActivity.this);
                final Intent i = buildAddEditAlarmActivityIntent(ReminderActivity.this, ADD_ALARM);
                startActivity(i);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgBack:
                onBackPressed();
                break;
        }
    }

    private void getReminder() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                reminders = realm.where(Reminder.class).findAll();
                lstReminders.addAll(reminders);
               // addinFirebase(tracks);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onAlarmsLoaded(ArrayList<Alarm> alarms) {
        mAdapter.setAlarms(alarms);
    }
}