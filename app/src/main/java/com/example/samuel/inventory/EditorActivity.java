package com.example.samuel.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.MailTo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.samuel.inventory.data.ProductContract;
import com.example.samuel.inventory.data.ProductContract.ProductEntry;
import com.example.samuel.inventory.data.ProductDbHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private ImageView image;
    private TextView product;
    private TextView price;
    private TextView quantity;
    private TextView disappearingTextView;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILES = 2;
    private Uri imageUri;
    private  String FilePath = "";
    private String imgPath ="";
    private static Uri mUri;
    private ProductDbHelper mDbHelper;
private static final int PET_LOADER = 0;
    File imageFile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Intent intent = getIntent();
        mUri = intent.getData();

            if(mUri == null){

            setTitle("Add a Product");
            invalidateOptionsMenu();
        }
        else{
                Log.v("muri","mUri is" + mUri.toString());
            setTitle("Edit a Product");
            getLoaderManager().initLoader(PET_LOADER,null,this);
        }
        image = (ImageView) findViewById(R.id.image);
        product = (TextView) findViewById(R.id.edit_product);
        price = (TextView) findViewById(R.id.edit_price);
        quantity = (TextView) findViewById(R.id.edit_Quantity);
        disappearingTextView = (TextView) findViewById(R.id.disappearing_text);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });


    mDbHelper = new ProductDbHelper(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.saveProduct:
            saveProduct();
                finish();


                break;
            case R.id.action_delete:
                deletionDialog();

                break;

        }
        return super.onOptionsItemSelected(item);
    }
    public Uri setImageUri() {
        // Store image in dcim
        String time_stamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format((System.currentTimeMillis()));

        File ImageDir = new File(Environment.getExternalStorageDirectory(),"Inventory");
        if(!ImageDir.exists()) {
            ImageDir.mkdir();
        }
         this.imageFile = new File(ImageDir, "product_" + time_stamp + ".jpg");
        this.imageUri = Uri.fromFile(imageFile);
        Log.v("image uri", "image uri is " + imageUri.toString());
        this.FilePath = imageUri.getPath();
        return imageUri;
    }
    public String getImagePath() {
        return FilePath;
    }
    private void saveProduct(){



        String ProductName = product.getText().toString().trim();
        String dPrice = price.getText().toString().trim();
        int Price = Integer.parseInt(dPrice);
        String dQuant = quantity.getText().toString().trim();
        int Quantity = Integer.parseInt(dQuant);




        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT,ProductName);
        values.put(ProductEntry.COLUMN_PRICE,Price);

        values.put(ProductEntry.COLUMN_IMAGE,FilePath);
        values.put(ProductEntry.COLUMN_QUANTITY,Quantity);



        if(mUri == null){
            values.put(ProductEntry.COLUMN_IMAGE,FilePath);

        Uri uri = getContentResolver().insert(ProductEntry.CONTENT_URI,values);
        if(uri == null){
            Toast.makeText(this,"couldn't save, Please retry",Toast.LENGTH_LONG).show();
            return;
        }
        else{
            Toast.makeText(this,"Pet saved successfully",Toast.LENGTH_LONG).show();
        }
        }else{
            String imageFilePath = (String) image.getTag();
            Log.v("imageFilePath","image file path is " + imageFilePath);

            values.put(ProductEntry.COLUMN_IMAGE,imageFilePath);
            int update = getContentResolver().update(mUri, values, null, null);

            if(update <1){
                Toast.makeText(this,"Couldn't Update Pet",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this,"Update Successful",Toast.LENGTH_LONG).show();
            }

        }


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(mUri == null) {
            MenuItem item = menu.findItem(R.id.action_delete);
            item.setVisible(false);
        }
        return true;
    }

    private void deletionDialog(){

        final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);
        deleteDialog.setTitle("Are you sure you want to delete?");
        deleteDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null) {
                    deleteProduct();
                    finish();
                }

            }
        });
        deleteDialog.setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = deleteDialog.create();

        alertDialog.show();

    }
    private void deleteProduct(){
        int delete = getContentResolver().delete(mUri, null, null);
        if(delete < 1){
            Toast.makeText(EditorActivity.this,"Deletion Unsuccessful",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(EditorActivity.this,"Deletion Successful",Toast.LENGTH_LONG).show();
        }
    }

    private void getImage(){

        AlertDialog.Builder picDialog = new AlertDialog.Builder(this);

        picDialog.setTitle("Select From");
        picDialog.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                  cameraIntent.putExtra("aspectX", 0);
                   cameraIntent.putExtra("aspectY", 0);
                   cameraIntent.putExtra("outputX", 100);
                   cameraIntent.putExtra("outputY", 50);
                    cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    cameraIntent.putExtra("return data", true);


                   cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,setImageUri());
                    startActivityForResult(cameraIntent, PICK_FROM_CAMERA);
                    }
                catch(Exception e){
                    e.printStackTrace();
                }finally {

                }

            }
        });
        picDialog.setNegativeButton("Files", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent fileIntent = new Intent();
                fileIntent.setType("image/*");
                fileIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(fileIntent,"Complete with"),PICK_FROM_FILES);
            }
        });

        AlertDialog alertDialog = picDialog.create();
        alertDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        if (resultCode != RESULT_CANCELED) {

            if (requestCode == PICK_FROM_CAMERA) {
                if (FilePath.isEmpty()) {
                    Toast.makeText(this, "couldn't save, please Retry", Toast.LENGTH_LONG).show();
                    if( imageFile != null){
                    //imageFile.delete();
                    }
                    return;
                }
                Toast.makeText(EditorActivity.this, "saved to " + FilePath, Toast.LENGTH_LONG).show();
                image.setTag(FilePath);

                Picasso.with(this).load(imageUri).into(image);
                disappearingTextView.setVisibility(View.GONE);
            }
        else{
                     imageUri = data.getData();
                    FilePath = getPath(imageUri);
                    InputStream inputStream = null;
                    try {
                        disappearingTextView.setVisibility(View.GONE);
                        image.setTag(FilePath);
                        Picasso.with(this).load(imageUri).into(image);
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        Toast.makeText(EditorActivity.this, "Image gotten from " + FilePath, Toast.LENGTH_LONG).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    private String getPath(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
            //  cursor = managedQuery(contentUri, proj, null, null, null);
            cursor = loader.loadInBackground();
            if (cursor == null) return null;
            int columnindex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(columnindex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] project = {ProductEntry.COLUMN_PRODUCT,ProductEntry.COLUMN_PRICE,ProductEntry.COLUMN_IMAGE,ProductEntry.COLUMN_QUANTITY};
        return new CursorLoader(this,mUri,project,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() < 1 || data == null) {
            return;
        }

        if (data.moveToFirst()) {
             String Product = data.getString(data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT));
            String Price = String.valueOf(data.getInt(data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE)));
            String Quantity = String.valueOf(data.getInt(data.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY)));
            //Uri imageUri = Uri.parse(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_IMAGE)));
            String file_path = data.getString(data.getColumnIndex(ProductContract.ProductEntry.COLUMN_IMAGE));

            disappearingTextView.setVisibility(View.GONE);
            File myfile = new File(file_path);
            image.setTag(file_path);
            Picasso.with(this).load(myfile).into(image);

            product.setText(Product);
            price.setText(Price);
            quantity.setText(Quantity);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        product.setText("");
        price.setText("");
        quantity.setText("");
    }
    private void OrderDialog(){

        AlertDialog.Builder getInput = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        getInput.setView(input);
        getInput.setTitle("How many items do you want");
        getInput.setPositiveButton("ORDER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null) {
                    String [] Project = {ProductEntry.COLUMN_PRODUCT};
                    Cursor saleInfo = getContentResolver().query(mUri,Project,null,null,null);
                    String Product = saleInfo.getString(saleInfo.getColumnIndex(ProductEntry.COLUMN_PRODUCT));
                    String amountOrderedFor = input.getText().toString();
                    String myOrder = "Good day, plese i would like to order for " + amountOrderedFor  + " units of "
                            + Product + "\nThank you ";

                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.putExtra(Intent.EXTRA_SUBJECT,"Order for your Products");
                    intent.putExtra(Intent.EXTRA_TEXT,myOrder);
                    startActivity(intent);

                }

            }
        });
        getInput.setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = getInput.create();

        alertDialog.show();


    }
}
