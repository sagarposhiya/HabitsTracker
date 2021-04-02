package com.example.habitstracker_verion.utils;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

public class DecimalValueFormatter implements IValueFormatter {

    private DecimalFormat mFormat;

    public DecimalValueFormatter() {
        mFormat = new DecimalFormat("###,###,##"); // use one decimal
        //mFormat = new DecimalFormat("###,###");
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return mFormat.format(value);
    }

//    @Override
//    public String getFormattedValue(float value, AxisBase axis) {
//        return mFormat.format(value);
//    }
//
//    @Override
//    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
//        return null;
//    }

}
