package zhenyuyang.cs190i.cs.ucsb.edu.a9977158imagetagexplorer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Zhenyu on 2017-05-15.
 */


public class YourTask extends AsyncTask<Object,Object,Object> { //change Object to required type




    public interface OnTaskCompleted{
        void onTaskCompleted(Uri[] u);
    }

    Uri[] imageUris2 ;
    private OnTaskCompleted listener;

    public YourTask(OnTaskCompleted listener){
        this.listener=listener;
    }

    // required methods

    @Override
    protected Object doInBackground(Object... params) {
        imageUris2 = getImageUriFromDB((SQLiteDatabase)params[0]);
        return null;
    }

    protected void onPostExecute(Object o){
        // your stuff
        listener.onTaskCompleted(imageUris2);
    }


    Uri[]  getImageUriFromDB( SQLiteDatabase db) {
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

        String query = "SELECT * FROM "+tableName_read;

        Cursor cursor = db.rawQuery(query,null);

        Log.i("cursor", "cursor !");
        ArrayList<Uri> itemIds = new ArrayList<Uri>();
        while (cursor.moveToNext()) {
            // long itemId = cursor.getLong(cursor.getColumnIndexOrThrow("Id"));
            //String ss = cursor.getString(cursor.getColumnIndexOrThrow("ImageUri"));
            Uri ss =  Uri.parse(cursor.getString(cursor.getColumnIndex("ImageUri")));
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
