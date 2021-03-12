package com.example.habitstracker.models;

import java.util.ArrayList;

public class FirebaseParam {

    private ArrayList<FirebaseEntryParam> entries;
    private String trackId;
    private String uid;
    private String Name;
    private String unit;
    private String color;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public ArrayList<FirebaseEntryParam> getEntries() {
        return entries;
    }

    public void setEntries(ArrayList<FirebaseEntryParam> entries) {
        this.entries = entries;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
