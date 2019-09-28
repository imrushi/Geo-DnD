package com.example.geodnd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "user_info";
    private static final String COL1 = "ID";
    private static final String TaskName = "TaskName";
    private static final String Date = "Date";
    private static final String pState = "pState";
    private static final String Radius = "Radius";
    private static final String Latlong = "Latlong";

    public DatabaseHelper(Context context) {
        super(context , TABLE_NAME , null ,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +  TaskName + " TEXT, " + Date + " TEXT, " + pState + " TEXT, " + Radius + " INTEGER, " + Latlong + " TEXT);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String item,String dd, String state,int rad,String loc)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskName ,item);
        contentValues.put(Date ,dd);
        contentValues.put(pState ,state);
        contentValues.put(Radius ,rad);
        contentValues.put(Latlong ,loc);

        Log.d(TAG, "addData: Adding " + item + " to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);

        //if data as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        }
        else {
            return true;
        }
    }

    //Return all data from database
    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    //Return only ID that matches the name passed in
    public Cursor getItemID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
       // String query = "SELECT " + COL1 +"," + TaskName + "," + Date +"," + pState + "," + Radius +"," + Latlong + " FROM " + TABLE_NAME + " WHERE " + TaskName + " = '" + name + "'";
       // String query = "SELECT " + COL1 +" FROM " + TABLE_NAME + " WHERE " + TaskName + " = '" + name + "'";
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + TaskName + " = '" + name + "'";
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public void updateName(String item,int id,String oldName,String dailydate,String state,int rad,String loc){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET "+ TaskName + " = '" + item + "'," + Date + " = '" + dailydate + "',"+ pState + " = '" + state +"'," + Radius +" = '" + rad +"'," + Latlong + " = '" + loc +"' WHERE " + COL1 + " = '" + id + "'" + " AND " + TaskName + " = '"+ oldName + "'";
       // String query = "UPDATE " + TABLE_NAME + " SET "+ TaskName + " = '" + item + "' WHERE " + COL1 + " = '" + id + "'" + " AND " + TaskName + " = '"+ oldName + "'";
        Log.d(TAG, "updateName: query:" + query);
        Log.d(TAG, "updateName: Setting name to " + item);
        db.execSQL(query);
    }

    public void deleteName(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL1 + " = '" + id + "'" + " AND "+ TaskName + " = '" + name + "'";
        Log.d(TAG, "deleteName: query: "+ query);
        Log.d(TAG, "deleteName: Deleting "+ name + " from database.");
        db.execSQL(query);
    }

}
