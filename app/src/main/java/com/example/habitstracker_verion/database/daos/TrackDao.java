package com.example.habitstracker_verion.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.habitstracker_verion.models.Entry;
import com.example.habitstracker_verion.models.Track;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface TrackDao {

    @Query("SELECT * FROM track")
    List<Track> getAll();

    @Insert
    void insert(Track track);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllTracks(List<Track> tracks);

    @Delete
    void delete(Track track);

    @Update
    void update(Track track);

}
