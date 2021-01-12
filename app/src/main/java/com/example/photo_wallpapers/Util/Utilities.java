package com.example.photo_wallpapers.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Utilities {

    public static final String ROOT_DIR_STORAGE_PICTURE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/Wallpaper";

    @SuppressLint("SdCardPath")
    public static final String ROOT_DIR_STORAGE_PICTURE_CACHE = "/data/data/com.example.photo_wallpapers/cache/picture_cache";

    public static final String ROOT_DIR_STORAGE_VIDEO = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath()+"/Wallpaper";

    @SuppressLint("SdCardPath")
    public static final String ROOT_DIR_STORAGE_VIDEO_CACHE = "/data/data/com.example.photo_wallpapers/cache/video_cache";;

    @SuppressLint("SdCardPath")
    public static final String ROOT_DIR_STORAGE_VIDEO_THUMB_CACHE = "/data/data/com.example.photo_wallpapers/cache/video_thumb_cache";;

    public static void shareFile(String filePath, Context context) {
        File fileWithinMyDir = new File(filePath);

        Uri uri = FileProvider.getUriForFile(context, "com.example.photo_wallpapers.fileprovider", fileWithinMyDir);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
//        shareIntent.putExtra(Intent.EXTRA_TEXT, "Gửi từ ứng dụng Live wallpapers!!!");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType("*/*");
        context.startActivity(Intent.createChooser(shareIntent, "Share..."));
    }

    public static void copyFile(File fromFile, File toFile) {

        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        FileChannel fileChannelInput = null;
        FileChannel fileChannelOutput = null;
        try {
            fileInputStream = new FileInputStream(fromFile);
            fileOutputStream = new FileOutputStream(toFile);
            fileChannelInput = fileInputStream.getChannel();
            fileChannelOutput = fileOutputStream.getChannel();
            fileChannelInput.transferTo(0, fileChannelInput.size(), fileChannelOutput);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
                if (fileChannelInput != null)
                    fileChannelInput.close();
                if (fileOutputStream != null)
                    fileOutputStream.close();
                if (fileChannelOutput != null)
                    fileChannelOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
