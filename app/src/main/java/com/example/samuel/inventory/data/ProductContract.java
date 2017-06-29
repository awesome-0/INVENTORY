package com.example.samuel.inventory.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URL;

/**
 * Created by Samuel on 21/06/2017.
 */

public final class ProductContract {
    private ProductContract(){}

    private static final String CONTENT_URL = "content://com.example.samuel.inventory/products";
    static final String AUTHORITY = "com.example.samuel.inventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    static final String PATH_PRODUCTS = "products";

    public static class ProductEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.parse(CONTENT_URL);


        public static final String TABLE_NAME = "products";
        // now for each column in the table
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT = "product";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_IMAGE= "file_path";
        public static final String COLUMN_QUANTITY = "quantity";


    }
}
