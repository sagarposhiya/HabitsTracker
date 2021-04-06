package com.example.habitstracker_verion.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.habitstracker_verion.App;
import com.example.habitstracker_verion.LoginScreenActivity;
import com.example.habitstracker_verion.R;
import com.example.habitstracker_verion.utils.AppUtils;
import com.example.habitstracker_verion.utils.Constants;
import com.example.habitstracker_verion.utils.RealmManager;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btnChooseColor)
    Button btnChooseColor;
    @BindView(R.id.txtSelectedColor)
    TextView txtSelectedColor;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txtUpdate)
    TextView txtUpdate;
    @BindView(R.id.txtUserId)
    TextView txtUserId;
    @BindView(R.id.txtTitleLog)
    TextView txtTitleLog;
    @BindView(R.id.btnLogout)
    Button btnLogout;
    @BindView(R.id.btnClear)
    Button btnClear;
    String color;

    private GoogleSignInClient mSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        setAppTheme();
        getInit();
    }

    private void setAppTheme() {
        color = AppUtils.getStringPreference(this, Constants.themeColor);
        toolbar.setBackgroundColor(Color.parseColor(color));
        btnChooseColor.setBackgroundColor(Color.parseColor(color));
        btnLogout.setBackgroundColor(Color.parseColor(color));
        txtSelectedColor.setBackgroundColor(Color.parseColor(color));
    }

    private void getInit() {

        RealmManager.closeInstance();
        RealmManager.closeInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mSignInClient = GoogleSignIn.getClient(this, gso);

        color = AppUtils.getStringPreference(this, Constants.themeColor);
        btnChooseColor.setOnClickListener(this);
        //txtSelectedColor.setBackgroundColor(Color.parseColor(color));

        long update = AppUtils.getLongPreference(SettingActivity.this, Constants.DB_UPDATED);
        if (update != 00) {
            txtUpdate.setText(AppUtils.convertTime(update));
        }

        String userId = AppUtils.getStringPreference(this, Constants.UserId);
        txtUserId.setText(userId);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle(getString(R.string.txtMsgDeleteEntry));
                builder.setMessage("Are you sure you want to reset the database?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RealmManager.closeInstance();
                        RealmManager.closeInstance();
                        RealmManager.closeInstance();
                        RealmManager.closeInstance();
                        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                                .name("habbitstracker.realm")
                                .schemaVersion(0)
                                .allowWritesOnUiThread(true)
                                .build();
                        Realm.deleteRealm(realmConfig);

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Tracks").child(userId);
                        ref.removeValue();
                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Reminders").child(userId);
                        ref2.removeValue();

                        AppUtils.setBooleanPreference(SettingActivity.this, Constants.isUnitAdded, false);

                        Intent clearIntent = new Intent(SettingActivity.this, DashboardActivity.class);
                        clearIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        // Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        //  clearIntent.putExtra("exit", true);
                        startActivity(clearIntent);
                        finish();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnChooseColor:
                chooseColor();
                break;
        }
    }

    private void chooseColor() {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                // .initialColor(currentBackgroundColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                //.noSliders()
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
                        AppUtils.setStringPreference(SettingActivity.this, Constants.themeColor, hexColor);
                        toolbar.setBackgroundColor(Color.parseColor(hexColor));
                        btnChooseColor.setBackgroundColor(Color.parseColor(hexColor));
                        txtSelectedColor.setBackgroundColor(Color.parseColor(hexColor));
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

    private void signOut() {

        mSignInClient.signOut();

        RealmManager.closeInstance();
        RealmManager.closeInstance();
     //   RealmManager.closeInstance();
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("habbitstracker.realm")
                .schemaVersion(0)
                .allowWritesOnUiThread(true)
                .build();
        Realm.deleteRealm(realmConfig);

        AppUtils.setBooleanPreference(SettingActivity.this, Constants.isUnitAdded, false);
        AppUtils.setBooleanPreference(SettingActivity.this, Constants.isLogin, false);
        Intent intent = new Intent(SettingActivity.this, LoginScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}