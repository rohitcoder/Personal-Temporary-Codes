package com.infooby.edvoid;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Rohit Kumar on 10/7/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static String ROOT_API = "https://api.edvoid.com/app";
    public static final String DATABASE_NAME = "main.db";
    public static final String T_USERS = "user";
    public static final String T_CLASSES = "classes";
    public static final String T_SETTINGS = "settings";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+T_USERS+" (id INTEGER PRIMARY KEY AUTOINCREMENT,user_id INTEGER,f_name TEXT,l_name TEXT,address TEXT,class_id INTEGER,is_TEACHER INTEGER,is_stuent INTEGER,is_admin INTEGER,admin_type INTEGER,custom_fields INTEGER,update_on INTEGER,is_updated INTEGER)");
        db.execSQL("CREATE TABLE "+T_CLASSES+" (id INTEGER PRIMARY KEY AUTOINCREMENT,class_id INTEGER,name TEXT,upated_on INTEGER,is_updated INTEGER)");
        db.execSQL("CREATE TABLE "+T_SETTINGS+" (login_session TEXT,user_id INTEGER,school_id INTEGER,user_type TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+T_USERS);
        db.execSQL("DROP TABLE IF EXISTS "+T_CLASSES);
        db.execSQL("DROP TABLE IF EXISTS "+T_SETTINGS);
        onCreate(db);
    }

    public boolean insertData(String table_name,String[] key,String[] values){
     SQLiteDatabase db = this.getWritableDatabase();
      ContentValues data = new ContentValues();
        for(int i=0;i<key.length;i++) {
           data.put(key[i],values[i]);
        }
     long result = db.insert(table_name,null,data);
     if(result == -1)
          return  false;
       else
           return true;

    }
    public Cursor getallData(String table_name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+table_name,null);
        return res;
    }
    public String getAuthToken(Context context){
        SharedPreferences pref = context.getSharedPreferences("edvoid_data", 0);
        String auth_token = pref.getString("login_session", null);
       /* SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT login_session FROM "+T_SETTINGS,null);
        if (res.moveToFirst()) {
             auth_token = res.getString(res.getColumnIndex("login_session"));
        }*/
        return auth_token;
    }
    public String getEdvoidSharedPreferences(Context context, String key){
        SharedPreferences pref = context.getSharedPreferences("edvoid_data", 0);
        String value = pref.getString(key, null);
        return value;
    }
    public boolean is_loggedin(String supplied_token){
       SQLiteDatabase db = this.getWritableDatabase();
        String auth_token;
        auth_token = "test";
        Cursor res = db.rawQuery("SELECT login_session FROM "+T_SETTINGS,null);
        if (res.moveToFirst()) {
            auth_token = res.getString(res.getColumnIndex("login_session"));
        }
        if(auth_token.equals(supplied_token)){
            return true;
        }else if(auth_token.equals("test")){
            return false;
        }else{
            return false;
        }
    }
}
