package com.example.photo_wallpapers.SettingActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.photo_wallpapers.R;
import com.example.photo_wallpapers.Util.VideoLiveWallpaper;

import java.io.IOException;

public class SettingActivity extends AppCompatActivity {

    SharedPreferences sharedPref;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        switchSound = findViewById(R.id.switchSound);
        switchSound.setChecked(sharedPref.getBoolean("SOUND", true));

        switchSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean value) {

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("SOUND", value);
                editor.apply();
                if (value) {

                    VideoLiveWallpaper.unmuteMusic(SettingActivity.this);
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Runtime.getRuntime().exec("touch " + getFilesDir().toPath() + "/unmute");
                        } else {
                            Runtime.getRuntime().exec("touch /data/data/com.example.photo_wallpapers/files/unmute");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                    VideoLiveWallpaper.muteMusic(SettingActivity.this);
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Runtime.getRuntime().exec("rm " + getFilesDir().toPath() + "/unmute");
                        } else {
                            Runtime.getRuntime().exec("rm /data/data/com.example.photo_wallpapers/files/unmute");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}