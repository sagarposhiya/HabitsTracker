package com.example.habitstracker_verion.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitstracker_verion.R;
import com.example.habitstracker_verion.models.Track;
import com.example.habitstracker_verion.utils.AppUtils;
import com.example.habitstracker_verion.utils.Constants;
import com.example.habitstracker_verion.utils.ItemMoveCallback;
import com.example.habitstracker_verion.utils.RealmManager;
import com.example.habitstracker_verion.utils.recyclercallbacks.ItemTouchHelperAdapter;
import com.example.habitstracker_verion.utils.recyclercallbacks.ItemTouchHelperViewHolder;
import com.example.habitstracker_verion.utils.recyclercallbacks.OnCustomerListChangedListener;
import com.example.habitstracker_verion.utils.recyclercallbacks.OnStartDragListener;
import com.example.habitstracker_verion.views.AddTrackActivity;
import com.example.habitstracker_verion.views.DashboardActivity;
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
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class TrackAddAdapter extends RecyclerView.Adapter<TrackAddAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    Activity context;
    static ArrayList<Track> tracks;
    onSavedListener listener;
    ArrayList<String> units;
    AddTrackActivity addTrackActivity = new AddTrackActivity();
    private OnStartDragListener mDragStartListener;
    private OnCustomerListChangedListener mListChangedListener;

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(tracks, fromPosition, toPosition);
        mListChangedListener.onNoteListChanged(tracks);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {

    }


    public interface onSavedListener {
        void onDeleteTrack(Track track, int position);
    }

    public TrackAddAdapter(Activity context, ArrayList<Track> tracks, ArrayList<String> units, onSavedListener listener, OnStartDragListener dragLlistener,
                           OnCustomerListChangedListener listChangedListener) {
        this.context = context;
        this.tracks = tracks;
        this.units = units;
        this.listener = listener;
        mDragStartListener = dragLlistener;
        mListChangedListener = listChangedListener;
    }

    public ArrayList<Track> getList() {
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

            if (track.getName() != null) {
                holder.edTrackName.setText(track.getName());
            }

            //
        } else {
            // if (!track.isNew()) {
            holder.edTrackName.setText(track.getName());
            // }

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

        holder.imgDrag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });

        Realm realm = null;
        try {
            realm = RealmManager.getInstance();
            realm.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realm) {
                    track.setUnit(units.get(0));
                    if (track.getColor() == "") {
                        track.setColor("#14748a");
                    }
                    Log.e("Selected Unit :- ", units.get(0) + " ");
                }
            });

            Realm finalRealm = RealmManager.getInstance();
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
                    track.setName(" ");
                    track.setEdited(false);
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    finalRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            track.setName(holder.edTrackName.getText().toString());
                            track.setEdited(true);
//                            Track track1 = realm.where(Track.class).equalTo("id", track.getId()).findFirst();
//                            if (track1 != null) {
//                               // tracks.get(position).setName(holder.edTrackName.getText().toString());
//
//                            }
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
                            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                            .showAlphaSlider(false)
                            .setOnColorSelectedListener(new OnColorSelectedListener() {
                                @Override
                                public void onColorSelected(int selectedColor) {

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

                    Realm realm = null;
                    try {
                        realm = RealmManager.getInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                if (track.isNew()) {
                                    tracks.remove(position);
                                    notifyItemChanged(position);
                                    notifyItemRangeChanged(position, tracks.size());
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle(context.getString(R.string.txtMsgDeleteEntry));
                                    builder.setMessage("Are you sure ?");
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Realm realm = null;
                                            try {
                                                realm = RealmManager.getInstance();
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
                                                            AppUtils.setLongPreference(context, Constants.DB_UPDATED, AppUtils.getCurrentTime());
                                                            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    for (DataSnapshot appleSnapshot : snapshot.getChildren()) {
                                                                        appleSnapshot.getRef().removeValue();

                                                                        realm.executeTransaction(new Realm.Transaction() {
                                                                            @Override
                                                                            public void execute(Realm realm) {
                                                                                listener.onDeleteTrack(track, position);
                                                                                exTrack.deleteFromRealm();
                                                                                // addTrackActivity.lstTracks.remove(position);
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
                                                RealmManager.closeInstance();
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
                            }
                        });
                    } finally {
                        RealmManager.closeInstance();
                    }

//                    finalRealm.executeTransaction(new Realm.Transaction() {
//                        @Override
//                        public void execute(Realm realm) {
//
//
//                        }
//                    });
                }
            });

        } finally {
            RealmManager.closeInstance();
        }
    }

    public ArrayList<Track> getLstTracks() {
        return tracks;
    }

    public ArrayList<Track> getTracksFromList() {
        return tracks;
    }

    public void updateList(final ArrayList<Track> results) {
        // this.tracks.clear();
        this.tracks = results;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

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
        @BindView(R.id.imgDrag)
        ImageView imgDrag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onItemSelected() {

        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
