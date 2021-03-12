package com.example.habitstracker.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.habitstracker.R;

import java.util.ArrayList;

public class UnitAdapter extends ArrayAdapter<String> {

    LayoutInflater flater;

    public UnitAdapter(Activity context, int resouceId, int textviewId, ArrayList<String> list){
        super(context,resouceId,textviewId, list);
        flater = context.getLayoutInflater();
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String unit = getItem(position);

        View rowview = flater.inflate(R.layout.item_unit,parent,false);

        TextView txtTitle = (TextView) rowview.findViewById(R.id.title);
        txtTitle.setText(unit);

        ImageView imageView = (ImageView) rowview.findViewById(R.id.icon);
        //imageView.setImageResource(unit.getImageId());

        return rowview;
    }
}
