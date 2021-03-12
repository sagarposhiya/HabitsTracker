package com.example.habitstracker.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitstracker.R;
import com.example.habitstracker.models.Track;
import com.example.habitstracker.utils.AppUtils;
import com.example.habitstracker.utils.Constants;
import com.example.habitstracker.utils.ItemMoveCallback;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class TrackAddAdapter extends RecyclerView.Adapter<TrackAddAdapter.ViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {

    Activity context;
    ArrayList<Track> tracks;
    onSavedListener listener;
    ArrayList<String> units;

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        int tempstart = fromPosition;
        int tempend = toPosition;
        moveItem(tracks.get(fromPosition),fromPosition,toPosition);
        notifyItemMoved(fromPosition, toPosition);

//        if (fromPosition < toPosition) {
//            for (int i = fromPosition; i <= toPosition - 1; i++) {
//                Collections.swap(tracks, i, i + 1);
//                Realm realm = null;
//                try {
//                    realm = Realm.getDefaultInstance();
//                    int finalI = i;
//                    realm.executeTransaction(new Realm.Transaction() {
//                        @Override
//                        public void execute(Realm realm) {
//                            Track track1 = realm.where(Track.class).equalTo("id",tracks.get(fromPosition).getId()).findFirst();
//                            track1.setOrderPosition(tracks.get(finalI).getOrderPosition());
//                            realm.insertOrUpdate(track1);
//                        }
//                    });
//                } finally {
//                    if (realm != null) {
//                        realm.close();
//                    }
//                }
//            }
////
//        } else {
//            for (int i = fromPosition; i >= toPosition + 1; i--) {
//                Collections.swap(tracks, i, i - 1);
//                Realm realm = null;
//                try {
//                    realm = Realm.getDefaultInstance();
//                    int finalI = i;
//                    realm.executeTransaction(new Realm.Transaction() {
//                        @Override
//                        public void execute(Realm realm) {
//                            Track track1 = realm.where(Track.class).equalTo("id",tracks.get(fromPosition).getId()).findFirst();
//                            track1.setOrderPosition(tracks.get(finalI).getOrderPosition());
//                            realm.insertOrUpdate(track1);
//                        }
//                    });
//                } finally {
//                    if (realm != null) {
//                        realm.close();
//                    }
//                }
//            }
//        }
//        }
       // notifyItemMoved(fromPosition, toPosition);
//
//        Log.d("drop", String.valueOf(fromPosition));
//        Log.d("drop", String.valueOf(toPosition));





//        Realm realm = null;
//        try {
//            realm = Realm.getDefaultInstance();
//            realm.executeTransaction(new Realm.Transaction() {
//                @Override
//                public void execute(Realm realm) {
//                    Track track1 = realm.where(Track.class).equalTo("id",tracks.get(fromPosition).getId()).findFirst();
//                   // Track track = tracks.get(fromPosition);
//                    track1.setOrderPosition(toPosition);
//                    realm.insertOrUpdate(track1);
//                   // realm.insertOrUpdate(tracks);
//                    Log.e("ORDERED",tracks.get(fromPosition).getName() + "  " + (toPosition - 1) + "");
//                }
//            });
//
//        } finally {
//            if (realm != null) {
//                realm.close();
//            }
//        }
    }

    public void moveItem(Track item, final int fromPosition, final int toPosition) {
        final int index = item.getOrderPosition();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Track item = realm.where(Track.class).equalTo("orderPosition", index).findFirst();
                    if (fromPosition < toPosition) {
                        RealmResults<Track> results = realm.where(Track.class)
                                .greaterThan("orderPosition", fromPosition)
                                .lessThanOrEqualTo("orderPosition", toPosition)
                                .findAll();
                        for (int i = 0; i < results.size(); i++) {
                            // results.get(i).index -= 1;
                            results.get(i).setOrderPosition(results.get(i).getOrderPosition() - 1);
                        }
                    } else {
                        RealmResults<Track> results = realm.where(Track.class)
                                .greaterThanOrEqualTo("orderPosition", toPosition)
                                .lessThan("orderPosition", fromPosition)
                                .findAll();
                        for (int i = 0; i < results.size(); i++) {
                            // results.get(i).index += 1;
                            results.get(i).setOrderPosition(results.get(i).getOrderPosition() + 2);
                        }
                    }
                    // item.index = toPosition;
                    item.setOrderPosition(toPosition);
                }
            });
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        //RealmRefresh.refreshRealm(realm);
        realm.refresh();
    }

    @Override
    public void onRowSelected(ViewHolder myViewHolder) {
        myViewHolder.cardTrack.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(ViewHolder myViewHolder) {
        myViewHolder.cardTrack.setBackgroundColor(Color.WHITE);
    }

    public interface onSavedListener {
        void onSavedClick();
    }

    public TrackAddAdapter(Activity context, ArrayList<Track> tracks, ArrayList<String> units, onSavedListener listener) {
        this.context = context;
        this.tracks = tracks;
        this.units = units;
        this.listener = listener;
    }

    public ArrayList<Track> getList(){
        return tracks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_track, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Track track = tracks.get(position);
        if (track.isNew()) {
            UnitAdapter unitAdapter = new UnitAdapter(context,
                    R.layout.item_unit, R.id.title, units);

            holder.spinnerUnit.setAdapter(unitAdapter);
        } else {
            holder.edTrackName.setText(track.getName());

            UnitAdapter unitAdapter = new UnitAdapter(context,
                    R.layout.item_unit, R.id.title, units);
            holder.spinnerUnit.setAdapter(unitAdapter);
            for (int i = 0; i < units.size(); i++) {
                if (track.getUnit().equalsIgnoreCase(units.get(i))) {
                    holder.spinnerUnit.setSelection(i);
                }
            }
        }

        if (track.getColor() != null) {
            holder.imgColor.setColorFilter(Color.parseColor(track.getColor()));
        }

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    track.setUnit(units.get(0));
                    if (track.getColor() == ""){
                        track.setColor("#14748a");
                    }
                    Log.e("Selected Unit :- ", units.get(0) + " " );
                }
            });

            Realm finalRealm = realm ;
//            Realm finalRealm = Realm.getDefaultInstance();
            holder.spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    finalRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            track.setUnit(units.get(position));
                            track.setEdited(true);
                        }
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            holder.edTrackName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    finalRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            track.setName(holder.edTrackName.getText().toString());
                            track.setEdited(true);
                        }
                    });
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            Realm finalRealm1 = realm;
            holder.imgColor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ColorPickerDialogBuilder
                            .with(context)
                            .setTitle("Choose color")
                           // .initialColor(currentBackgroundColor)
                            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                            .density(12)
                            .showAlphaSlider(false)
                            .setOnColorSelectedListener(new OnColorSelectedListener() {
                                @Override
                                public void onColorSelected(int selectedColor) {
                                 //   toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                                }
                            })
                            .setPositiveButton("ok", new ColorPickerClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                    //changeBackgroundColor(selectedColor);
                                    String hexColor = String.format("#%06X", (0xFFFFFF & selectedColor));
                                   // holder.imgColor.setBackgroundColor(Color.parseColor(hexColor));
                                    holder.imgColor.setColorFilter(Color.parseColor(hexColor));

                                    finalRealm1.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            track.setColor(hexColor);
                                            if (!track.isNew()) {
                                                realm.insertOrUpdate(track);
                                            }
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .build()
                            .show();
                }
            });

            holder.imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(context.getString(R.string.txtMsgDeleteEntry));
                            builder.setMessage("Are you sure ?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Realm realm = null;
                                    try {
                                        realm = Realm.getDefaultInstance();
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                tracks.remove(position);
                                                notifyItemChanged(position);
                                                notifyItemRangeChanged(position, tracks.size());
                                                track.setEdited(true);

                                                Track exTrack = realm.where(Track.class).equalTo("id", track.getId()).findFirst();
                                                if (exTrack != null) {
                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                                String userId = AppUtils.getStringPreference(context, Constants.UserId);
                                                Query applesQuery = ref.child("Tracks").child(userId).orderByChild("trackId").equalTo(track.getId() + "");

                                                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for (DataSnapshot appleSnapshot : snapshot.getChildren()) {
                                                            appleSnapshot.getRef().removeValue();

                                                            realm.executeTransaction(new Realm.Transaction() {
                                                                @Override
                                                                public void execute(Realm realm) {
                                                                    exTrack.deleteFromRealm();
                                                                }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                            }
                                        });

                                    } finally {
                                        if (realm != null) {
                                            realm.close();
//                                            finalRealm.close();
//                                            finalRealm1.close();
                                        }
                                    }
                                }
                            });

                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                        }
                    });
                }
            });

        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public ArrayList<Track> getTracksFromList() {
        return tracks;
    }

    public void updateList(ArrayList<Track> results) {
        //this.tracks.clear();
        this.tracks = results;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.edTrackName)
        EditText edTrackName;
        @BindView(R.id.spinnerUnit)
        Spinner spinnerUnit;
        @BindView(R.id.imgClose)
        ImageView imgClose;
        @BindView(R.id.cardTrack)
        CardView cardTrack;
        @BindView(R.id.imgColor)
        ImageView imgColor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
