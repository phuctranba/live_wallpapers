package com.example.photo_wallpapers.MainActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.JsonReader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.photo_wallpapers.Data.DatabaseHelper;
import com.example.photo_wallpapers.Data.EnumTypeWallPaper;
import com.example.photo_wallpapers.Data.Wallpaper;
import com.example.photo_wallpapers.ListOptionsActivity.ListOptionsActivity;
import com.example.photo_wallpapers.R;
import com.example.photo_wallpapers.SettingActivity.SettingActivity;
import com.example.photo_wallpapers.Util.DownloadFile;
import com.example.photo_wallpapers.Util.Utilities;
import com.joooonho.SelectableRoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private CardView cardViewPicture, cardViewVideo;
    private static final int REQUEST_PERMISSIONS = 100;
    DatabaseHelper databaseHelper;
    public static int wallpaperId = 0;
    ProgressDialog dialogLoad;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init các thành phần và gán sự kiện
        init();
        setEventClick();

        checkPermission();

        MainActivity.wallpaperId = WallpaperManager.getInstance(this).getWallpaperId(WallpaperManager.FLAG_SYSTEM);
    }

    private void init() {
        cardViewPicture = findViewById(R.id.cardviewPicture);
        cardViewVideo = findViewById(R.id.cardviewVideo);

        databaseHelper = new DatabaseHelper(this);

        SelectableRoundedImageView imageViewPicture = findViewById(R.id.imagePicture);
        SelectableRoundedImageView imageViewVideo = findViewById(R.id.imageVideo);

        Picasso.get().load(R.drawable.beach_main_1).into(imageViewPicture);
        Picasso.get().load(R.drawable.beach_main_2).into(imageViewVideo);

        dialogLoad = new ProgressDialog(MainActivity.this);
    }

    private void setEventClick() {
        cardViewVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ListOptionsActivity.class);
                intent.putExtra("TYPE", EnumTypeWallPaper.VIDEO);
                startActivity(intent);
            }
        });

        cardViewPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ListOptionsActivity.class);
                intent.putExtra("TYPE", EnumTypeWallPaper.PICTURE);
                startActivity(intent);
            }
        });
    }


    private void callAPI(boolean firstCache) {

        if (!new File(Utilities.ROOT_DIR_STORAGE_PICTURE).exists()) {
            new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath(), "Wallpaper").mkdir();
        }

        if (!new File(Utilities.ROOT_DIR_STORAGE_VIDEO).exists()) {
            new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath(), "Wallpaper").mkdir();
        }

        new fetchAPI().execute();
    }

    private void setCachePicture(List<Wallpaper> wallpaperList) {
        for (Wallpaper wallpaper : wallpaperList) {
            if (!(new File(Utilities.ROOT_DIR_STORAGE_PICTURE_CACHE, wallpaper.getName()).exists())) {
                new DownloadFile().execute(wallpaper.getUri(), Utilities.ROOT_DIR_STORAGE_PICTURE_CACHE, wallpaper.getName());
            }
        }
    }

    private void setCacheVideo(List<Wallpaper> wallpaperList) {

        for (Wallpaper wallpaper : wallpaperList) {
            if (!(new File(Utilities.ROOT_DIR_STORAGE_VIDEO_THUMB_CACHE, wallpaper.getName()).exists())) {
                new DownloadFile().execute(Wallpaper.URI_VIDEO + "/" + wallpaper.getThum(), Utilities.ROOT_DIR_STORAGE_VIDEO_THUMB_CACHE, wallpaper.getThum());
            }
        }

        if(dialogLoad!=null && dialogLoad.isShowing()) dialogLoad.dismiss();

        for (Wallpaper wallpaper : wallpaperList) {
            if (!(new File(Utilities.ROOT_DIR_STORAGE_VIDEO_CACHE, wallpaper.getName()).exists())) {
                new DownloadFile().execute(wallpaper.getUri(), Utilities.ROOT_DIR_STORAGE_VIDEO_CACHE, wallpaper.getName());
            }
        }

    }

    private void readDataAPI(URL url, EnumTypeWallPaper enumTypeWallPaper, String parentURI) throws IOException {
        HttpsURLConnection myConnection = (HttpsURLConnection) url.openConnection();
        if (myConnection.getResponseCode() == 200) {
            InputStreamReader responseBodyReader =
                    new InputStreamReader(myConnection.getInputStream(), "UTF-8");

            JsonReader jsonReader = new JsonReader(responseBodyReader);

            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                if (jsonReader.nextName().equals("datas")) {

                    List<Wallpaper> wallpaperLocal = databaseHelper.getAllByType(enumTypeWallPaper);
                    Map<String, String> mapWallpaper = new HashMap<>();

                    // dùng map, lọc giá trị trùng đưa vào DB
                    for (Wallpaper wallpaper : wallpaperLocal) {
                        mapWallpaper.put(wallpaper.getName(), wallpaper.getUri());
                    }

                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) {
                        String file = "", thum = "";
                        jsonReader.beginObject();

                        while (jsonReader.hasNext()) {
                            String key = jsonReader.nextName();
                            switch (key) {
                                case "name":
                                case "file":
                                    file = jsonReader.nextString();
                                    break;
                                case "thum":
                                    thum = jsonReader.nextString();
                                    break;
                            }
                        }

                        jsonReader.endObject();

                        if (!mapWallpaper.containsKey(file)) {
                            Wallpaper wallpaper = new Wallpaper(
                                    UUID.randomUUID().toString(),
                                    parentURI + "/" + file,
                                    enumTypeWallPaper.equals(EnumTypeWallPaper.PICTURE) ? file : thum,
                                    file,
                                    enumTypeWallPaper,
                                    false
                            );

                            databaseHelper.addWallpaper(wallpaper);
                        }
                    }
                    jsonReader.endArray();

                    break;
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.close();
        }
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
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkPermission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
//            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
//                    Manifest.permission.READ_EXTERNAL_STORAGE))) {
//
//            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
//            }
        } else {
            callAPI(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    if (databaseHelper.getAllWallpaper().size() == 0) {
                        dialogLoad.setTitle("Waiting...");
                        dialogLoad.setMessage("Loading in the first use . Please wait...");
                        dialogLoad.show();
                    }
                    callAPI(true);
                } else {
                    Toast.makeText(MainActivity.this, "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class fetchAPI extends AsyncTask<String, String, String> {
        protected String doInBackground(String... urls) {
            try {
                URL pictureEndpoint = new URL("https://bybit.vn/api/photos.json");
                URL videoEndpoint = new URL("https://bybit.vn/api/livephotos.json");

                readDataAPI(pictureEndpoint, EnumTypeWallPaper.PICTURE, Wallpaper.URI_PICTURE);
                readDataAPI(videoEndpoint, EnumTypeWallPaper.VIDEO, Wallpaper.URI_VIDEO);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String result) {
            setCachePicture(databaseHelper.get16ByType(EnumTypeWallPaper.PICTURE));
            setCacheVideo(databaseHelper.get16ByType(EnumTypeWallPaper.VIDEO));

            setCachePicture(databaseHelper.getAllByType(EnumTypeWallPaper.PICTURE));
            setCacheVideo(databaseHelper.getAllByType(EnumTypeWallPaper.VIDEO));
        }
    }
}

