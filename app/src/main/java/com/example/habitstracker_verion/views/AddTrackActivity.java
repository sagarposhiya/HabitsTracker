package com.example.habitstracker_verion.views;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitstracker_verion.R;
import com.example.habitstracker_verion.adapters.NewTrackAdapter;
import com.example.habitstracker_verion.adapters.TrackAddAdapter;
import com.example.habitstracker_verion.models.Track;
import com.example.habitstracker_verion.models.Unit;
import com.example.habitstracker_verion.utils.ItemMoveCallback;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class AddTrackActivity extends AppCompatActivity implements View.OnClickListener, TrackAddAdapter.onSavedListener {

    @BindView(R.id.llAddNewParam)
    LinearLayout llAddNewParam;
    @BindView(R.id.rvTracks)
    RecyclerView rvTracks;
    @BindView(R.id.imgAdd)
    ImageView imgAdd;
    @BindView(R.id.imgClose)
    ImageView imgClose;
    @BindView(R.id.llSlider)
    LinearLayout llSlider;
    @BindView(R.id.llYesNo)
    LinearLayout llYesNo;
    @BindView(R.id.llValue)
    LinearLayout llValue;
    RealmResults<Unit> units;
    ArrayList<String> lstUnits;
    Realm mRealm;
    RealmResults<Track> tracks;
    ArrayList<Track> lstTracks = new ArrayList<>();
    ArrayList<Track> newTracks = new ArrayList<>();
    TrackAddAdapter trackAddAdapter;
    NewTrackAdapter newTrackAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_track);
        FirebaseApp.initializeApp(this);
        ButterKnife.bind(this);
        getInit();
        getUnits();
        getTracks();
        setAdapter();
    }

    private void setAdapter() {
        trackAddAdapter = new TrackAddAdapter(this,lstTracks,lstUnits,this);
        rvTracks.setAdapter(trackAddAdapter);
        rvTracks.setItemViewCacheSize(lstTracks.size());
        ItemTouchHelper.Callback callback =
                new ItemMoveCallback(trackAddAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rvTracks);
        ArrayList<Track> trackArrayList = trackAddAdapter.getList();
        for (int i = 0; i < trackArrayList.size(); i++) {
            Log.e("TRACKS",trackArrayList.get(i).getName());
        }
    }

    private void getTracks() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                tracks = realm.where(Track.class).sort("orderPosition", Sort.ASCENDING).findAll();
                lstTracks.addAll(tracks);
                }
            });
    }

    private void getInit() {
        mRealm = Realm.getDefaultInstance();
        rvTracks.setLayoutManager(new LinearLayoutManager(this));
        llAddNewParam.setOnClickListener(this);
        llSlider.setOnClickListener(this);
        llYesNo.setOnClickListener(this);
        llValue.setOnClickListener(this);
        imgAdd.setOnClickListener(this);
        imgClose.setOnClickListener(this);
    }

    private void getUnits(){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                units = realm.where(Unit.class).findAll();
            }
        });
        lstUnits = new ArrayList<>();
        if (units.size() > 0){
            for (int i = 0; i < units.size(); i++) {
                lstUnits.add(units.get(i).getName());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llAddNewParam:
                addNewParam();
                break;
            case R.id.imgAdd:
                addTracks();
                break;
            case R.id.imgClose:
                onBackPressed();
                break;
            case R.id.llSlider:
                slider();
                break;
            case R.id.llYesNo:
                yesno();
                break;
            case R.id.llValue:
                valye();
                break;
        }
    }

    private void valye() {
    }

    private void yesno() {
    }

    private void slider() {
    }

    private void addTracks() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Saving Data");
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        ArrayList<Track> added = trackAddAdapter.getTracksFromList();
        ArrayList<Track> newToAdd = new ArrayList<>();
        for (int i = 0; i <added.size() ; i++) {
            View view = rvTracks.getChildAt(i);
            EditText nameEditText = (EditText) view.findViewById(R.id.edTrackName);
            String name = nameEditText.getText().toString();

            Realm realm = null;
            try {
                realm = Realm.getDefaultInstance();
                int finalI = i;
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if (!checkIfExists(added.get(finalI).getId()) || added.get(finalI).isNew()){ // || added.get(finalI).isEdited()
                            Track track = realm.createObject(Track.class,getNextKey());
                            track.setOrderPosition(getNextKey());
                            track.setName(name);
                            track.setUnit(added.get(finalI).getUnit());
                            track.setColor(added.get(finalI).getColor());
                            newToAdd.add(track);
                            realm.insert(track);
                        }
                    }
                });

            } finally {
                if (realm != null) {
                    realm.close();
                    progressDialog.dismiss();
                    onBackPressed();
                }
            }
        }
//        Log.e("Tracks",newToAdd.get(0).getName());
    }

    public boolean checkIfExists(int id){

        RealmQuery<Track> query = mRealm.where(Track.class)
                .equalTo("id", id);

        return query.count() != 0;
    }

    public int getNextKey() {
        try {
            Number number = mRealm.where(Track.class).max("id");
            if (number != null) {
                return number.intValue() + 1;
            } else {
                return 0;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }

    private void addNewParam() {
        Track track = new Track();
        track.setNew(true);
        lstTracks.add(track);
        trackAddAdapter.updateList(lstTracks);
        rvTracks.setItemViewCacheSize(lstTracks.size());
    }

    @Override
    public void onSavedClick() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();;
    }
}