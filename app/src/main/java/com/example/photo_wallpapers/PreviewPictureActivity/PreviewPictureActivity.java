package com.example.photo_wallpapers.PreviewPictureActivity;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.photo_wallpapers.Data.DatabaseHelper;
import com.example.photo_wallpapers.Data.EnumTypeWallPaper;
import com.example.photo_wallpapers.Data.Wallpaper;
import com.example.photo_wallpapers.R;

import java.util.List;

public class PreviewPictureActivity extends AppCompatActivity {

    ViewPager2 pictureViewPager;
    List<Wallpaper> wallpaperList;
    DatabaseHelper databaseHelper;
    PreviewPictureAdapter previewPictureAdapter;
    ImageView btnBack;
    boolean isStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_picture);

        init();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    void init() {
        databaseHelper = new DatabaseHelper(this);
        btnBack = findViewById(R.id.btnBack);
        isStorage = getIntent().getBooleanExtra("STORAGE", false);

        if (isStorage) {
            wallpaperList = (List<Wallpaper>) getIntent().getSerializableExtra("DATA");
        } else {
            wallpaperList = databaseHelper.getAllByType(EnumTypeWallPaper.PICTURE);
        }
        pictureViewPager = findViewById(R.id.viewPager);

        previewPictureAdapter = new PreviewPictureAdapter(this, wallpaperList, isStorage);
        pictureViewPager.setAdapter(previewPictureAdapter);

        pictureViewPager.setCurrentItem(getIntent().getIntExtra("POSITION", 0), false);
    }

    public void displaySuccessDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.success_dialog, null);
        final LinearLayout btnOK = (LinearLayout) alertLayout.findViewById(R.id.btnOK);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setCancelable(false);

        AlertDialog dialog = alert.create();

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

}