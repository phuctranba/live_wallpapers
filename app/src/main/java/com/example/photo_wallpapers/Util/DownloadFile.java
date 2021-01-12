package com.example.photo_wallpapers.Util;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadFile extends AsyncTask<String, String, String> {

    private OnTaskCompleted listener;

    public DownloadFile(OnTaskCompleted listener) {
        this.listener = listener;
    }

    public DownloadFile() {
        this.listener = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String nameFile) {

            }
        };
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        return downloadfile(strings[0], strings[1], strings[2]);
    }

    @Override
    protected void onPostExecute(String nameFile) {
        super.onPostExecute(nameFile);
        listener.onTaskCompleted(nameFile);
    }

    private String downloadfile(String vidurl, String rootDir, String nameFile) {

        try {
            File rootFile = new File(rootDir);
            if (!rootFile.exists())
                rootFile.mkdir();
            File pathFile = new File(rootDir, nameFile);
            URL url = new URL(vidurl);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.connect();
            FileOutputStream f = new FileOutputStream(pathFile);
            InputStream in = c.getInputStream();
            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }
            f.close();

            return nameFile;
        } catch (IOException e) {
            Log.d("Error....", e.toString());
        }
        return null;
    }

}