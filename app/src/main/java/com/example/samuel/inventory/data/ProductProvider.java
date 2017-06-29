package com.example.samuel.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.samuel.inventory.DetailsActivity;
import com.example.samuel.inventory.data.ProductContract.ProductEntry;


/**
 * Created by Samuel on 21/06/2017.
 */

public class ProductProvider extends ContentProvider {
    /** URI matcher code for the content URI for the pets table */
    private static final int PRODUCTS = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int PRODUCTS_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(ProductContract.AUTHORITY,ProductContract.PATH_PRODUCTS,PRODUCTS);
        sUriMatcher.addURI(ProductContract.AUTHORITY,ProductContract.PATH_PRODUCTS + "/#",PRODUCTS_ID);


    }
    private ProductDbHelper mDbHelper ;

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                 cursor = db.query(ProductEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;

            case PRODUCTS_ID:

                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                 cursor = db.query(ProductEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:throw  new IllegalArgumentException("cannot Query given Uri" + uri.toString());

        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
              case PRODUCTS:
                  return insertProduct(uri,values);

            default:
                throw new IllegalArgumentException("cannot Query given Uri"  + uri.toString());
        }
    }


    private Uri insertProduct (Uri uri,ContentValues values){
        String product = values.getAsString(ProductEntry.COLUMN_PRODUCT);
        String price = values.getAsString(ProductEntry.COLUMN_PRICE);
        String quant = values.getAsString(ProductEntry.COLUMN_QUANTITY);



        Log.v("Product", "product nam is " +product);
        Log.v("Price", "price nam is " +price);
        Log.v("quant", "quant nam is " +quant);

        if(product == null){
            throw new IllegalArgumentException("Product requires a name");
        }
        if(price == null){
            throw new IllegalArgumentException("Product requires a price");
        }
        if(quant == null){
            throw new IllegalArgumentException("Product requires a quantity");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(ProductEntry.TABLE_NAME, null, values);
        Log.v("row of inserted product", "row is " + String.valueOf(id));

        if(id == -1){
            Toast.makeText(getContext(),"failed to insert row into database",Toast.LENGTH_LONG).show();
            return null ;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,id);


    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
              return deletePet(uri,selection,selectionArgs);
            case PRODUCTS_ID:

                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deletePet(uri,selection,selectionArgs);
            default:throw  new IllegalArgumentException("cannot Query given Uri" + uri.toString());

        }
    }
    private int deletePet(Uri uri, String selection,String[] selectionArgs){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int delete = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri,null);
        return delete;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return UpdateProduct(uri,values,selection,selectionArgs);

            case PRODUCTS_ID:

                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return UpdateProduct(uri,values,selection,selectionArgs);

            default:throw  new IllegalArgumentException("cannot Query given Uri" + uri.toString());

        }
    }

    private int UpdateProduct(Uri uri,ContentValues values,String selection,String[] selectionArgs){

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int update = db.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        if(update == 0){
            Toast.makeText(getContext(),"failed to update row into database",Toast.LENGTH_LONG).show();
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return  update;

    }

    }
