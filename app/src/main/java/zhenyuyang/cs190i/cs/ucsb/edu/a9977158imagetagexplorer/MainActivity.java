package zhenyuyang.cs190i.cs.ucsb.edu.a9977158imagetagexplorer;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements EditNameDialogFragment.OnCompleteListener{
    static Random rnd = new Random();
    public static final int REQUEST_CAMERA = 111;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 112;
    //    String[] web = {
//            "Google"
//    } ;
    Uri[] imageUris;
    GridView grid;
    final int PICK_PHOTO_REQUEST = 5;
    final int TAKE_PHOTO_REQUEST = 4;
    static String[] StringsForAutoComplete;
    ImageTagDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageTagDatabaseHelper.Initialize(this);
        dbHelper = ImageTagDatabaseHelper.GetInstance();





// ===============    Tag recyclerView  Registration ===============

        final RecyclerView tagRecyclerView = (RecyclerView) findViewById(R.id.tag_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        tagRecyclerView.setLayoutManager(linearLayoutManager);
        //String [] tags_test = {"qwer","weewer","qwgrqeg3","gh","t42","my","342gr","3rvf","uizxcvo","qewdvs","qwefcavd"};
        final ArrayList<String> tags = new ArrayList<String>();
        TagRVAdapter tagAdapter = new TagRVAdapter(tags);
        tagRecyclerView.setAdapter(tagAdapter);

// ===============  End of   Tag recyclerView  Registration ===============








// ===============   autoCompleteTextView  Registration ===============


        StringsForAutoComplete = getALLTagsUriFromDB(dbHelper.getReadableDatabase());


        AutoCompleteTextView autocomplete = (AutoCompleteTextView)
                findViewById(R.id.autoCompleteTextView);

        ArrayAdapter<String> auto_complete_adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, StringsForAutoComplete);
        autocomplete.setAdapter(auto_complete_adapter);
        autocomplete.setThreshold(1);

        autocomplete.setOnEditorActionListener(
                new AutoCompleteTextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            Log.i("my", "TextView = " + v.getText().toString());
                            if (!v.getText().toString().equals("")) {
                                //tags.add(v.getText().toString());   //not adding
                                if (tags.size() == 0) {
                                    tags.add(v.getText().toString());
                                } else {
                                    tags.set(0, v.getText().toString());
                                }

                                //update tags RV
                                TagRVAdapter tagAdapter = new TagRVAdapter(tags);
                                tagRecyclerView.setAdapter(tagAdapter);
                                Log.i("my", "tags.size()-1 = " + (tags.size() - 1));

                                tagRecyclerView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tagRecyclerView.smoothScrollToPosition(tags.size() - 1);
                                    }
                                });
                                Uri[] LinkedUris = getLinkedDataFromDBByTag(v.getText().toString(), dbHelper.getReadableDatabase());  //test linked table
                                SelelctImageGrid adapter = new SelelctImageGrid(getApplicationContext(), LinkedUris);
                                grid = (GridView) findViewById(R.id.grid);
                                grid.setAdapter(adapter);

                                v.setText("");
                            }
                            return true;
                        }
                        return false;
                    }
                });
// ===============   End of autoCompleteTextView  Registration ===============







// ===============   floatingActionButton  Registration ===============


        FloatingActionButton fab_pickImage = (FloatingActionButton) findViewById(R.id.fab_pickImage);
        fab_pickImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("my", "FloatingActionButton");


//                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION), PICK_PHOTO_REQUEST);//one can be replaced with any action code


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {


                        // Should we show an explanation?
                        if (shouldShowRequestPermissionRationale(
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            // Explain to the user why we need to read the contacts
                            Log.i("my", "permission.READ_EXTERNAL_STORAGE");

                        }

                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant

                        return;
                    }
                    else{
                        Log.i("my", "permission.READ_EXTERNAL_STORAGE3");

                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION), PICK_PHOTO_REQUEST);//one can be replaced with any action code
                    }
                }


            }
        });

        FloatingActionButton fab_takePhoto = (FloatingActionButton) findViewById(R.id.fab_takePhoto);
        fab_takePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("my", "FloatingActionButton");



//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, TAKE_PHOTO_REQUEST);



                // Check permission for CAMERA
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Check Permissions Now
                    // Callback onRequestPermissionsResult interceptado na Activity MainActivity
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            MainActivity.REQUEST_CAMERA);
                } else {
                    // permission has been granted, continue as usual

                    Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(captureIntent, TAKE_PHOTO_REQUEST);
                }






            }
        });

// =============== End of  floatingActionButton  Registration ===============






// ===============   Button  Registration ===============

        Button button = (Button) findViewById(R.id.button_online);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                getAllOnlineResource();
                updateMainViewWithDB(dbHelper.getReadableDatabase());
            }
        });

        Button button_refresh = (Button) findViewById(R.id.button_refresh);
        button_refresh.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                updateMainViewWithDB(dbHelper.getReadableDatabase());
            }
        });

        Button button_clearDB = (Button) findViewById(R.id.button_clearDB);
        button_clearDB.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Log.i("ClearDB", "db = " + db);
                if (db != null) {
                    dbHelper.ClearDB(db);
                    Toast.makeText(getApplicationContext(), "Clear DB!", Toast.LENGTH_LONG).show();
                } else {

                    Toast.makeText(getApplicationContext(), "Database is empty!", Toast.LENGTH_LONG).show();
                }
            }
        });
// ===============  End of  Button  Registration ===============



// ===============   GridView  Registration ===============
        imageUris = getALLImageUriFromDB(dbHelper.getReadableDatabase());
        grid = (GridView) findViewById(R.id.grid);
        if (imageUris != null && !imageUris.equals("")) {
            SelelctImageGrid adapter = new SelelctImageGrid(this, imageUris);
            grid.setAdapter(adapter);
        }
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.i("addOnItemTouchListener", "onItemClick position =" + position);
                String[] clickedTagList = getTagsIndexByImageIndex(position, dbHelper.getReadableDatabase());
                String clickedText = imageUris[position].toString();
                Log.i("addOnItemTouchListener", "clickedText =" + clickedText);

                //After clicking an iterm, display dialog fragment
                FragmentManager fm = getFragmentManager();
                EditNameDialogFragment editNameDialogFragment = EditNameDialogFragment.newInstance("Detail", clickedText, clickedTagList, StringsForAutoComplete);
                editNameDialogFragment.show(fm, "fragment_edit_name");
            }
        });


// ===============  End of GridView  Registration ===============


    }

    void updateMainViewWithDB(SQLiteDatabase db) {
        imageUris = getALLImageUriFromDB(db);
        String [] testList = getALLTagsUriFromDB(db);

        StringsForAutoComplete = getALLTagsUriFromDB(dbHelper.getReadableDatabase());
        ArrayAdapter<String> auto_complete_adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, StringsForAutoComplete);
        AutoCompleteTextView autocomplete = (AutoCompleteTextView)
                findViewById(R.id.autoCompleteTextView);
        autocomplete.setAdapter(auto_complete_adapter);


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


//                saveImageUriToDB(selectedImageURI.toString(), dbHelper.getReadableDatabase());
//                saveTagUriToDB("Tag_iamge_picked", dbHelper.getWritableDatabase(),dbHelper.getReadableDatabase());
//                updateGridViewWithDB(dbHelper.getWritableDatabase());

                String clickedText = selectedImageURI.toString();
                String[] tagsList2 = {"qwer", "weewer", "qwgrqeg3", "gh", "t42", "my", "342gr", "3rvf", "uizxcvo", "qewdvs", "qwefcavd"};


                //display dialog fragment
                FragmentManager fm = getFragmentManager();
                EditNameDialogFragment editNameDialogFragment = EditNameDialogFragment.newInstance("Detail", clickedText, tagsList2, tagsList2);
                editNameDialogFragment.show(fm, "fragment_edit_name");
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
                    //Log.i("onActivityResult", "result44 = " + filePathURI.toString());

                    saveDataToDB(filePathURI.toString(),"camera",dbHelper.getWritableDatabase(), dbHelper.getReadableDatabase());

//                    int savedImageIndex = saveImageUriToDB(filePathURI.toString(), dbHelper.getWritableDatabase(), dbHelper.getReadableDatabase());
//                    int savedTagIndex = saveTagToDB("camera", dbHelper.getWritableDatabase(), dbHelper.getReadableDatabase());
//                    Log.i("onActivityResult", "savedImageIndex = "+savedImageIndex+", savedTagIndex = "+savedTagIndex);
//                    boolean status = saveLinkedDataToDB(savedImageIndex,savedTagIndex,dbHelper.getWritableDatabase());
//                    Log.i("onActivityResult", "saveLinkedDataToDB statu = "+status);


                    updateMainViewWithDB(dbHelper.getReadableDatabase());
                }
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.i("my", "permission requestCode = "+requestCode);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("my", "permission.READ_EXTERNAL_STORAGE2");

                    //to start activity after the first time asking for permission
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION), PICK_PHOTO_REQUEST);//one can be replaced with any action code
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Log.i("my", "permission.READ_EXTERNAL_STORAGE denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    void getAllOnlineResource() {
        int imageIndex = 0;

        // get the total number of resource first
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
                        int ImageID = saveImageUriToDB(filePathURI, database_w,database_r);
                        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                        Log.i("onTaggedImage", "getFileStreamPath = " + filePathURI);
                        updateMainViewWithDB(database_r);  //dynamically shows the downloaded images.
                        //Picasso.with(MainActivity.this).load(filePathURI).resize(500, 500).centerCrop().into(imageView);
                        // imageView.setImageBitmap(image.image);
                        StringBuilder tagList = new StringBuilder();
                        for (String p : image.tags) {
                            tagList.append(p + "\n");
                            int TagID = saveTagToDB(p, database_w, database_r);
                            saveLinkedDataToDB(ImageID, TagID, database_w);
                        }
                        // textView.setText(textView.getText() + "\n\n" + tagList.toString());
                    }
                }
            });
        }
        //database_w.close();   //????
    }


    int saveImageUriToDB(String uri_input, SQLiteDatabase database_w, SQLiteDatabase database_r) {
        Log.i("SQLiteDatabase", "database = " + database_w.toString());
        // Insert the new row, returning the primary key value of the new row
        String tableName_insert = "Image";

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        String column_name_insert = "ImageUri";
        values.put(column_name_insert, uri_input);
//        try {
//            long newRowId = database_w.insertOrThrow(tableName_insert, null, values);
//            Log.i("SQLiteDatabase", "newRowId = " + newRowId);
//            return (int) newRowId;
//        } catch (Exception e) {
//            Log.i("SQLiteDatabase", "Exception = " + e.toString());
//            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
//            return -1;

        try {
            long newRowId = database_w.insertOrThrow(tableName_insert, null, values);
            Log.i("SQLiteDatabase", "ImageUti saved = " + uri_input + ", index = " + newRowId);
            return (int) newRowId;
        } catch (Exception e) {
            //Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            int back = getTagsIndexByContent(uri_input, database_r);
            Log.i("SQLiteDatabase", "Exception,   " + uri_input + "   has a duplicated value in the database, get int back = " + back);
            Log.i("onActivityResult", "tag not saved, existing tag with index = "+back);
            return back;
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


    Uri[] getALLImageUriFromDB(SQLiteDatabase db) {
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

        Log.i("Read from DB", "getALLImageUriFromDB !");
        ArrayList<Uri> itemIds = new ArrayList<Uri>();
        while (cursor.moveToNext()) {
            // long itemId = cursor.getLong(cursor.getColumnIndexOrThrow("Id"));
            //String ss = cursor.getString(cursor.getColumnIndexOrThrow("ImageUri"));
            Uri ss = Uri.parse(cursor.getString(cursor.getColumnIndex("ImageUri")));
            //Log.i("Read from DB", "1cursor ImageUri = " + ss);
            itemIds.add(ss);
        }
        cursor.close();
        // end of read information
        Uri[] uris = itemIds.toArray(new Uri[itemIds.size()]);

        for(int i = 0; i< uris.length;i++){
            Log.i("Read from DB", "imageUri["+i+"] = " + uris[i]);
        }

        return uris;
    }


    int saveTagToDB(String tag_input, SQLiteDatabase database_w, SQLiteDatabase database_r) {
        Log.i("SQLiteDatabase", "database = " + database_w.toString());
        // Insert the new row, returning the primary key value of the new row
        String tableName_insert = "Tag";

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        String column_name_insert = "Text";
        values.put(column_name_insert, tag_input);
        try {
            long newRowId = database_w.insertOrThrow(tableName_insert, null, values);
            Log.i("SQLiteDatabase", "Tag saved = " + tag_input + ", index = " + newRowId);
            return (int) newRowId;
        } catch (Exception e) {
            //Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            int back = getTagsIndexByContent(tag_input, database_r);
            Log.i("SQLiteDatabase", "Exception,   " + tag_input + "   has a duplicated value in the database, get int back = " + back);
            Log.i("onActivityResult", "tag not saved, existing tag with index = "+back);
            return back;
        }
    }

    int getTagsIndexByContent(String text, SQLiteDatabase db) {
        String tableName_read = "Tag";
        String query = "SELECT * FROM " + tableName_read + " WHERE Tag.Text = '" + text + "'";
        Cursor cursor = db.rawQuery(query, null);
        Log.i("cursor", "getTagsIndexByContent!");
        int itemId = -2;
        while (cursor.moveToNext()) {
            itemId = (int) cursor.getLong(cursor.getColumnIndexOrThrow("Id"));
            Log.i("cursor", "cursor Text back = " + itemId);
        }
        cursor.close();
        return itemId;
    }

    int getImageIndexByContent(String text, SQLiteDatabase db) {
        String tableName_read = "Image";
        String query = "SELECT * FROM " + tableName_read + " WHERE Image.ImageUri = '" + text + "'";
        Cursor cursor = db.rawQuery(query, null);
        Log.i("cursor", "cgetImageIndexByContent!");
        int itemId = -2;
        while (cursor.moveToNext()) {
            itemId = (int) cursor.getLong(cursor.getColumnIndexOrThrow("Id"));
            Log.i("cursor", "cursor ImageUri back = " + itemId);
        }
        cursor.close();
        return itemId;
    }


    String[] getTagsIndexByImageIndex(int index, SQLiteDatabase database_r) {
//  read information
        String[] projection = {
                "Id",
                "Text",
        };

        // Filter results WHERE "title" = 'My Title'
        String column_name_read_filter = "Id";
        String selection = column_name_read_filter + " = *";


// How you want the results sorted in the resulting Cursor
        String sortOrder =
                "Id" + " DESC";
        String tableName_read = "Link";
        String query = "SELECT * FROM " + tableName_read + "  INNER JOIN Tag ON Link.TagId =  Tag.Id WHERE Link.ImageID = " + (index + 1) + "";  //plus 1 to fix the index issue
        String query2 = "SELECT *  " +
                "FROM Image " +
                "WHERE Tag.Id=TagId";
        Cursor cursor = database_r.rawQuery(query, null);

        Log.i("cursor", "cursor Linked !");
        ArrayList<String> itemIds = new ArrayList<String>();
        while (cursor.moveToNext()) {
            // long itemId = cursor.getLong(cursor.getColumnIndexOrThrow("Id"));
            String ss = cursor.getString(cursor.getColumnIndexOrThrow("Text"));
            //Uri ss = Uri.parse(cursor.getString(cursor.getColumnIndex("Text")));
            //Log.i("cursor", "cursor Text = " + ss);
            itemIds.add(ss);
        }
        cursor.close();
        // end of read information
        String[] Tags = itemIds.toArray(new String[itemIds.size()]);

        for (int i = 0; i < Tags.length; i++) {
            Log.i("Linked", "Linked results Tags[" + i + "] = " + Tags[i]);
        }
        return Tags;
    }


    String[] getALLTagsUriFromDB(SQLiteDatabase db) {
//  read information
        String[] projection = {
                "Id",
                "Text",
        };

        // Filter results WHERE "title" = 'My Title'
        String column_name_read_filter = "Id";
        String selection = column_name_read_filter + " = *";


// How you want the results sorted in the resulting Cursor
        String sortOrder =
                "Id" + " DESC";
        String tableName_read = "Tag";
        String query = "SELECT * FROM " + tableName_read;

        Cursor cursor = db.rawQuery(query, null);

        Log.i("Read from DB", "getALLTagsUriFromDB !");
        ArrayList<String> itemIds = new ArrayList<String>();
        while (cursor.moveToNext()) {
            // long itemId = cursor.getLong(cursor.getColumnIndexOrThrow("Id"));
            //String ss = cursor.getString(cursor.getColumnIndexOrThrow("ImageUri"));
            String ss = (cursor.getString(cursor.getColumnIndex("Text")));
            //Log.i("cursor", "cursor Text = " + ss);
            itemIds.add(ss);
        }
        cursor.close();
        // end of read information
        String[] Tags = itemIds.toArray(new String[itemIds.size()]);

        for (int i = 0; i < Tags.length; i++) {
            Log.i("Read from DB", "Tags[" + i + "] = " + Tags[i]);
        }

        return Tags;
    }

    //linked table
    private static final String CreateLinkTable =
            "CREATE TABLE Link (ImageId integer, TagId integer, PRIMARY KEY (ImageId, TagId), " +
                    "FOREIGN KEY (ImageId) REFERENCES Image (Id) ON DELETE CASCADE ON UPDATE NO ACTION, " +
                    "FOREIGN KEY (TagId) REFERENCES Tag (Id) ON DELETE CASCADE ON UPDATE NO ACTION);";


    boolean saveLinkedDataToDB(int ImageId, int TagId, SQLiteDatabase database_w) {
        Log.i("SQLiteDatabase", "database = " + database_w.toString());
        // Insert the new row, returning the primary key value of the new row
        String tableName_insert = "Link";

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        String column_name_insert = "ImageId";
        values.put(column_name_insert, ImageId);

        column_name_insert = "TagId";
        values.put(column_name_insert, TagId);


        try {
            long newRowId = database_w.insertOrThrow(tableName_insert, null, values);
            Log.i("SQLiteDatabase", "ImageId saved = " + ImageId + ", TagId saved " + TagId + ", newRowId for linked = " + newRowId);
            return true;
        } catch (Exception e) {
            Log.i("SQLiteDatabase", "Exception,  saveLinkedDataToDB  has a duplicated value in the database");

            //Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            return false;
        }
    }


    Uri[] getLinkedDataFromDBByTag(String filterTag, SQLiteDatabase database_r) {
//  read information
        String[] projection = {
                "Id",
                "Text",
        };

        // Filter results WHERE "title" = 'My Title'
        String column_name_read_filter = "Id";
        String selection = column_name_read_filter + " = *";


// How you want the results sorted in the resulting Cursor
        String sortOrder =
                "Id" + " DESC";
        String tableName_read = "Link";
        String query = "SELECT * FROM " + tableName_read + "  INNER JOIN Tag ON Link.TagId =  Tag.Id INNER JOIN Image ON Link.ImageID =  Image.Id  WHERE Tag.Text = '" + filterTag + "'";
        String query2 = "SELECT *  " +
                "FROM Image " +
                "WHERE Tag.Id=TagId";
        Cursor cursor = database_r.rawQuery(query, null);

        Log.i("cursor", "cursor Linked !");
        ArrayList<Uri> itemIds = new ArrayList<Uri>();
        while (cursor.moveToNext()) {
            // long itemId = cursor.getLong(cursor.getColumnIndexOrThrow("Id"));
            //String ss = cursor.getString(cursor.getColumnIndexOrThrow("ImageUri"));
            Uri ss = Uri.parse(cursor.getString(cursor.getColumnIndex("ImageUri")));
            //Log.i("cursor", "cursor Text = " + ss);
            itemIds.add(ss);
        }
        cursor.close();
        // end of read information
        Uri[] Uris = itemIds.toArray(new Uri[itemIds.size()]);

        for (int i = 0; i < Uris.length; i++) {
            Log.i("Linked", "Linked results Uris[" + i + "] = " + Uris[i]);
        }
        return Uris;
    }


    boolean saveDataToDB(String uri_input,String tag_input, SQLiteDatabase database_w, SQLiteDatabase database_r){

        int savedImageIndex = saveImageUriToDB(uri_input, dbHelper.getWritableDatabase(), dbHelper.getReadableDatabase());
        int savedTagIndex = saveTagToDB(tag_input, dbHelper.getWritableDatabase(), dbHelper.getReadableDatabase());
        Log.i("onActivityResult", "savedImageIndex = "+savedImageIndex+", savedTagIndex = "+savedTagIndex);
        boolean status = saveLinkedDataToDB(savedImageIndex,savedTagIndex,dbHelper.getWritableDatabase());
        Log.i("onActivityResult", "saveLinkedDataToDB statu = "+status);

        return status;
    }


    @Override
    public void onComplete(String imageuri, String [] taglist) {
        Log.i("Frag_CB", "onComplete imageuri = "+imageuri);

        for(int i = 0; i <taglist.length;i++){
            Log.i("Frag_CB", "onComplete taglist["+i+"] = "+taglist[i]);
        }
    }
}