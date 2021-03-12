package com.example.habitstracker.models;

import io.realm.RealmObject;

public class Unit extends RealmObject {

    private String name;
    private boolean isDeletable;

    public Unit(){}

    public Unit(String name,boolean isDeletable){
        this.name = name;
        this.isDeletable = isDeletable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDeletable() {
        return isDeletable;
    }

    public void setDeletable(boolean deletable) {
        isDeletable = deletable;
    }
}
