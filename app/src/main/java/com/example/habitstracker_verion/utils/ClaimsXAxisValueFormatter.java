package com.example.habitstracker_verion.utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClaimsXAxisValueFormatter extends IndexAxisValueFormatter {

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // Convert float value to date string
        // Convert from seconds back to milliseconds to format time  to show to the user
//        long emissionsMilliSince1970Time = ((long) value) * 1000;
        long emissionsMilliSince1970Time = ((long) value);

        // Show time in local version
//        SimpleDateFormat format = new SimpleDateFormat("MMM dd, hh:mm a");
        SimpleDateFormat format = new SimpleDateFormat("MMM dd");
       // SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        Date timeMilliseconds = new Date(emissionsMilliSince1970Time);
       // DateFormat dateTimeFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        return format.format(timeMilliseconds);
    }
}
