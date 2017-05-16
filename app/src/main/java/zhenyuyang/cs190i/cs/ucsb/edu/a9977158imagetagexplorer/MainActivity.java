package zhenyuyang.cs190i.cs.ucsb.edu.a9977158imagetagexplorer;

import android.app.Activity;
import android.app.FragmentManager;
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

public class MainActivity extends AppCompatActivity {
    static Random rnd = new Random();
    //    String[] web = {
//            "Google"
//    } ;
    Uri[] imageUris;
    GridView grid;
    final int PICK_PHOTO_REQUEST = 5;
    final int TAKE_PHOTO_REQUEST = 4;

    ImageTagDatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageTagDatabaseHelper.Initialize(this);
        dbHelper = ImageTagDatabaseHelper.GetInstance();


        //  Tag recyclerView

        final RecyclerView tagRecyclerView = (RecyclerView) findViewById(R.id.tag_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        tagRecyclerView.setLayoutManager(linearLayoutManager);
        //String [] tags = {"qwer","weewer","qwgrqeg3","gh","t42","my","342gr","3rvf","uizxcvo","qewdvs","qwefcavd"};
        final ArrayList<String> tags= new  ArrayList<String>();
        TagRVAdapter tagAdapter = new TagRVAdapter(tags);
        tagRecyclerView.setAdapter(tagAdapter);
        // End of Tag recyclerView



        // autocompletetextview



        String[] StringsForAutoComplete= getALLTagsUriFromDB(dbHelper.getReadableDatabase());






        AutoCompleteTextView autocomplete = (AutoCompleteTextView)
                findViewById(R.id.autoCompleteTextView);

        ArrayAdapter<String> auto_complete_adapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item, StringsForAutoComplete);

        autocomplete.setThreshold(1);
        autocomplete.setAdapter(auto_complete_adapter);
        autocomplete.setOnEditorActionListener(
                new AutoCompleteTextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            Log.i("my", "TextView = "+v.getText().toString());
                            if(!v.getText().toString().equals("")) {
                                //tags.add(v.getText().toString());   //not adding
                                if(tags.size()==0){
                                    tags.add(v.getText().toString());
                                }
                                else{
                                    tags.set(0,v.getText().toString());
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
                                Uri[] LinkedUris = getLinkedDataFromDBByTag(v.getText().toString(),dbHelper.getReadableDatabase());  //test linked table
                                SelelctImageGrid adapter = new SelelctImageGrid(getApplicationContext(), LinkedUris);
                                grid = (GridView) findViewById(R.id.grid);
                                grid.setAdapter(adapter);

                                v.setText("");
                            }


                            //StringsForAutoComplete
//                            InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            return true;
                        }
                        return false;
                    }
                });
        // end of autocompletetextview


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



        imageUris = getALLImageUriFromDB(dbHelper.getReadableDatabase());
        //Uri[] LinkedUris = getLinkedDataFromDBByTag("cute",dbHelper.getReadableDatabase());  //test linked table


        String [] test = getALLTagsUriFromDB(dbHelper.getReadableDatabase());




        Button button_clearDB = (Button) findViewById(R.id.button_clearDB);
        button_clearDB.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Log.i("ClearDB", "db = "+db);
                if(db!=null){
                dbHelper.ClearDB(db);
                Toast.makeText(getApplicationContext(), "Clear DB!", Toast.LENGTH_LONG).show();
                }
                else{

                    Toast.makeText(getApplicationContext(), "Database is empty!", Toast.LENGTH_LONG).show();
                }
            }
        });




        // ==============    GridView   =====================
        SelelctImageGrid adapter = new SelelctImageGrid(this, imageUris);
        grid = (GridView) findViewById(R.id.grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.i("addOnItemTouchListener", "onItemClick position =" + position);


                String [] clickedTagList =  getTagsIndexByImageIndex(position, dbHelper.getReadableDatabase());

                String clickedText = imageUris[position].toString();
                Log.i("addOnItemTouchListener", "clickedText =" + clickedText);
                //dialog fragment
                FragmentManager fm = getFragmentManager();
                EditNameDialogFragment editNameDialogFragment = EditNameDialogFragment.newInstance("Detail",clickedText,clickedTagList);
                editNameDialogFragment.show(fm, "fragment_edit_name");



            }
        });




// ==============   End of GridView   =====================



    }

    public void updateGridView(){

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
                saveTagUriToDB("Tag_iamge_picked", dbHelper.getWritableDatabase(),dbHelper.getReadableDatabase());
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
                    saveTagUriToDB("Tag_camera_captured", dbHelper.getWritableDatabase(),dbHelper.getReadableDatabase());
                    updateGridViewWithDB(dbHelper.getWritableDatabase());
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
                        int ImageID = saveImageUriToDB(filePathURI, database_w);
                        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                        Log.i("onTaggedImage", "getFileStreamPath = " + filePathURI);
                        updateGridViewWithDB(database_r);  //dynamically shows the downloaded images.
                        //Picasso.with(MainActivity.this).load(filePathURI).resize(500, 500).centerCrop().into(imageView);
                        // imageView.setImageBitmap(image.image);
                        StringBuilder tagList = new StringBuilder();
                        for (String p : image.tags) {
                            tagList.append(p + "\n");
                            int TagID = saveTagUriToDB(p, database_w,database_r);
                            saveLinkedDataToDB(ImageID,TagID,database_w);
                        }
                        // textView.setText(textView.getText() + "\n\n" + tagList.toString());
                    }
                }
            });
        }
        //database_w.close();   //????
    }


    int saveImageUriToDB(String uri_input, SQLiteDatabase database_w) {
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
            return (int)newRowId;
        } catch (Exception e) {
            Log.i("SQLiteDatabase", "Exception = " + e.toString());
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            return -1;
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




    int saveTagUriToDB(String tag_input, SQLiteDatabase database_w, SQLiteDatabase database_r) {
        Log.i("SQLiteDatabase", "database = " + database_w.toString());
        // Insert the new row, returning the primary key value of the new row
        String tableName_insert = "Tag";

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        String column_name_insert = "Text";
        values.put(column_name_insert, tag_input);
        try {
            long newRowId = database_w.insertOrThrow(tableName_insert, null, values);
            Log.i("SQLiteDatabase", "Tag saved = "+tag_input+", newRowId for tag = " + newRowId);
            return (int)newRowId;
        } catch (Exception e) {


            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            int back = getTagsIndexByContent(tag_input,database_r);
            Log.i("SQLiteDatabase", "Exception,   " + tag_input+"   has a duplicated value in the database, get int back = "+back);
            return back;
        }
    }

    int getTagsIndexByContent(String text, SQLiteDatabase db) {
//  read information
        String[] projection = {
                "Id",
                "Text",
        };

        // Filter results WHERE "title" = 'My Title'
        String column_name_read_filter = "Id";
        String selection = column_name_read_filter + " = *";
        String[] selectionArgs = {text};

//// How you want the results sorted in the resulting Cursor
//        String sortOrder =
//                "Id" + " DESC";
        String tableName_read = "Tag";
        Log.i("cursor", "text = " + text);
        String query = "SELECT * FROM " + tableName_read+" WHERE Tag.Text = '"+text+"'";

        Cursor cursor = db.rawQuery(query, null);

        Log.i("cursor", "cursor Text !");
        ArrayList<String> itemIds = new ArrayList<String>();
        int itemId = -2;
        while (cursor.moveToNext()) {
              itemId = (int)cursor.getLong(cursor.getColumnIndexOrThrow("Id"));
            //String ss = cursor.getString(cursor.getColumnIndexOrThrow("ImageUri"));
            //String ss = (cursor.getString(cursor.getColumnIndex("Id")));
            Log.i("cursor", "cursor Text back = " + itemId);
           // itemIds.add(ss);
        }
        cursor.close();
//        // end of read information
//        String[] Tags = itemIds.toArray(new String[itemIds.size()]);
//
//        for(int i = 0; i< Tags.length;i++){
//            Log.i("Tags", "Tags["+i+"] = " + Tags[i]);
//        }

        return itemId;
    }


    String [] getTagsIndexByImageIndex(int index, SQLiteDatabase database_r) {
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
        String query= "SELECT * FROM " + tableName_read+"  INNER JOIN Tag ON Link.TagId =  Tag.Id WHERE Link.ImageID = "+(index+1)+"";  //minus 1 to fix the index issue
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

        for(int i = 0; i< Tags.length;i++){
            Log.i("Linked", "Linked results Tags["+i+"] = " + Tags[i]);
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

        Log.i("cursor", "cursor Text !");
        ArrayList<String> itemIds = new ArrayList<String>();
        while (cursor.moveToNext()) {
            // long itemId = cursor.getLong(cursor.getColumnIndexOrThrow("Id"));
            //String ss = cursor.getString(cursor.getColumnIndexOrThrow("ImageUri"));
            String ss = (cursor.getString(cursor.getColumnIndex("Text")));
            Log.i("cursor", "cursor Text = " + ss);
            itemIds.add(ss);
        }
        cursor.close();
        // end of read information
        String[] Tags = itemIds.toArray(new String[itemIds.size()]);

        for(int i = 0; i< Tags.length;i++){
            Log.i("Tags", "Tags["+i+"] = " + Tags[i]);
        }

        return Tags;
    }

    //linked table
    private static final String CreateLinkTable =
            "CREATE TABLE Link (ImageId integer, TagId integer, PRIMARY KEY (ImageId, TagId), " +
                    "FOREIGN KEY (ImageId) REFERENCES Image (Id) ON DELETE CASCADE ON UPDATE NO ACTION, " +
                    "FOREIGN KEY (TagId) REFERENCES Tag (Id) ON DELETE CASCADE ON UPDATE NO ACTION);";



    void saveLinkedDataToDB(int ImageId,int TagId , SQLiteDatabase database_w) {
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
            Log.i("SQLiteDatabase", "ImageId saved = "+ImageId+", TagId saved "+TagId+", newRowId for linked = " + newRowId);

        } catch (Exception e) {
            Log.i("SQLiteDatabase", "Exception,  saveLinkedDataToDB  has a duplicated value in the database");

            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }


    Uri [] getLinkedDataFromDBByTag( String filterTag, SQLiteDatabase database_r) {
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
        String query= "SELECT * FROM " + tableName_read+"  INNER JOIN Tag ON Link.TagId =  Tag.Id INNER JOIN Image ON Link.ImageID =  Image.Id  WHERE Tag.Text = '"+filterTag+"'";
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

        for(int i = 0; i< Uris.length;i++){
            Log.i("Linked", "Linked results Uris["+i+"] = " + Uris[i]);
        }
        return Uris;
    }





}