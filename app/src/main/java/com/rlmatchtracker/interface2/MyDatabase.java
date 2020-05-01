package com.rlmatchtracker.interface2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class MyDatabase {
    private static final String DEBUG_TAG = "DEBUG_TAG";
    public SQLiteDatabase db;
    private Context context;
    private final MyHelper helper;

    public MyDatabase(Context c){
        context = c;
        helper = new MyHelper(context);
    }
    public void resetTable(){
        db = helper.getWritableDatabase();
        helper.resetTable(db);
    }
    public void compressTable(){
        db = helper.getWritableDatabase();
        helper.compressTable(db);
    }
    public long insertData (String Image, Integer wl, Integer Mode, Integer Team, Integer Opp, Integer Points, Integer Goals, Integer Assists, Integer Saves, Integer Shots, String Time)
    {
        db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.IMAGE, Image);
        contentValues.put(Constants.WL, wl);
        contentValues.put(Constants.MODE, Mode);
        contentValues.put(Constants.TEAM, Team);
        contentValues.put(Constants.OPP, Opp);
        contentValues.put(Constants.POINTS, Points);
        contentValues.put(Constants.GOALS, Goals);
        contentValues.put(Constants.ASSISTS, Assists);
        contentValues.put(Constants.SAVES, Saves);
        contentValues.put(Constants.SHOTS, Shots);
        contentValues.put(Constants.TIME, Time);
        Log.d(DEBUG_TAG,"time: "+Time);
        long id = db.insert(Constants.TABLE_NAME, null, contentValues);
        return id;
    }

    public long updateData (String id, String Image, Integer wl, Integer Mode, Integer Team, Integer Opp, Integer Points, Integer Goals, Integer Assists, Integer Saves, Integer Shots, String Time)
    {
        db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.IMAGE, Image);
        contentValues.put(Constants.WL, wl);
        contentValues.put(Constants.MODE, Mode);
        contentValues.put(Constants.TEAM, Team);
        contentValues.put(Constants.OPP, Opp);
        contentValues.put(Constants.POINTS, Points);
        contentValues.put(Constants.GOALS, Goals);
        contentValues.put(Constants.ASSISTS, Assists);
        contentValues.put(Constants.SAVES, Saves);
        contentValues.put(Constants.SHOTS, Shots);
        contentValues.put(Constants.TIME, Time);
        long num = db.update(Constants.TABLE_NAME, contentValues, Constants.UID+"=?", new String[]{id});
        return num;
    }

    public long deleteRow (String id)
    {
        db = helper.getWritableDatabase();
        long num = db.delete(Constants.TABLE_NAME, Constants.UID+"=?", new String[]{id});
        db.close();
        return num;
    }

    public ArrayList<String> getData(int var,String order) {

        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constants.UID, Constants.IMAGE, Constants.WL, Constants.MODE, Constants.TEAM, Constants.OPP, Constants.POINTS, Constants.GOALS, Constants.ASSISTS, Constants.SAVES, Constants.SHOTS, Constants.TIME};
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, null, null, null, null, columns[var] + order);
        ArrayList<String> rows = new ArrayList<String>();

        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(Constants.UID));
            String Image = cursor.getString(cursor.getColumnIndex(Constants.IMAGE));
            Integer WL = cursor.getInt(cursor.getColumnIndex(Constants.WL));
            Integer Mode = cursor.getInt(cursor.getColumnIndex(Constants.MODE));
            Integer Team = cursor.getInt(cursor.getColumnIndex(Constants.TEAM));
            Integer Opp = cursor.getInt(cursor.getColumnIndex(Constants.OPP));
            Integer Points = cursor.getInt(cursor.getColumnIndex(Constants.POINTS));
            Integer Goals = cursor.getInt(cursor.getColumnIndex(Constants.GOALS));
            Integer Assists = cursor.getInt(cursor.getColumnIndex(Constants.ASSISTS));
            Integer Saves = cursor.getInt(cursor.getColumnIndex(Constants.SAVES));
            Integer Shots = cursor.getInt(cursor.getColumnIndex(Constants.SHOTS));
            String Time = cursor.getString(cursor.getColumnIndex(Constants.TIME));
            rows.add(Image + " " + WL + " " + Mode + " " + Team + " " + Opp + " " + Points + " " + Goals + " " + Assists + " " + Shots + " " + Saves + " " + Time + " " + id + "\n");
        }
        return rows;
    }

    public Cursor getCursor() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constants.UID, Constants.IMAGE, Constants.WL, Constants.MODE, Constants.TEAM, Constants.OPP, Constants.POINTS, Constants.GOALS, Constants.ASSISTS, Constants.SAVES, Constants.SHOTS, Constants.TIME};
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }

}
