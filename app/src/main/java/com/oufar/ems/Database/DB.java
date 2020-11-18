package com.oufar.ems.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {


    private static final String DB_NAME = "Client.db";
    private static final String DB_TABLE = "Client_Table";

    // Columns
    private static final String CLIENT_ID = "CLIENT_ID";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String ID = "ID";
    private static final String IMAGE_URL = "IMAGE_URL";
    private static final String PLATE = "PLATE";
    private static final String PRICE = "PRICE";
    private static final String NUMBER = "NUMBER";
    private static final String STORE_ID = "STORE_ID";
    private static final String STORE_EMAIL = "STORE_EMAIL";

    // Command
    private static final String CREATE_TABLE = "CREATE TABLE "+ DB_TABLE+" ("+
            ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+  // 0
            CLIENT_ID+ " TEXT, "+  // 1
            STORE_ID+ " TEXT, "+  // 2
            PLATE+ " TEXT, "+  // 3
            IMAGE_URL+ " TEXT, "+  // 4
            DESCRIPTION+ " TEXT, "+  // 5
            NUMBER+ " TEXT, "+  // 6
            PRICE+ " TEXT, "+  // 7
            STORE_EMAIL+ " TEXT "+")";  // 8



    public DB(Context context) {
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
    public boolean insertData(String clientId, String storeId, String plate,
                              String imageURL, String description, String number, String price, String storeEmail){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CLIENT_ID, clientId);
        contentValues.put(STORE_ID, storeId);
        contentValues.put(PLATE, plate);
        contentValues.put(IMAGE_URL, imageURL);
        contentValues.put(DESCRIPTION, description);
        contentValues.put(NUMBER, number);
        contentValues.put(PRICE, price);
        contentValues.put(STORE_EMAIL, storeEmail);

        long result = db.insert(DB_TABLE, null, contentValues);

        if (result == -1){

            return false;
        }else {

            return true;
        }

        // return result != -1; // if result = -1 data doesn't inserted
    }

    // A method to delete data
    public void deleteData(String id, String clientId, String storeId, String plate,
                           String imageURL, String description, String number, String price, String storeEmail){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_TABLE,ID + " = ?",new String[]{id});
        db.delete(DB_TABLE,CLIENT_ID + " = ?",new String[]{clientId});
        db.delete(DB_TABLE,STORE_ID + " = ?",new String[]{storeId});
        db.delete(DB_TABLE,PLATE + " = ?",new String[]{plate});
        db.delete(DB_TABLE,IMAGE_URL + " = ?",new String[]{imageURL});
        db.delete(DB_TABLE,DESCRIPTION + " = ?",new String[]{description});
        db.delete(DB_TABLE,NUMBER + " = ?",new String[]{number});
        db.delete(DB_TABLE,PRICE + " = ?",new String[]{price});
        db.delete(DB_TABLE,STORE_EMAIL + " = ?",new String[]{storeEmail});
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
