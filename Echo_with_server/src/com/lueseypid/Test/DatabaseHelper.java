package com.lueseypid.Test;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor; 
import android.content.Context;



public class DatabaseHelper extends SQLiteOpenHelper {
    String nameTable="CONVERSATION";
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE CONVERSATION (_id INTEGER PRIMARY KEY AUTOINCREMENT, request TEXT, date TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String date, String request) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO CONVERSATION VALUES(null, '" + date + "', '" + request + "');");
        db.close();
    }

    public String getAll(){
        SQLiteDatabase db = getWritableDatabase();	
        String result = "";
        Cursor cursor = db.rawQuery("SELECT * FROM CONVERSATION", null);
            result += cursor.getString(0)
                    + "\n"
                    + cursor.getString(1);
        return result;
    }


}