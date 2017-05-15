package zhenyuyang.cs190i.cs.ucsb.edu.a9977158imagetagexplorer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                ImageTagDatabaseHelper dbHelper = ImageTagDatabaseHelper.GetInstance();

                //  write information
                SQLiteDatabase database_w;
                database_w = dbHelper.getWritableDatabase();

                    String[] imageUris = getImageUriFromDBAtIndex(1,dbHelper.getReadableDatabase());

                getAllOnlineResource();
            }
        });


        // ==============    ImageTagDatabaseHelper   =====================

        ImageTagDatabaseHelper.Initialize(this);
        //ImageTagDatabaseHelper h = new ImageTagDatabaseHelper(this);
        ImageTagDatabaseHelper dbHelper = ImageTagDatabaseHelper.GetInstance();


//        SQLiteDatabase db1 = dbHelper.getReadableDatabase();
//        String[] selectionArgs1 = { "1" };
//        Cursor cursor1 = db1.query(
//                "asdf",                     // The table to query
//                selectionArgs1,                               // The columns to return
//                "asdf",                                // The columns for the WHERE clause
//                selectionArgs1,                            // The values for the WHERE clause
//                null,                                     // don't group the rows
//                null,                                     // don't filter by row groups
//                "asdf"                                 // The sort order
//        );

        //  write information
        SQLiteDatabase database_w;
        database_w = dbHelper.getWritableDatabase();
        Log.i("SQLiteDatabase", "database = " + database_w.toString());


        // Insert the new row, returning the primary key value of the new row
        String tableName_insert = "Image";

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        String column_name_insert = "ImageUri";
        values.put(column_name_insert, "http://cs.jalexander.ninja:8080/");
//        column_name_insert = "COLUMN_NAME_SUBTITLE";
//        values.put(column_name_insert, "testSubtitle");

        try {
            long newRowId = database_w.insertOrThrow(tableName_insert, null, values);
            Log.i("SQLiteDatabase", "newRowId = " + newRowId);
        } catch (Exception e) {
            Log.i("SQLiteDatabase", "Exception = " + e.toString());
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

        // end of write information

        //  read information

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                "Id",
                "ImageUri",
        };

        // Filter results WHERE "title" = 'My Title'
        String column_name_read_filter = "Id";
        String selection = column_name_read_filter + " = ?";
        String[] selectionArgs = {"2"};

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                "Id" + " DESC";
        String tableName_read = "Image";
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
        while (cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow("Id"));
            String ss = cursor.getString(cursor.getColumnIndexOrThrow("ImageUri"));
            Log.i("cursor", "cursor ss = " + ss);
            itemIds.add(itemId);
        }
        cursor.close();

        // end of read information


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
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);


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
                    Picasso.with(MainActivity.this).load(filePathURI).resize(500, 500).centerCrop().into(imageView);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                        saveImageUriToDB(filePathURI,database_w);
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
//        ImageTagDatabaseHelper.Initialize(this);
//        ImageTagDatabaseHelper dbHelper = ImageTagDatabaseHelper.GetInstance();
//
//        //  write information
//        SQLiteDatabase database_w;
//        database_w = dbHelper.getWritableDatabase();

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
        // end of write information

    }

    String[]  getImageUriFromDBAtIndex(int index, SQLiteDatabase db) {
//  read information
        String[] projection = {
                "Id",
                "ImageUri",
        };

        // Filter results WHERE "title" = 'My Title'
        String column_name_read_filter = "Id";
        String selection = column_name_read_filter + " = *";
        String[] selectionArgs = {""+index};

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

        String query = "SELECT * FROM "+tableName_read;

        Cursor  cursor = db.rawQuery(query,null);

        Log.i("cursor", "cursor !");
        ArrayList<String> itemIds = new ArrayList<String>();
        while (cursor.moveToNext()) {
           // long itemId = cursor.getLong(cursor.getColumnIndexOrThrow("Id"));
            //String ss = cursor.getString(cursor.getColumnIndexOrThrow("ImageUri"));
            String ss =  cursor.getString(cursor.getColumnIndex("ImageUri"));
            Log.i("cursor", "1cursor ImageUri = " + ss);
            itemIds.add(ss);
        }
        cursor.close();
        // end of read information
        String[] uris = itemIds.toArray(new String[itemIds.size()]);

//        for(int i = 0; i< uris.length;i++){
//            Log.i("cursor", "uris["+i+"] = " + uris[i]);
//        }

        return uris;
    }


}