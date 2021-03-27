package com.example.habitstracker_verion.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.habitstracker_verion.R;
import com.example.habitstracker_verion.utils.AppUtils;
import com.example.habitstracker_verion.utils.Constants;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btnChooseColor)
    Button btnChooseColor;
    @BindView(R.id.txtSelectedColor)
    TextView txtSelectedColor;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    String color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        setAppTheme();
        getInit();
    }

    private void setAppTheme() {
        color = AppUtils.getStringPreference(this,Constants.themeColor);
        toolbar.setBackgroundColor(Color.parseColor(color));
        btnChooseColor.setBackgroundColor(Color.parseColor(color));
        txtSelectedColor.setBackgroundColor(Color.parseColor(color));
    }

    private void getInit() {

        color  = AppUtils.getStringPreference(this, Constants.themeColor);
        btnChooseColor.setOnClickListener(this);
        //txtSelectedColor.setBackgroundColor(Color.parseColor(color));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

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
                       AppUtils.setStringPreference(SettingActivity.this,Constants.themeColor,hexColor);
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
}