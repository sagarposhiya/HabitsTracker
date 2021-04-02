package com.example.habitstracker_verion.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.habitstracker_verion.models.Entry;
import com.google.android.gms.tasks.Task;

import java.util.List;

@Dao
public interface EntryDao {

    @Query("SELECT * FROM entry")
    LiveData<List<Entry>> getAll();

    @Insert
    void insert(Entry entry);

    @Delete
    void delete(Entry entry);

    @Update
    void update(Entry entry);
}
