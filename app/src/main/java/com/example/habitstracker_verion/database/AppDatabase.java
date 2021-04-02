package com.example.habitstracker_verion.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.habitstracker_verion.database.daos.EntryDao;
import com.example.habitstracker_verion.database.daos.TrackDao;
import com.example.habitstracker_verion.models.Entry;
import com.example.habitstracker_verion.models.Track;
import com.google.android.gms.tasks.Task;

@Database(entities = {Entry.class, Track.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EntryDao entryDao();
    public abstract TrackDao trackDao();
}
