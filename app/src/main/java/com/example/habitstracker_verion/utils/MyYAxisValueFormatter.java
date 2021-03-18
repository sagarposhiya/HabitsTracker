package com.example.habitstracker_verion.utils;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;

public class MyYAxisValueFormatter implements IValueFormatter {

    public MyYAxisValueFormatter() {
    }


    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy");
        return dateFormat.format(value);
    }
}
