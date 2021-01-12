package com.example.photo_wallpapers.ListOptionsActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photo_wallpapers.Data.DatabaseHelper;
import com.example.photo_wallpapers.Data.EnumTypeWallPaper;
import com.example.photo_wallpapers.Data.Wallpaper;
import com.example.photo_wallpapers.GalleryActivity.GalleryActivity;
import com.example.photo_wallpapers.PreviewPictureActivity.PreviewPictureActivity;
import com.example.photo_wallpapers.PreviewVideoActivity.PreviewVideoActivity;
import com.example.photo_wallpapers.R;
import com.example.photo_wallpapers.SettingActivity.SettingActivity;

import java.util.ArrayList;
import java.util.List;

public class ListOptionsActivity extends AppCompatActivity implements ItemAdapter.ItemWallpaperListener {

    DatabaseHelper databaseHelper;
    private List<Wallpaper> wallpaperList;
    private RecyclerView recyclerViewWallpaper;
    private ItemAdapter itemAdapter;
    private EnumTypeWallPaper TypeDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_options);

        init();

        setTitle(TypeDisplay.equals(EnumTypeWallPaper.PICTURE)?"PICTURE":"VIDEO");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        wallpaperList.clear();
        wallpaperList.add(null);
        wallpaperList.addAll(databaseHelper.getAllByType(TypeDisplay));
        itemAdapter.notifyDataSetChanged();
    }


    private void init() {
        databaseHelper = new DatabaseHelper(this);
        TypeDisplay = (EnumTypeWallPaper) getIntent().getSerializableExtra("TYPE");

        recyclerViewWallpaper = findViewById(R.id.recyclerViewWallpaper);

        wallpaperList = new ArrayList<>();
        wallpaperList.add(null); //Phần tử đầu rỗng
        wallpaperList.addAll(databaseHelper.getAllByType(TypeDisplay));
        itemAdapter = new ItemAdapter(this, wallpaperList, this);

        recyclerViewWallpaper.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewWallpaper.setAdapter(itemAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_setting) {
            startActivity(new Intent(ListOptionsActivity.this, SettingActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void itemWallpaperClick(int position) {
        if (wallpaperList.get(position) == null) {
            Intent intent = new Intent(ListOptionsActivity.this, GalleryActivity.class);
            intent.putExtra("TYPE", TypeDisplay.equals(EnumTypeWallPaper.VIDEO) ? EnumTypeWallPaper.STORAGE_VIDEO : EnumTypeWallPaper.STORAGE_PICTURE);
            startActivity(intent);
        } else {
            Intent intent = new Intent(ListOptionsActivity.this, TypeDisplay.equals(EnumTypeWallPaper.VIDEO) ? PreviewVideoActivity.class : PreviewPictureActivity.class);
            intent.putExtra("POSITION", position - 1);
            intent.putExtra("STORAGE", false);
            startActivity(intent);
        }
    }

}