package com.example.samuel.inventory;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import com.example.samuel.inventory.data.ProductDbHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;

import static com.squareup.picasso.Picasso.*;
import com.example.samuel.inventory.DetailsActivity;

/**
 * Created by Samuel on 21/06/2017.
 */

public class ProductCursorAdapter extends CursorAdapter  {
    private Toast mToast;



    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
         View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        MyviewHolder holder = new MyviewHolder(view);
        view.setTag(holder);
        return  view;
    }
    class MyviewHolder{

         ImageView image;
         TextView product;
         TextView price;
         TextView quantity;
        Button saleButton;
        TextView missing_text;

        public MyviewHolder(View view) {
            image = (ImageView) view.findViewById(R.id.detail_image);
            product = (TextView) view.findViewById(R.id.detail_product);
            price = (TextView) view.findViewById(R.id.detail_price);
            quantity = (TextView) view.findViewById(R.id.detail_quantity);
            saleButton = (Button) view.findViewById(R.id.salebutton);
            missing_text = (TextView) view.findViewById(R.id.missing_image);
        }
    }


    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final MyviewHolder holder = (MyviewHolder) view.getTag();
        String Product = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT));
        String Price = String.valueOf(cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE)));
        String Quantity = String.valueOf(cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY)));
        String file_path = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_IMAGE));
        Price += "$";
        Quantity += " UNITS";



        File myfile = new File(file_path);
        Uri imageUri = Uri.fromFile(myfile);
        if(myfile.exists()){
            holder.missing_text.setVisibility(View.GONE);

            holder.image.setTag(imageUri);
            Picasso.with(context).load(imageUri).fit().centerCrop().into(holder.image);

        }
        else{
            holder.missing_text.setVisibility(View.VISIBLE);
            holder.image.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.image.setPadding(2,2,2,2);

        }

        holder.product.setText(Product);
        holder.price.setText(Price);
        holder.quantity.setText(Quantity);
        holder.saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductDbHelper myDbHelper = new ProductDbHelper(v.getContext());
                SQLiteDatabase db = myDbHelper.getWritableDatabase();
                String parts[] = holder.quantity.getText().toString().split(" ");
                int availableQuantity = Integer.parseInt(parts[0]);
                if(mToast != null){
                    mToast.cancel();
                }
                if(availableQuantity == 0){
                   mToast = Toast.makeText(v.getContext(), "No more units available, please order for more...", Toast.LENGTH_LONG);
                    mToast.show();
               return;
                }
                else if (availableQuantity == 2){
                   mToast = Toast.makeText(v.getContext(), "Sold!..." + (availableQuantity - 1) + " unit remaining", Toast.LENGTH_LONG);
                    mToast.show();
                }
                else if (availableQuantity == 1){
                    mToast = Toast.makeText(v.getContext(), "Sold!... last unit remaining", Toast.LENGTH_LONG);
                    mToast.show();
                }
                    else{
                  mToast = Toast.makeText(v.getContext(), "Sold!...", Toast.LENGTH_LONG);
                    mToast.show();

                }
                    availableQuantity -= 1;
                if(availableQuantity == 1){
                    holder.quantity.setText(String.valueOf(availableQuantity) + " UNIT");
                }
                else {
                    holder.quantity.setText(String.valueOf(availableQuantity) + " UNITS");
                }
                long id = cursor.getLong(cursor.getColumnIndex(ProductContract.ProductEntry._ID));

                ContentValues values = new ContentValues();
                values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, availableQuantity);

                db.update(ProductContract.ProductEntry.TABLE_NAME,values,"_id=?",new String[]{String.valueOf(id)});
                db.close();


            }
        });
             }





}
