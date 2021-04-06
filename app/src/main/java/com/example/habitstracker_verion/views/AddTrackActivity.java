package com.example.habitstracker_verion.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitstracker_verion.R;
import com.example.habitstracker_verion.adapters.NewTrackAdapter;
import com.example.habitstracker_verion.adapters.TrackAddAdapter;
import com.example.habitstracker_verion.models.Entry;
import com.example.habitstracker_verion.models.Track;
import com.example.habitstracker_verion.models.Unit;
import com.example.habitstracker_verion.utils.AppUtils;
import com.example.habitstracker_verion.utils.Constants;
import com.example.habitstracker_verion.utils.RealmManager;
import com.example.habitstracker_verion.utils.WrapContentLinearLayoutManager;
import com.example.habitstracker_verion.utils.recyclercallbacks.OnCustomerListChangedListener;
import com.example.habitstracker_verion.utils.recyclercallbacks.OnStartDragListener;
import com.example.habitstracker_verion.utils.recyclercallbacks.SimpleItemTouchHelperCallback;
import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class AddTrackActivity extends AppCompatActivity implements View.OnClickListener, TrackAddAdapter.onSavedListener, OnCustomerListChangedListener,
        OnStartDragListener {

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
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    RealmResults<Unit> units;
    ArrayList<String> lstUnits;
    Realm mRealm;
    RealmResults<Track> tracks;
    ArrayList<Track> lstTracks = new ArrayList<>();
    ArrayList<Track> newTracks = new ArrayList<>();
    TrackAddAdapter trackAddAdapter;
    NewTrackAdapter newTrackAdapter;
    String color;
    private ItemTouchHelper mItemTouchHelper;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    public static final String LIST_OF_SORTED_DATA_ID = "json_list_sorted_data_id";
    public final static String PREFERENCE_FILE = "preference_file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_track);
        FirebaseApp.initializeApp(this);
        ButterKnife.bind(this);
        setAppTheme();
        getInit();
        getUnits();
        getTracks(Constants.SHOW);
        setAdapter();
    }

    private void setAppTheme() {
        color = AppUtils.getStringPreference(this, Constants.themeColor);
        toolbar.setBackgroundColor(Color.parseColor(color));
        //  btnDone.setBackgroundColor(Color.parseColor(color));
    }

    private void setAdapter() {
        lstTracks = getSortedList();
        mRealm = Realm.getDefaultInstance();
        trackAddAdapter = new TrackAddAdapter(this, lstTracks, lstUnits, this,this,this);
        rvTracks.setItemViewCacheSize(lstTracks.size());
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(trackAddAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvTracks);
        rvTracks.setAdapter(trackAddAdapter);
    }

    private void getTracks(String add) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                tracks = realm.where(Track.class).sort("orderPosition", Sort.ASCENDING).findAll();
                lstTracks.addAll(tracks);
            }
        });
    }

    private ArrayList<Track> getSortedList(){
        ArrayList<Track> sortedCustomers = new ArrayList<Track>();

        //get the JSON array of the ordered of sorted customers
        String jsonListOfSortedCustomerId = mSharedPreferences.getString(LIST_OF_SORTED_DATA_ID, "");

        //check for null
        if (!jsonListOfSortedCustomerId.isEmpty()){

            //convert JSON array into a List<Long>
            Gson gson = new Gson();
            List<Long> listOfSortedCustomersId = gson.fromJson
                    (jsonListOfSortedCustomerId, new TypeToken<List<Long>>(){}.getType());

            //build sorted list
            if (listOfSortedCustomersId != null && listOfSortedCustomersId.size() > 0){
                for (Long id: listOfSortedCustomersId){
                    for (Track track: lstTracks){
                        if (track.getId() == id){
                            sortedCustomers.add(track);
                            lstTracks.remove(track);
                            break;
                        }
                    }
                }
            }

            //if there are still customers that were not in the sorted list
            //maybe they were added after the last drag and drop
            //add them to the sorted list
            if (lstTracks.size() > 0){
                sortedCustomers.addAll(lstTracks);
            }

            return sortedCustomers;
        }else {
            return lstTracks;
        }
    }

    private void getInit() {
        mRealm = RealmManager.getInstance();
      //  rvTracks.setLayoutManager(new LinearLayoutManager(this));
        rvTracks.setLayoutManager(new WrapContentLinearLayoutManager(AddTrackActivity.this));
        llAddNewParam.setOnClickListener(this);
        llSlider.setOnClickListener(this);
        llYesNo.setOnClickListener(this);
        llValue.setOnClickListener(this);
        imgAdd.setOnClickListener(this);
        imgClose.setOnClickListener(this);

        mSharedPreferences = this.getApplicationContext()
                .getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    private void getUnits() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                units = realm.where(Unit.class).findAll();
            }
        });
        lstUnits = new ArrayList<>();
        if (units.size() > 0) {
            for (int i = 0; i < units.size(); i++) {
                lstUnits.add(units.get(i).getName());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
        for (int i = 0; i < added.size(); i++) {
            View view = rvTracks.getChildAt(i);
            EditText nameEditText = (EditText) view.findViewById(R.id.edTrackName);
            String name = nameEditText.getText().toString();

            Realm realm = null;
            try {
                realm = RealmManager.getInstance();
                int finalI = i;
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if (!checkIfExists(added.get(finalI).getId()) || added.get(finalI).isNew()) { // || added.get(finalI).isEdited()
                            RealmList<Entry> entries = new RealmList<>();
                            Track track = realm.createObject(Track.class, getNextKey());
                            track.setOrderPosition(getNextKey());
                            track.setName(name);
                            track.setUnit(added.get(finalI).getUnit());
                            track.setColor(added.get(finalI).getColor());
                            track.setEntries(entries);
                            newToAdd.add(track);
                            realm.insert(track);
                        }
                    }
                });

            } finally {
                if (realm != null) {
                    RealmManager.closeInstance();
                    progressDialog.dismiss();
                    onBackPressed();
                }
            }
        }
//        Log.e("Tracks",newToAdd.get(0).getName());
    }

    public boolean checkIfExists(int id) {

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
       // lstTracks.clear();
//        getTracks(Constants.ADD);
        lstTracks = trackAddAdapter.getLstTracks();
        Track track = new Track();
        track.setNew(true);
        lstTracks.add(track);
        setAdapter();
//        trackAddAdapter.updateList(lstTracks);
//        rvTracks.setItemViewCacheSize(lstTracks.size());
    }

    @Override
    public void onDeleteTrack(Track track, int position) {
//        if (position - 1 >= 0) {
//            lstTracks.remove(position - 1);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       RealmManager.closeInstance();
    }

    @Override
    public void onNoteListChanged(List<Track> customers) {
        //after drag and drop operation, the new list of Customers is passed in here

        //create a List of Long to hold the Ids of the
        //Customers in the List
        List<Integer> listOfSortedCustomerId = new ArrayList<Integer>();

        for (Track customer: customers){
            listOfSortedCustomerId.add(customer.getId());
        }

        //convert the List of Longs to a JSON string
        Gson gson = new Gson();
        String jsonListOfSortedCustomerIds = gson.toJson(listOfSortedCustomerId);


        //save to SharedPreference
        mEditor.putString(LIST_OF_SORTED_DATA_ID, jsonListOfSortedCustomerIds).commit();
        mEditor.commit();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}