package com.example.habitstracker_verion;

import android.app.Application;

import com.google.firebase.FirebaseApp;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
      //  FirebaseApp.initializeApp(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("habbitstracker.realm")
                .schemaVersion(0)
                .allowWritesOnUiThread(true)
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}
