package com.example.photo_wallpapers.GalleryActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photo_wallpapers.Data.EnumTypeWallPaper;
import com.example.photo_wallpapers.Data.Wallpaper;
import com.example.photo_wallpapers.ListOptionsActivity.ItemAdapter;
import com.example.photo_wallpapers.PreviewPictureActivity.PreviewPictureActivity;
import com.example.photo_wallpapers.PreviewVideoActivity.PreviewVideoActivity;
import com.example.photo_wallpapers.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity implements ItemAdapter.ItemWallpaperListener {

    private List<Wallpaper> galleryPathList;
    private static final int REQUEST_PERMISSIONS = 100;
    private RecyclerView recyclerViewWallpaper;
    private ItemAdapter itemAdapter;
    private EnumTypeWallPaper typeWallPaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        checkPermission();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void init() {
        galleryPathList = new ArrayList<>();
        typeWallPaper = (EnumTypeWallPaper) getIntent().getSerializableExtra("TYPE");

        recyclerViewWallpaper = findViewById(R.id.recyclerViewWallpaper);
        recyclerViewWallpaper.setLayoutManager(new GridLayoutManager(this, 2));
        itemAdapter = new ItemAdapter(this, galleryPathList, this);
        recyclerViewWallpaper.setAdapter(itemAdapter);
    }

    private void checkPermission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(GalleryActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(GalleryActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(GalleryActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        } else {
            if (typeWallPaper.equals(EnumTypeWallPaper.STORAGE_PICTURE)) {
                getAllShownImagesPath();
            } else {
                getAllShownVideoPath();
            }
        }
    }

    @SuppressLint("Recycle")
    private void getAllShownImagesPath() {
        Uri uri;
        Cursor cursor;
        int column_index_data;
        galleryPathList.clear();
        List<Wallpaper> listOfAllImages = new ArrayList<Wallpaper>();
        String absolutePathOfImage;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = this.getContentResolver().query(uri, projection, null,
                null, orderBy + " DESC");

        assert cursor != null;
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Wallpaper wallpaper = new Wallpaper();
            wallpaper.setUri(absolutePathOfImage);
            wallpaper.setThum(absolutePathOfImage);
            wallpaper.setType(EnumTypeWallPaper.STORAGE_PICTURE);
            listOfAllImages.add(wallpaper);
        }
        galleryPathList.addAll(listOfAllImages);
        itemAdapter.notifyDataSetChanged();
    }

    @SuppressLint("Recycle")
    private void getAllShownVideoPath() {
        Uri uri;
        Cursor cursor;
        int column_index_data, thum;
        galleryPathList.clear();
        List<Wallpaper> listOfAllVideos = new ArrayList<Wallpaper>();
        String absolutePathOfVideo;
        uri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media._ID, MediaStore.Video.Thumbnails.DATA};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);
        while (cursor.moveToNext()) {
            absolutePathOfVideo = cursor.getString(column_index_data);
            Wallpaper wallpaper = new Wallpaper();
            wallpaper.setUri(absolutePathOfVideo);
            wallpaper.setThum(cursor.getString(thum));
            wallpaper.setType(EnumTypeWallPaper.STORAGE_VIDEO);
            listOfAllVideos.add(wallpaper);
        }
        galleryPathList.addAll(listOfAllVideos);
        itemAdapter.notifyDataSetChanged();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    if (typeWallPaper.equals(EnumTypeWallPaper.STORAGE_PICTURE)) {
                        getAllShownImagesPath();
                    } else {
                        getAllShownVideoPath();
                    }
                } else {
                    Toast.makeText(GalleryActivity.this, "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void itemWallpaperClick(int position) {
        Intent intent = new Intent(GalleryActivity.this, typeWallPaper.equals(EnumTypeWallPaper.STORAGE_VIDEO) ? PreviewVideoActivity.class : PreviewPictureActivity.class);
        intent.putExtra("POSITION", position);
        intent.putExtra("STORAGE", true);
        intent.putExtra("DATA", (Serializable) galleryPathList);
        startActivity(intent);
    }
}