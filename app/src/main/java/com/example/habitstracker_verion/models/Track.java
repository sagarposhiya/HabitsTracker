package com.example.habitstracker_verion.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

@Entity
public  class Track {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "averageUnit")
    private String averageUnit;

    @ColumnInfo(name = "increment")
    private String increment;

    @ColumnInfo(name = "entries")
    List<Entry> entries;

    @ColumnInfo(name = "position")
    private int position;

    @ColumnInfo(name = "isNew")
    private boolean isNew;

    @ColumnInfo(name = "isEdited")
    private boolean isEdited;

    @ColumnInfo(name = "isValueChanged")
    private boolean isValueChanged;

    @ColumnInfo(name = "unit")
    private String unit;

    @ColumnInfo(name = "orderPosition")
    private int orderPosition;

    @ColumnInfo(name = "color")
    private String color;

    public Track(){
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAverageUnit() {
        return averageUnit;
    }

    public void setAverageUnit(String averageUnit) {
        this.averageUnit = averageUnit;
    }

    public String getIncrement() {
        return increment;
    }

    public void setIncrement(String increment) {
        this.increment = increment;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public boolean isValueChanged() {
        return isValueChanged;
    }

    public void setValueChanged(boolean valueChanged) {
        isValueChanged = valueChanged;
    }

    public int getOrderPosition() {
        return orderPosition;
    }

    public void setOrderPosition(int orderPosition) {
        this.orderPosition = orderPosition;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
