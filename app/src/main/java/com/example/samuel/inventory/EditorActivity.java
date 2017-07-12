package com.example.samuel.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samuel.inventory.data.ProductContract;
import com.example.samuel.inventory.data.ProductContract.ProductEntry;
import com.example.samuel.inventory.data.ProductDbHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ImageView image;
    private TextView product;
    private TextView price;
    private TextView quantity;
    private TextView disappearingTextView;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILES = 2;
    private static Uri imageUri;
    private String FilePath = "";
    private static Uri mUri;
    private ProductDbHelper mDbHelper;
    private boolean mProductTouched = false;
    private static final int PET_LOADER = 0;
    File imageFile;
    File ImageDir;

    private static int productNumber ;
    int container = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        if(savedInstanceState != null){
            productNumber = savedInstanceState.getInt("currentProduct",0);
        }



        Intent intent = getIntent();
        mUri = intent.getData();

        if (mUri == null) {

            setTitle(getString(R.string.product_add));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.product_Edit));
            getLoaderManager().initLoader(PET_LOADER, null, this);
        }
        View.OnTouchListener mlistener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mProductTouched = true;
                return false;
            }
        };

        ImageDir = new File(Environment.getExternalStorageDirectory(), "Inventory");
        if (!ImageDir.exists()) {
            ImageDir.mkdir();
        }
        image = (ImageView) findViewById(R.id.image);
        product = (TextView) findViewById(R.id.edit_product);
        price = (TextView) findViewById(R.id.edit_price);
        quantity = (TextView) findViewById(R.id.edit_Quantity);
        disappearingTextView = (TextView) findViewById(R.id.disappearing_text);
        image.setOnTouchListener(mlistener);
        product.setOnTouchListener(mlistener);
        price.setOnTouchListener(mlistener);
        quantity.setOnTouchListener(mlistener);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });
        mDbHelper = new ProductDbHelper(this);

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt("currentProduct",productNumber);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.saveProduct:
                String tag = (String) image.getTag();

                if(product.getText().toString().isEmpty() ){
                    Toast.makeText(this, R.string.enter_product,Toast.LENGTH_LONG).show();
                    return true ;
                }if( price.getText().toString().isEmpty()){
                Toast.makeText(this, R.string.enter_price,Toast.LENGTH_LONG).show();
                return true ;


            } if( quantity.getText().toString().isEmpty()){
                Toast.makeText(this, R.string.enter_quantity,Toast.LENGTH_LONG).show();
                return true ;
            }
                if(tag== null){
                    Toast.makeText(this, R.string.add_image,Toast.LENGTH_LONG).show();
                    return true;
                }
                saveProduct();
                finish();
                return true;
            case R.id.action_delete:
                deletionDialog();
                return true;
            case R.id.action_order:
                OrderDialog();
                return true;
            case android.R.id.home:
                if (!mProductTouched) {

                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                } else {

                    leaveScreenDialog();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public Uri setImageUri() {
        this.imageFile = new File(ImageDir, "product_" + productNumber + ".jpg");
        container = productNumber;
        this.imageUri = Uri.fromFile(imageFile);
        this.FilePath = imageUri.getPath();
        return imageUri;
    }

    @Override
    public void onBackPressed() {
        if (!mProductTouched) {
            super.onBackPressed();
            return;
        }
        leaveScreenDialog();
    }

    public String getImagePath() {
        return FilePath;
    }

    private void saveProduct() {


        String ProductName = product.getText().toString().trim();
        String dPrice = price.getText().toString().trim();
        int Price = Integer.parseInt(dPrice);
        String dQuant = quantity.getText().toString().trim();
        int Quantity = Integer.parseInt(dQuant);
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT, ProductName);
        values.put(ProductEntry.COLUMN_PRICE, Price);
        values.put(ProductEntry.COLUMN_IMAGE, FilePath);
        values.put(ProductEntry.COLUMN_QUANTITY, Quantity);
        if (mUri == null) {
            String imageFilePath = (String) image.getTag();
            values.put(ProductEntry.COLUMN_IMAGE, imageFilePath);

            Uri uri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            if (uri == null) {
                Toast.makeText(this, R.string.save_failed, Toast.LENGTH_LONG).show();

                return;
            } else {
                Toast.makeText(this, R.string.save_successful, Toast.LENGTH_LONG).show();
            }
        } else {
            String imageFilePath = (String) image.getTag();
            values.put(ProductEntry.COLUMN_IMAGE, imageFilePath);
            int update = getContentResolver().update(mUri, values, null, null);



            if (update < 1) {
                Toast.makeText(this, R.string.update_failed, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.update_successful, Toast.LENGTH_LONG).show();
            }

        }


    }

    public void leaveScreenDialog() {
        AlertDialog.Builder discardDialog = new AlertDialog.Builder(this);
        discardDialog.setTitle(R.string.discard_changes);
        discardDialog.setNegativeButton(R.string.discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    finish();
                }

            }
        });
        discardDialog.setPositiveButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = discardDialog.create();

        alertDialog.show();


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mUri == null) {
            MenuItem item = menu.findItem(R.id.action_delete);
            item.setVisible(false);
        }
        return true;
    }

    private void deletionDialog() {

        final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);
        deleteDialog.setTitle(R.string.sure_to_delete);
        deleteDialog.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    deleteProduct();
                    finish();
                }

            }
        });
        deleteDialog.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = deleteDialog.create();

        alertDialog.show();

    }

    private void deleteProduct() {
        int delete = getContentResolver().delete(mUri, null, null);
        if (delete < 1) {
            Toast.makeText(EditorActivity.this, R.string.delete_failed, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(EditorActivity.this, R.string.delete_successful, Toast.LENGTH_LONG).show();
        }
    }

    private void getImage() {

        AlertDialog.Builder picDialog = new AlertDialog.Builder(this);

        picDialog.setTitle(R.string.select_from);
        picDialog.setPositiveButton(R.string.camera, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                try {
                    cameraIntent.putExtra("aspectX", 0);
                    cameraIntent.putExtra("aspectY", 0);
                    cameraIntent.putExtra("outputX", 200);
                    cameraIntent.putExtra("outputY", 100);
                    cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    cameraIntent.putExtra("return data", true);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
                    startActivityForResult(cameraIntent, PICK_FROM_CAMERA);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    }


            }
        });
        picDialog.setNegativeButton(R.string.files, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent fileIntent = new Intent();
                fileIntent.setType("image/*");
                fileIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(fileIntent, getString(R.string.complete_with)), PICK_FROM_FILES);

            }
        });

        AlertDialog alertDialog = picDialog.create();
        alertDialog.show();

    }
    public File getFile(int product){
        File fileFromPhone = new File(ImageDir.getAbsolutePath() + "/product_" + product + ".jpg");
        return fileFromPhone;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        if (resultCode != RESULT_CANCELED) {

            if (requestCode == PICK_FROM_CAMERA) {


                if(FilePath.isEmpty()) {
                    File fileFromPhone = getFile(container);
                    String camFilePath = fileFromPhone.getAbsolutePath();
                    Uri dImageUri = Uri.fromFile(fileFromPhone);
                    if (fileFromPhone.exists()) {
                        image.setTag(camFilePath);
                        Toast.makeText(EditorActivity.this, getString(R.string.saved_to) + camFilePath, Toast.LENGTH_LONG).show();
                        Picasso.with(this).load(dImageUri).fit().centerCrop().into(image);
                        disappearingTextView.setVisibility(View.GONE);

                    } else {
                        Toast.makeText(EditorActivity.this, R.string.No_exisiting_image, Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    image.setTag(FilePath);
                    Toast.makeText(EditorActivity.this, getString(R.string.saved_to) + FilePath, Toast.LENGTH_LONG).show();
                    Picasso.with(this).load(imageUri).fit().centerCrop().into(image);
                    disappearingTextView.setVisibility(View.GONE);


                }



            } else {
               Uri dImageUri = data.getData();
                FilePath = getPath(dImageUri);
                InputStream inputStream = null;
                try {
                    disappearingTextView.setVisibility(View.GONE);
                    image.setTag(FilePath);
                    Picasso.with(this).load(dImageUri).fit().into(image);
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    Toast.makeText(EditorActivity.this, getString(R.string.image_gotten_from) + FilePath, Toast.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        productNumber++;
    }


    private String getPath(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
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
        String[] project = {ProductEntry.COLUMN_PRODUCT, ProductEntry.COLUMN_PRICE, ProductEntry.COLUMN_IMAGE, ProductEntry.COLUMN_QUANTITY};
        return new CursorLoader(this, mUri, project, null, null, null);
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
            String file_path = data.getString(data.getColumnIndex(ProductContract.ProductEntry.COLUMN_IMAGE));

            disappearingTextView.setVisibility(View.GONE);
            File myfile = new File(file_path);
            Uri imgUri = Uri.fromFile(myfile);
            image.setTag(file_path);
            Picasso.with(this).load(imgUri).fit().centerCrop().into(image);

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

    private void OrderDialog() {

        AlertDialog.Builder getInput = new AlertDialog.Builder(this);
        getInput.setTitle(R.string.order_info);
        getInput.setMessage(R.string.items_needed);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 0, 40, 0);

        final EditText QUANTITY_INPUT = new EditText(EditorActivity.this);
        QUANTITY_INPUT.setHint(R.string.quantity);
        QUANTITY_INPUT.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(QUANTITY_INPUT);

        final EditText EMAIL_INPUT = new EditText(EditorActivity.this);
        EMAIL_INPUT.setHint(R.string.supplier_email);
        layout.addView(EMAIL_INPUT);
        getInput.setView(layout);

        getInput.setPositiveButton(R.string.order, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    String[] Project = {ProductEntry.COLUMN_PRODUCT};
                    Cursor saleInfo = getContentResolver().query(mUri, Project, null, null, null);
                    saleInfo.moveToFirst();
                    String Product = saleInfo.getString(saleInfo.getColumnIndex(ProductEntry.COLUMN_PRODUCT));
                    String amountOrderedFor = QUANTITY_INPUT.getText().toString().trim();
                    String email = EMAIL_INPUT.getText().toString().trim();
                    String myOrder = "Good day, please i would like to order for " + amountOrderedFor + " units of "
                            + Product + ".\nThank you ";

                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.order_for));
                    intent.putExtra(Intent.EXTRA_TEXT, myOrder);
                    startActivity(intent);

                }

            }
        });
        getInput.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = getInput.create();

        alertDialog.show();


    }
}
