package com.example.habitstracker_verion.models;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public  class Track extends RealmObject{
    @PrimaryKey
    private int id;
    private String name;
    private String averageUnit;
    private String increment;
    private RealmList<Entry> entries;
    private int position;
    private boolean isNew;
    private boolean isEdited;
    private boolean isValueChanged;
    private String unit;
    private int orderPosition;
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

    public RealmList<Entry> getEntries() {
        return entries;
    }

    public void setEntries(RealmList<Entry> entries) {
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
