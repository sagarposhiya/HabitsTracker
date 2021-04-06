package com.example.habitstracker_verion.utils.recyclercallbacks;

import com.example.habitstracker_verion.models.Track;

import java.util.List;

public interface OnCustomerListChangedListener {
    void onNoteListChanged(List<Track> customers);
}
