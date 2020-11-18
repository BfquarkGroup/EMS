package com.oufar.ems.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Favorite extends SQLiteOpenHelper {


    private static final String DB_NAME = "Favorite.db";
    private static final String DB_TABLE = "Favorite_Table";

    // Columns
    private static final String ID = "ID";
    private static final String STORE_ID = "STORE_ID";
    private static final String PHONE = "PHONE";
    private static final String NAME = "NAME";
    private static final String ADDRESS = "ADDRESS";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String IMAGE_URL = "IMAGE_URL";
    private static final String STATUS = "STATUS";
    private static final String EMAIL = "EMAIL";

    // Command
    private static final String CREATE_TABLE = "CREATE TABLE "+ DB_TABLE+" ("+
            ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+  // 0
            STORE_ID+ " TEXT, "+  // 1
            PHONE+ " TEXT, "+  // 2
            NAME+ " TEXT, "+  // 3
            ADDRESS+ " TEXT, "+  // 4
            DESCRIPTION+ " TEXT, "+  // 5
            IMAGE_URL+ " TEXT, "+  // 6
            STATUS+ " TEXT, "+  // 7
            EMAIL+ " TEXT "+")";  // 8



    public Favorite(Context context) {
        super(context, DB_NAME, null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+ DB_TABLE);

        onCreate(db);
    }

    // A method to insert data
    public boolean insertData(String storeId, String phone,
                              String name, String address,
                              String description, String imageURL,
                              String status, String email){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STORE_ID, storeId);
        contentValues.put(PHONE, phone);
        contentValues.put(NAME, name);
        contentValues.put(ADDRESS, address);
        contentValues.put(DESCRIPTION, description);
        contentValues.put(IMAGE_URL, imageURL);
        contentValues.put(STATUS, status);
        contentValues.put(EMAIL, email);

        long result = db.insert(DB_TABLE, null, contentValues);

        if (result == -1){

            return false;
        }else {

            return true;
        }

        // return result != -1; // if result = -1 data doesn't inserted
    }

    // A method to delete data
    public void deleteData(String id, String storeId, String phone,
                           String name, String address,
                           String description, String imageURL,
                           String status, String email){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_TABLE,ID + " = ?",new String[]{id});
        db.delete(DB_TABLE,STORE_ID + " = ?",new String[]{storeId});
        db.delete(DB_TABLE,PHONE + " = ?",new String[]{phone});
        db.delete(DB_TABLE,NAME + " = ?",new String[]{name});
        db.delete(DB_TABLE,ADDRESS + " = ?",new String[]{address});
        db.delete(DB_TABLE,DESCRIPTION + " = ?",new String[]{description});
        db.delete(DB_TABLE,IMAGE_URL + " = ?",new String[]{imageURL});
        db.delete(DB_TABLE,STATUS + " = ?",new String[]{status});
        db.delete(DB_TABLE,EMAIL + " = ?",new String[]{email});
        db.close();
    }

    // A method to view data
    public Cursor viewData(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "Select * from "+ DB_TABLE;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        return cursor;
    }
}
