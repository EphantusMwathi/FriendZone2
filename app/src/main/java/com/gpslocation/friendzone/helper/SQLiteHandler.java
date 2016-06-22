package com.gpslocation.friendzone.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by mwathi on 2/11/2016.
 */
public class SQLiteHandler extends SQLiteOpenHelper {
    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "friendzone.db";

    // Login table name
    private static final String TABLE_USER = "user";
    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_FNAME = "fname";
    private static final String KEY_SNAME = "sname";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";



    private static final String TABLE_FRIENDS= "friend";
    private static final String ID="id";
    private static final String FNAME="name1";
    private static final String SNAME="name2";
    private static final String EMAIL="email";
    private static final String UID="uid";
   // private static final String ID="ID";
      private static String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_FNAME + " TEXT,"
            +KEY_SNAME + " TEXT,"
            + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
            + KEY_CREATED_AT + " TEXT" + ")";


   private  static String CREATE_FRIEND_TABLE="CREATE TABLE " + TABLE_FRIENDS + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + FNAME + " TEXT,"
            +SNAME + " TEXT,"
            + EMAIL + " TEXT UNIQUE,"+ UID + " TEXT UNIQUE" + ")";


    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_FRIEND_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String fname,String sname, String email, String uid, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FNAME, fname);
        values.put(KEY_SNAME, sname);// Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // Email
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }


    public void addFriend(String fname,String sname, String email, String uid) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FNAME, fname);
        values.put(SNAME, sname);// Name
        values.put(EMAIL, email); // Email
        values.put(UID, uid); // Email
                    // Created At

        // Inserting Row
        long id = db.insert(TABLE_FRIENDS, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }






    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name1", cursor.getString(1));
            user.put("name2", cursor.getString(2));
            user.put("email", cursor.getString(3));
            user.put("uid", cursor.getString(4));
            user.put("created_at", cursor.getString(5));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }


    public HashMap<String, String> getFriendDetails(String uid) {
        HashMap<String, String> user = new HashMap<String, String>();
        //String selectQuery = "SELECT  * FROM " + TABLE_FRIENDS+"WHERE UID='uid'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(this.TABLE_FRIENDS, null, this.UID + "='" + uid + "'", null, null, null, null);
        // Move to first row

        while(cursor.moveToNext()) {
            user.put("name1", cursor.getString(1));
            user.put("name2", cursor.getString(2));
            user.put("email", cursor.getString(3));
            user.put("uid", cursor.getString(4));

        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    public void deleteFriend() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_FRIENDS, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }
}
