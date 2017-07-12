package com.example.samuel.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samuel.inventory.data.ProductContract.ProductEntry;
import com.example.samuel.inventory.data.ProductDbHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>  {
    private ProductDbHelper mDbHelper;
    private FloatingActionButton fab;
    private ProductCursorAdapter mAdapter;
    private static final int PRODUCT_LOADER = 0;
    public static Uri currentProductUri;
    private ListView list;
    LinearLayout noproductView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        noproductView = (LinearLayout) findViewById(R.id.noProductView);
        String[] project = {ProductEntry.COLUMN_PRODUCT, ProductEntry.COLUMN_PRICE, ProductEntry.COLUMN_IMAGE, ProductEntry.COLUMN_QUANTITY};

        Cursor mCursor = getContentResolver().query(ProductEntry.CONTENT_URI,project,null,null,null);
        if(mCursor.getCount() == 0){
            noproductView.setVisibility(View.VISIBLE);
        }else{
            noproductView.setVisibility(View.GONE);
        }

        mDbHelper = new ProductDbHelper(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        mAdapter = new ProductCursorAdapter(this, null);

         list = (ListView) findViewById(R.id.list);
        list.setItemsCanFocus(true);
        list.setAdapter(mAdapter);
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                final Intent intent = new Intent(DetailsActivity.this, EditorActivity.class);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
                     });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        String[] project = {ProductEntry.COLUMN_PRODUCT, ProductEntry.COLUMN_PRICE, ProductEntry.COLUMN_IMAGE, ProductEntry.COLUMN_QUANTITY};
        Cursor mCursor = getContentResolver().query(ProductEntry.CONTENT_URI,project,null,null,null);
        if(mCursor.getCount() == 0){
            noproductView.setVisibility(View.VISIBLE);
        }else{
            noproductView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_products:
                deletionDialog();
               break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] project = {ProductEntry._ID, ProductEntry.COLUMN_IMAGE, ProductEntry.COLUMN_QUANTITY,
                ProductEntry.COLUMN_PRODUCT, ProductEntry.COLUMN_PRICE};
        return new CursorLoader(this, ProductEntry.CONTENT_URI, project, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);

    }

    private void deletionDialog() {

        final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);
        deleteDialog.setTitle(R.string.sure_to_delete);
        deleteDialog.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    deleteProduct();

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
        int delete = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
        if (delete < 1) {
            Toast.makeText(this, R.string.delete_failed, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.delete_successful, Toast.LENGTH_LONG).show();
        }
    }


}
