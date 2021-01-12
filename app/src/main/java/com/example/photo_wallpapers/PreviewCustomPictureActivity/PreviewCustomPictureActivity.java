package com.example.photo_wallpapers.PreviewCustomPictureActivity;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.photo_wallpapers.R;
import com.example.photo_wallpapers.Util.LiveWallpaperService;

public class PreviewCustomPictureActivity extends AppCompatActivity {

    private SharedPreferences.Editor editor;
    private LinearLayout btnApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_custom_picture);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        if (Build.VERSION.SDK_INT >= 21) {
            LinearLayout viewSavePower = findViewById(R.id.viewSavePower);
            @SuppressLint("UseSwitchCompatOrMaterialCode")
            Switch switchSavePower = findViewById(R.id.switchSavePower);
            viewSavePower.setVisibility(View.VISIBLE);
            switchSavePower.setChecked(preferences.getBoolean("POWER-SAVER", true));
            switchSavePower.setOnCheckedChangeListener(new CompoundButton
                    .OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    editor.putBoolean("POWER-SAVER", isChecked);
                    editor.apply();
                }
            });
        }

        btnApply = findViewById(R.id.btnApply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        SeekBar seekBarRange = (SeekBar) findViewById(R.id.seekBarRange);
        seekBarRange.setProgress(preferences.getInt("RANGE", 10));
        seekBarRange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    editor.putInt("RANGE", progress);
                    editor.apply();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        SeekBar seekBarDelay = (SeekBar) findViewById(R.id.seekBarDelay);
        seekBarDelay.setProgress(preferences.getInt("DELAY", 10));
        seekBarDelay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    editor.putInt("DELAY", progress);
                    editor.apply();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switchScroll =  findViewById(R.id.switchScroll);
        switchScroll.setChecked(preferences.getBoolean("SCROLL", true));
        switchScroll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("SCROLL", isChecked);
                editor.apply();
            }
        });

        startActivity(new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                .putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new
                        ComponentName(this, LiveWallpaperService.class))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

    }

}