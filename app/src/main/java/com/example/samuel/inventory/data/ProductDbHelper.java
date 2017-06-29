package com.example.samuel.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.samuel.inventory.data.ProductContract.ProductEntry;

/**
 * Created by Samuel on 21/06/2017.
 */

public class ProductDbHelper extends SQLiteOpenHelper {
    //database name
    private static final String DATABASE_NAME = "products.db";
    //database version ,if changed...you have to increment the version
    private static final int DATABASE_VERSION = 1;

    public ProductDbHelper(Context context){//, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
String SQL_CREATE_PRODUCTS_TABLE ="CREATE TABLE "+ ProductEntry.TABLE_NAME+"("+
        ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        ProductEntry.COLUMN_PRODUCT + " TEXT NOT NULL,"+
        ProductEntry.COLUMN_PRICE + " INTEGER NOT NULL ,"+
        ProductEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0,"+
        ProductEntry.COLUMN_IMAGE + " TEXT NOT NULL"+");";

        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
        Log.v("DATABASE", "database created");


}


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
