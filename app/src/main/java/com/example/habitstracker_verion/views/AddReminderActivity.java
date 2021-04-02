package com.example.habitstracker_verion.views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.habitstracker_verion.R;
import com.example.habitstracker_verion.models.Reminder;
import com.example.habitstracker_verion.receivers.AlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;

public class AddReminderActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    @BindView(R.id.edTitle)
    EditText edTitle;
    @BindView(R.id.txtDate)
    TextView txtDate;
    @BindView(R.id.txtTime)
    TextView txtTime;
    @BindView(R.id.btnAdd)
    Button btnAdd;
    @BindView(R.id.imgBack)
    ImageView imgBack;

    Calendar nowSelected;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    String formatted;
    int day, month, year, hour, minute;
    int myday, myMonth, myYear, myHour, myMinute;
    final static int req1 = 1;
    Realm mRealm;

    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    String strDate;
    String strTime;
    long date;
    long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        ButterKnife.bind(this);
        getInit();
        setEvents();
        //setCurrentDate();
    }

    private void setEvents() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(AddReminderActivity.this, AddReminderActivity.this, year, month, day);
    }

    private void setCurrentDate() {
        nowSelected = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("MMM dd YYYY");
        String formatted = format1.format(nowSelected.getTime());
        txtDate.setText("" + formatted);
        strDate = formatted;
        date = nowSelected.getTime().getTime();
        SimpleDateFormat format2 = new SimpleDateFormat("hh:mm a");
        String formatted1 = format2.format(nowSelected.getTime());
        txtTime.setText("" + formatted1);
        strTime = formatted1;
        time = nowSelected.getTimeInMillis();

    }

    private void getInit() {
        mRealm = Realm.getDefaultInstance();
        imgBack.setOnClickListener(this);
        txtDate.setOnClickListener(this);
        txtTime.setOnClickListener(this);
        btnAdd.setOnClickListener(this);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.imgBack:
                onBackPressed();
                break;

            case R.id.txtTime:
                setTime();
                break;

            case R.id.txtDate:
                setDate();
                break;

            case R.id.btnAdd:
                addReminder();
                break;
        }
    }

    private void addReminder() {

        String title = edTitle.getText().toString();
        String strDate = txtDate.getText().toString();
        String strTime = txtTime.getText().toString();
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Enter Title", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(strDate)){
            Toast.makeText(this, "Select Date", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(strTime)){
            Toast.makeText(this, "Select Time", Toast.LENGTH_SHORT).show();
        } else {

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    //if (!checkIfExists(added.get(finalI).getId())){ // || added.get(finalI).isEdited()
                    Reminder reminder = realm.createObject(Reminder.class, getNextKey());
                    reminder.setStrDate(strDate);
                    reminder.setStrTime(strTime);
                    reminder.setDate(date);
                    reminder.setTime(time);
                    reminder.setTitle(title);

                  //  realm.insert(reminder);

                    Calendar cal=Calendar.getInstance();
                    cal.set(Calendar.MONTH,month);
                    cal.set(Calendar.YEAR,myYear);
                    cal.set(Calendar.DAY_OF_MONTH,myday);
                    cal.set(Calendar.HOUR_OF_DAY,myHour);
                    cal.set(Calendar.MINUTE,myMinute);


                //    setAlarm(cal);
                    startAlarm();


                    Toast.makeText(AddReminderActivity.this, "Added", Toast.LENGTH_SHORT).show();
                    edTitle.setText("");
                   // setCurrentDate();

//                    Calendar calendar = Calendar.getInstance();
//                    calendar.set(myYear, myMonth, myday,myHour,myMinute);


                    // }
                }
            });

        }

    }

    private void startAlarm() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
        }


    }

    private void setDate() {
        datePickerDialog.show();
    }

    private void setTime() {
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        timePickerDialog = new TimePickerDialog(AddReminderActivity.this, AddReminderActivity.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myday = dayOfMonth;
        myMonth = month;
        nowSelected = Calendar.getInstance();
        nowSelected.set(myYear, myMonth, myday);
        SimpleDateFormat format1 = new SimpleDateFormat("MMM dd YYYY");
        String formatted = format1.format(nowSelected.getTime());
        strDate = formatted;
        txtDate.setText("" + formatted);
        date = nowSelected.getTime().getTime();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;
        nowSelected = Calendar.getInstance();
        nowSelected.set(myYear, myMonth, myday, myHour, myMinute);
        SimpleDateFormat format1 = new SimpleDateFormat("hh:mm a");
        String formatted = format1.format(nowSelected.getTime());
        strTime = formatted;
        txtTime.setText("" + formatted);
        time = nowSelected.getTimeInMillis();
    }

    private void setAlarm(Calendar target) {
        SimpleDateFormat format1 = new SimpleDateFormat("MMM dd YYYY hh:mm:ss a");
        String formatted = format1.format(target.getTime());
        Log.e("Alarm","TIME :- " + target + " DATE :- " + formatted );
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), req1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, target.getTimeInMillis(), pendingIntent);
    }

    public boolean checkIfExists(int id) {

        RealmQuery<Reminder> query = mRealm.where(Reminder.class)
                .equalTo("id", id);

        return query.count() != 0;
    }

    public int getNextKey() {
        try {
            Number number = mRealm.where(Reminder.class).max("id");
            if (number != null) {
                return number.intValue() + 1;
            } else {
                return 0;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
        ;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        startActivity(new Intent(this,ReminderActivity.class));
        finish();
    }
}