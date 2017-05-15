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
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    static Random rnd = new Random();
    //    String[] web = {
//            "Google"
//    } ;
    Uri[] imageUris;
    GridView grid;
    final int PICK_PHOTO_REQUEST = 5;
    final int TAKE_PHOTO_REQUEST = 4;

    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo0000.jpg";


    ImageTagDatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageTagDatabaseHelper.Initialize(this);
        dbHelper = ImageTagDatabaseHelper.GetInstance();

// register floatingActionButton


        FloatingActionButton fab_pickImage = (FloatingActionButton) findViewById(R.id.fab_pickImage);
        fab_pickImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("my", "FloatingActionButton");
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) , PICK_PHOTO_REQUEST);//one can be replaced with any action code
            }
        });

        FloatingActionButton fab_takePhoto = (FloatingActionButton) findViewById(R.id.fab_takePhoto);
        fab_takePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("my", "FloatingActionButton");
                //Intent cameraI = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);


                /*
// Whenever you’d like to launch your camera, do this:
// create Intent to take a picture and return control to the calling application
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName));
// set the image file name
// If you call startActivityForResult() using an intent that no app can handle, your app willcrash.
// So as long as the result is not null, it's safe to use the intent.
                if (intent.resolveActivity(getPackageManager()) != null) {
// Start the image capture intent to take photo
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }

*/


                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, TAKE_PHOTO_REQUEST);
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


//        int index = 2; //0~10
//        TaggedImageRetriever.getTaggedImageByIndex(index, new TaggedImageRetriever.TaggedImageResultListener() {
//            @Override
//            public void onTaggedImage(TaggedImageRetriever.TaggedImage image) {
//                if (image != null) {
//                    try (FileOutputStream stream = openFileOutput("Test.jpg", Context.MODE_PRIVATE)) {
//                        image.image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                        image.image.recycle();
//                    } catch (IOException e) {
//                    }
//
//                    String filePathURI = Uri.fromFile(getFileStreamPath("Test.jpg")).toString();
//                    Log.i("onTaggedImage", "getFileStreamPath(\"Test.jpg\") = " + filePathURI);
//                    // Picasso.with(MainActivity.this).load(filePathURI).resize(500, 500).centerCrop().into(imageView);
//                    // imageView.setImageBitmap(image.image);
//                    StringBuilder tagList = new StringBuilder();
//                    for (String p : image.tags) {
//                        tagList.append(p + "\n");
//                    }
//                    textView.setText(textView.getText() + "\n\n" + tagList.toString());
//                }
//            }
//
//            @Override
//            public void onTaggedImage(TaggedImageRetriever.TaggedImage image, int index) {
//
//            }
//        });


    }

    // Returns the Uri for a photo stored on disk given the fileName
    public Uri getPhotoFileUri(String fileName) {
// Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {
// Get safe storage directory for photos
// Use `getExternalFilesDir` on Context to access package-specific directories.
// This way, we don't need to request external read/write runtime permissions.
            File mediaStorageDir = new
                    File( getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
// Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(APP_TAG, "failed to create directory");
            }
// Return the file target for the photo based on filename
            File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
// wrap File object into a content provider, required for API >= 24
            return FileProvider.getUriForFile(MainActivity.this, "com.codepath.fileprovider", file);
        }
        return null; }

    // Returns true if external storage for photos is available
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
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


        if (requestCode == PICK_PHOTO_REQUEST) {
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

        if (requestCode == TAKE_PHOTO_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

               //    Log.i("onActivityResult", "getData = " + data.getExtras().toString());
                Bitmap image = (Bitmap) data.getExtras().get("data");

                if (image != null) {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String fileName = "camera_captured_" + timeStamp + ".jpg";
                    try (FileOutputStream stream = openFileOutput(fileName, Context.MODE_PRIVATE)) {
                        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    } catch (IOException e) {
                    }

                    String filePathURI = Uri.fromFile(getFileStreamPath(fileName)).toString();
                    //Uri selectedImageURI = data.getData();
                    Log.i("onActivityResult", "result44 = " + filePathURI.toString());
                    saveImageUriToDB(filePathURI.toString(), dbHelper.getReadableDatabase());
                    updateGridViewWithDB(dbHelper.getWritableDatabase());
                }




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


        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri takenPhotoUri = getPhotoFileUri(photoFileName);
                Log.i("onActivityResult", "takenPhotoUri2 = "+takenPhotoUri);
// by this point we have the camera photo on disk
                //Bitmap takenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
// RESIZE BITMAP (if desired)
// Load the taken image into a preview
               // ImageView ivPreview = (ImageView) findViewById(R.id.ivPreview);
                //ivPreview.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
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
        final SQLiteDatabase database_r = dbHelper.getReadableDatabase();
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
                        updateGridViewWithDB(database_r);  //dynamically shows the downloaded images.
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