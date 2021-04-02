package com.example.habitstracker_verion.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import io.realm.RealmObject;

@Entity
public class Entry extends RealmObject {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "date")
    private long date;

    @ColumnInfo(name = "paramDate")
    private String paramDate;

    @ColumnInfo(name = "paramName")
    private String paramName;

    @ColumnInfo(name = "value")
    private int value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getParamDate() {
        return paramDate;
    }

    public void setParamDate(String paramDate) {
        this.paramDate = paramDate;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }


}
