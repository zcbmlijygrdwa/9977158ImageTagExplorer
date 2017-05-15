package zhenyuyang.cs190i.cs.ucsb.edu.a9977158imagetagexplorer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    static Random rnd = new Random();
    //    String[] web = {
//            "Google"
//    } ;
    Uri[] imageUris;
    GridView grid;
    final int NEW_PHOTO_REQUEST = 5;
    ImageTagDatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageTagDatabaseHelper.Initialize(this);
        dbHelper = ImageTagDatabaseHelper.GetInstance();

// register floatingActionButton


        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("my", "FloatingActionButton");
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) , NEW_PHOTO_REQUEST);//one can be replaced with any action code
            }
        });

        // end of register floatingActionButton

        Button button = (Button) findViewById(R.id.button_online);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                getAllOnlineResource();
                updateGridViewWithDB(dbHelper.getReadableDatabase());
            }
        });


        Button button_refresh = (Button) findViewById(R.id.button_refresh);
        button_refresh.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                updateGridViewWithDB(dbHelper.getReadableDatabase());
            }
        });




        imageUris = getImageUriFromDB(dbHelper.getReadableDatabase());
        // ==============    GridView   =====================
        SelelctImageGrid adapter = new SelelctImageGrid(this, imageUris);
        grid = (GridView) findViewById(R.id.grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.i("addOnItemTouchListener", "onItemClick position =" + position);


            }
        });

// ==============   End of GridView   =====================


        // ==============    ImageTagDatabaseHelper   =====================


// *****************  ImageTagDatabaseHelper  Test *****************
/*
        //  write information
        SQLiteDatabase database_w;
        database_w = dbHelper.getWritableDatabase();
        Log.i("SQLiteDatabase", "database = " + database_w.toString());

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        String column_name_insert = "COLUMN_NAME_TITLE";
        values.put(column_name_insert, "testTitle2");
        column_name_insert = "COLUMN_NAME_SUBTITLE";
        values.put(column_name_insert, "testSubtitle");

        // Insert the new row, returning the primary key value of the new row
        String tableName_insert = "TABLE_NAME";
        long newRowId = database_w.insert(tableName_insert, null, values);
        Log.i("SQLiteDatabase", "newRowId = " + newRowId);
        // end of write information

        //  read information

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                "id",
                "COLUMN_NAME_TITLE",
                "COLUMN_NAME_SUBTITLE"
        };

        // Filter results WHERE "title" = 'My Title'
        String column_name_read = "COLUMN_NAME_TITLE";
        String selection = column_name_read + " = ?";
        String[] selectionArgs = { "testTitle1" };

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                "COLUMN_NAME_SUBTITLE" + " DESC";
        String tableName_read = "TABLE_NAME";
        Cursor cursor = db.query(
                tableName_read,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        Log.i("cursor", "cursor !");
        List itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow("id"));
            Log.i("cursor", "cursor itemId = " + itemId);
            itemIds.add(itemId);
        }
        cursor.close();

        // end of read information
        */
// ***************** end of  ImageTagDatabaseHelper  Test *****************


        // ==============    End of ImageTagDatabaseHelper   =====================


        final TextView textView = (TextView) findViewById(R.id.textView);
        // final ImageView imageView = (ImageView) findViewById(R.id.imageView);


        TaggedImageRetriever.getNumImages(new TaggedImageRetriever.ImageNumResultListener() {
            @Override
            public void onImageNum(int num) {
                Log.i("onImageNum", "num = " + num);
                textView.setText(textView.getText() + "\n\n" + num);
            }
        });


        int index = 2; //0~10
        TaggedImageRetriever.getTaggedImageByIndex(index, new TaggedImageRetriever.TaggedImageResultListener() {
            @Override
            public void onTaggedImage(TaggedImageRetriever.TaggedImage image) {
                if (image != null) {
                    try (FileOutputStream stream = openFileOutput("Test.jpg", Context.MODE_PRIVATE)) {
                        image.image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        image.image.recycle();
                    } catch (IOException e) {
                    }

                    String filePathURI = Uri.fromFile(getFileStreamPath("Test.jpg")).toString();
                    Log.i("onTaggedImage", "getFileStreamPath(\"Test.jpg\") = " + filePathURI);
                    // Picasso.with(MainActivity.this).load(filePathURI).resize(500, 500).centerCrop().into(imageView);
                    // imageView.setImageBitmap(image.image);
                    StringBuilder tagList = new StringBuilder();
                    for (String p : image.tags) {
                        tagList.append(p + "\n");
                    }
                    textView.setText(textView.getText() + "\n\n" + tagList.toString());
                }
            }

            @Override
            public void onTaggedImage(TaggedImageRetriever.TaggedImage image, int index) {

            }
        });


    }

    void updateGridViewWithDB(SQLiteDatabase db){
        //imageUris = getImageUriFromDB(db);
        UpdateGridViewWithDataBase y = new UpdateGridViewWithDataBase(new UpdateGridViewWithDataBase.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(Uri[] temp_uris) {
                Log.i("my", "onTaskCompleted, u.length = " + temp_uris.length);
                SelelctImageGrid adapter = new SelelctImageGrid(getApplicationContext(), temp_uris);
                grid = (GridView) findViewById(R.id.grid);
                grid.setAdapter(adapter);
            }
        });
        y.execute(db);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to


        if (requestCode == NEW_PHOTO_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Uri selectedImageURI = data.getData();
                Log.i("onActivityResult", "result = " + selectedImageURI.toString());
                saveImageUriToDB(selectedImageURI.toString(), dbHelper.getReadableDatabase());
                updateGridViewWithDB(dbHelper.getWritableDatabase());
                //Uri[] imageUris = { selectedImageURI,selectedImageURI,selectedImageURI};
//                imageUris = addUri(imageUris,selectedImageURI);
//
//                SelelctImageGrid adapter = new SelelctImageGrid(SelectImageActivity.this, web, imageUris);
//                Log.i("onActivityResult", "imageUris.length = " + imageUris.length);
//                grid=(GridView)findViewById(R.id.grid);
//                grid.setAdapter(adapter);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("onActivityResult", "NEW_TITLE_REQUEST RESULT_CANCELED");
                Toast.makeText(this, "Pick photo canceled", Toast.LENGTH_LONG).show();
                //Write your code if there's no result
            }
        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageTagDatabaseHelper.GetInstance().close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ImageTagDatabaseHelper.GetInstance().close();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        // Show your dialog here (this is called right after onActivityResult)
    }

    void getAllOnlineResource() {
        int imageIndex = 0;


        TaggedImageRetriever.getNumImages(new TaggedImageRetriever.ImageNumResultListener() {
            @Override
            public void onImageNum(int num) {
                Log.i("onImageNum", "num = " + num);
                int imageCount = num;
                // textView.setText(textView.getText() + "\n\n" + num);
                getAllOnlineImages(num);
            }
        });

    }

    void getAllOnlineImages(int count) {
        int index = 2; //0~10
        ImageTagDatabaseHelper.Initialize(this);
        ImageTagDatabaseHelper dbHelper = ImageTagDatabaseHelper.GetInstance();
        final SQLiteDatabase database_w = dbHelper.getWritableDatabase();
        for (int i = 0; i < count; i++) {
            index = i;
            Log.i("getAllOnlineImages", "i = " + i);


            TaggedImageRetriever.getTaggedImageByIndex(index, new TaggedImageRetriever.TaggedImageResultListener() {
                @Override
                public void onTaggedImage(TaggedImageRetriever.TaggedImage image) {

                }

                @Override
                public void onTaggedImage(TaggedImageRetriever.TaggedImage image, int index) {
                    if (image != null) {
                        String fileName = "Test" + index + ".jpg";
                        try (FileOutputStream stream = openFileOutput(fileName, Context.MODE_PRIVATE)) {
                            image.image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        } catch (IOException e) {
                        }

                        String filePathURI = Uri.fromFile(getFileStreamPath(fileName)).toString();
                        saveImageUriToDB(filePathURI, database_w);
                        Log.i("onTaggedImage", "getFileStreamPath = " + filePathURI);
                        //Picasso.with(MainActivity.this).load(filePathURI).resize(500, 500).centerCrop().into(imageView);
                        // imageView.setImageBitmap(image.image);
                        StringBuilder tagList = new StringBuilder();
                        for (String p : image.tags) {
                            tagList.append(p + "\n");
                        }
                        // textView.setText(textView.getText() + "\n\n" + tagList.toString());
                    }
                }
            });
        }
        //database_w.close();   //????
    }


    void saveImageUriToDB(String uri_input, SQLiteDatabase database_w) {

        Log.i("SQLiteDatabase", "database = " + database_w.toString());

        // Insert the new row, returning the primary key value of the new row
        String tableName_insert = "Image";

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        String column_name_insert = "ImageUri";
        values.put(column_name_insert, uri_input);
        try {
            long newRowId = database_w.insertOrThrow(tableName_insert, null, values);
            Log.i("SQLiteDatabase", "newRowId = " + newRowId);
        } catch (Exception e) {
            Log.i("SQLiteDatabase", "Exception = " + e.toString());
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    Uri[] getImageUriFromDBAtIndex(int index, SQLiteDatabase db) {
//  read information
        String[] projection = {
                "Id",
                "ImageUri",
        };

        // Filter results WHERE "title" = 'My Title'
        String column_name_read_filter = "Id";
        String selection = column_name_read_filter + " = *";
        String[] selectionArgs = {"" + index};

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                "Id" + " DESC";
        String tableName_read = "Image";
//        Cursor cursor = db.query(
//                tableName_read,                     // The table to query
//                projection,                               // The columns to return
//                selection,                                // The columns for the WHERE clause
//                selectionArgs,                            // The values for the WHERE clause
//                null,                                     // don't group the rows
//                null,                                     // don't filter by row groups
//                sortOrder                                 // The sort order
//        );

        String query = "SELECT * FROM " + tableName_read;

        Cursor cursor = db.rawQuery(query, null);

        Log.i("cursor", "cursor !");
        ArrayList<Uri> itemIds = new ArrayList<Uri>();
        while (cursor.moveToNext()) {
            // long itemId = cursor.getLong(cursor.getColumnIndexOrThrow("Id"));
            //String ss = cursor.getString(cursor.getColumnIndexOrThrow("ImageUri"));
            Uri ss = Uri.parse(cursor.getString(cursor.getColumnIndex("ImageUri")));
            Log.i("cursor", "1cursor ImageUri = " + ss);
            itemIds.add(ss);
        }
        cursor.close();
        // end of read information
        Uri[] uris = itemIds.toArray(new Uri[itemIds.size()]);

//        for(int i = 0; i< uris.length;i++){
//            Log.i("cursor", "uris["+i+"] = " + uris[i]);
//        }

        return uris;
    }


    Uri[] getImageUriFromDB(SQLiteDatabase db) {
//  read information
        String[] projection = {
                "Id",
                "ImageUri",
        };

        // Filter results WHERE "title" = 'My Title'
        String column_name_read_filter = "Id";
        String selection = column_name_read_filter + " = *";


// How you want the results sorted in the resulting Cursor
        String sortOrder =
                "Id" + " DESC";
        String tableName_read = "Image";
//        Cursor cursor = db.query(
//                tableName_read,                     // The table to query
//                projection,                               // The columns to return
//                selection,                                // The columns for the WHERE clause
//                selectionArgs,                            // The values for the WHERE clause
//                null,                                     // don't group the rows
//                null,                                     // don't filter by row groups
//                sortOrder                                 // The sort order
//        );

        String query = "SELECT * FROM " + tableName_read;

        Cursor cursor = db.rawQuery(query, null);

        Log.i("cursor", "cursor !");
        ArrayList<Uri> itemIds = new ArrayList<Uri>();
        while (cursor.moveToNext()) {
            // long itemId = cursor.getLong(cursor.getColumnIndexOrThrow("Id"));
            //String ss = cursor.getString(cursor.getColumnIndexOrThrow("ImageUri"));
            Uri ss = Uri.parse(cursor.getString(cursor.getColumnIndex("ImageUri")));
            Log.i("cursor", "1cursor ImageUri = " + ss);
            itemIds.add(ss);
        }
        cursor.close();
        // end of read information
        Uri[] uris = itemIds.toArray(new Uri[itemIds.size()]);

//        for(int i = 0; i< uris.length;i++){
//            Log.i("cursor", "uris["+i+"] = " + uris[i]);
//        }

        return uris;
    }


}