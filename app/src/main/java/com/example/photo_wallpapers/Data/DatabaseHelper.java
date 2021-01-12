package com.example.photo_wallpapers.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "SQLite";

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "WallpaperDatabase";

    private static final String TABLE_WALLPAPER = "Wallpaper";

    private static final String COLUMN_WALLPAPER_ID ="Wallpaper_Id";
    private static final String COLUMN_WALLPAPER_URI ="Wallpaper_Uri";
    private static final String COLUMN_WALLPAPER_THUM ="Wallpaper_Thum";
    private static final String COLUMN_WALLPAPER_NAME = "Wallpaper_Name";
    private static final String COLUMN_WALLPAPER_TYPE = "Wallpaper_Type";
    private static final String COLUMN_WALLPAPER_LIKE = "Wallpaper_Like";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String scriptWallpaper = "CREATE TABLE " + TABLE_WALLPAPER + "("
                + COLUMN_WALLPAPER_ID + " TEXT PRIMARY KEY,"
                + COLUMN_WALLPAPER_URI + " TEXT,"
                + COLUMN_WALLPAPER_THUM + " TEXT,"
                + COLUMN_WALLPAPER_NAME + " TEXT,"
                + COLUMN_WALLPAPER_TYPE + " TEXT,"
                + COLUMN_WALLPAPER_LIKE + " INTEGER"
                + ")";

        // Execute Script.
        sqLiteDatabase.execSQL(scriptWallpaper);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_WALLPAPER);
        onCreate(sqLiteDatabase);
    }

    public List<Wallpaper> getAllWallpaper() {

        List<Wallpaper> wallpaperList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_WALLPAPER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Wallpaper wallpaper = new Wallpaper();
                wallpaper.setId(cursor.getString(0));
                wallpaper.setUri(cursor.getString(1));
                wallpaper.setThum(cursor.getString(2));
                wallpaper.setName(cursor.getString(3));
                wallpaper.setType(EnumTypeWallPaper.valueOf(cursor.getString(4)));
                wallpaper.setLike(cursor.getInt(5)==1);
                // Adding note to list
                wallpaperList.add(wallpaper);
            } while (cursor.moveToNext());
        }

        return wallpaperList;
    }

    public List<Wallpaper> getAllByType(EnumTypeWallPaper enumTypeWallPaper) {

        List<Wallpaper> wallpaperList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_WALLPAPER +" WHERE "+COLUMN_WALLPAPER_TYPE+" = '"+enumTypeWallPaper.name()+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Wallpaper wallpaper = new Wallpaper();
                wallpaper.setId(cursor.getString(0));
                wallpaper.setUri(cursor.getString(1));
                wallpaper.setThum(cursor.getString(2));
                wallpaper.setName(cursor.getString(3));
                wallpaper.setType(EnumTypeWallPaper.valueOf(cursor.getString(4)));
                wallpaper.setLike(cursor.getInt(5)==1);
                // Adding note to list
                wallpaperList.add(wallpaper);
            } while (cursor.moveToNext());
        }

        return wallpaperList;
    }

    public List<Wallpaper> get16ByType(EnumTypeWallPaper enumTypeWallPaper) {

        List<Wallpaper> wallpaperList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_WALLPAPER +" WHERE "+COLUMN_WALLPAPER_TYPE+" = '"+enumTypeWallPaper.name()+"' LIMIT 16";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Wallpaper wallpaper = new Wallpaper();
                wallpaper.setId(cursor.getString(0));
                wallpaper.setUri(cursor.getString(1));
                wallpaper.setThum(cursor.getString(2));
                wallpaper.setName(cursor.getString(3));
                wallpaper.setType(EnumTypeWallPaper.valueOf(cursor.getString(4)));
                wallpaper.setLike(cursor.getInt(5)==1);
                // Adding note to list
                wallpaperList.add(wallpaper);
            } while (cursor.moveToNext());
        }

        return wallpaperList;
    }

    public void addWallpaper(Wallpaper wallpaper) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_WALLPAPER_ID, wallpaper.getId());
        values.put(COLUMN_WALLPAPER_URI, wallpaper.getUri());
        values.put(COLUMN_WALLPAPER_THUM, wallpaper.getThum());
        values.put(COLUMN_WALLPAPER_NAME, wallpaper.getName());
        values.put(COLUMN_WALLPAPER_TYPE, wallpaper.getType().name());
        values.put(COLUMN_WALLPAPER_LIKE, wallpaper.isLike()?1:0);

        sqLiteDatabase.insert(TABLE_WALLPAPER, null, values);

        sqLiteDatabase.close();
    }

    public int updateWallpaper(Wallpaper wallpaper) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_WALLPAPER_URI, wallpaper.getUri());
        values.put(COLUMN_WALLPAPER_THUM, wallpaper.getThum());
        values.put(COLUMN_WALLPAPER_NAME, wallpaper.getName());
        values.put(COLUMN_WALLPAPER_TYPE, wallpaper.getType().name());
        values.put(COLUMN_WALLPAPER_LIKE, wallpaper.isLike()?1:0);

        return sqLiteDatabase.update(TABLE_WALLPAPER, values, COLUMN_WALLPAPER_ID + " = ?",
                new String[]{String.valueOf(wallpaper.getId())});
    }
}
