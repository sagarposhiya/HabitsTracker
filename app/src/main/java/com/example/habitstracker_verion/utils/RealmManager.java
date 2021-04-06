package com.example.habitstracker_verion.utils;

import android.util.LongSparseArray;

import io.realm.Realm;

public class RealmManager {

    private volatile static LongSparseArray<Realm> THREAD_INSTANCES = new LongSparseArray<>();

    public static Realm getInstance() {
        final long threadId = Thread.currentThread().getId();

        if (THREAD_INSTANCES.indexOfKey(threadId) >= 0) {
            Realm instance = THREAD_INSTANCES.get(threadId);

            if (instance == null || instance.isClosed()) {
                instance = Realm.getDefaultInstance();

                THREAD_INSTANCES.put(threadId, instance);
            }

            return instance;
        } else {
            Realm instance = Realm.getDefaultInstance();
            THREAD_INSTANCES.put(threadId, instance);

            return instance;
        }
    }

    public static void closeInstance() {
        long threadId = Thread.currentThread().getId();

        if (THREAD_INSTANCES.indexOfKey(threadId) >= 0) {
            Realm instance = THREAD_INSTANCES.get(threadId);

            if (instance == null || instance.isClosed()) {
                THREAD_INSTANCES.remove(threadId);
            } else {
                instance.close();
                instance = null;

                THREAD_INSTANCES.remove(threadId);
            }
        }
    }
}
