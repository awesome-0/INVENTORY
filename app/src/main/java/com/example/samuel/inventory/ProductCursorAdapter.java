package com.example.samuel.inventory;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samuel.inventory.data.ProductContract;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;

import static com.squareup.picasso.Picasso.*;
import com.example.samuel.inventory.DetailsActivity;

/**
 * Created by Samuel on 21/06/2017.
 */

public class ProductCursorAdapter extends CursorAdapter  {
    private ImageView image;
    private TextView product;
    private TextView price;
    private TextView quantity;
    LayoutInflater mInflater;


    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    @Override
    public void bindView(View view, final Context context,  Cursor cursor) {




        String Product = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT));
        String Price = String.valueOf(cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE)));
        String Quantity = String.valueOf(cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY)));
        Uri imageUri = Uri.parse(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_IMAGE)));
        String file_path = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_IMAGE));
        int i = Integer.parseInt(Quantity);
        Price += "$";
        Quantity += " UNITS";
        image = (ImageView) view.findViewById(R.id.detail_image);
        product = (TextView) view.findViewById(R.id.detail_product);
        price = (TextView) view.findViewById(R.id.detail_price);
        quantity = (TextView) view.findViewById(R.id.detail_quantity);
        Log.v("image file path ", "image file path is" + file_path);
        File myfile = new File(file_path);
        image.setTag(file_path);
        Picasso.with(context).load(myfile).into(image);
        product.setText(Product);
        price.setText(Price);
        quantity.setText(Quantity);

             }



}
