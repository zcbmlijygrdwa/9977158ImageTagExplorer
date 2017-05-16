package zhenyuyang.cs190i.cs.ucsb.edu.a9977158imagetagexplorer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhenyu on 2017-05-14.
 */

public class ImageTagDatabaseHelper extends SQLiteOpenHelper {
    private static final String CreateImageTable = "CREATE TABLE Image (Id integer PRIMARY KEY AUTOINCREMENT, ImageUri text NOT NULL UNIQUE);";
    private static final String CreateTagTable = "CREATE TABLE Tag (Id integer PRIMARY KEY AUTOINCREMENT, Text text NOT NULL UNIQUE);";
    private static final String CreateLinkTable =
            "CREATE TABLE Link (ImageId integer, TagId integer, PRIMARY KEY (ImageId, TagId), " +
                    "FOREIGN KEY (ImageId) REFERENCES Image (Id) ON DELETE CASCADE ON UPDATE NO ACTION, " +
                    "FOREIGN KEY (TagId) REFERENCES Tag (Id) ON DELETE CASCADE ON UPDATE NO ACTION);";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + "TABLE_NAME" + " (" +
                    "id" + " INTEGER PRIMARY KEY," +
                    "COLUMN_NAME_TITLE "+ " TEXT," +
                    "COLUMN_NAME_SUBTITLE" + " TEXT);";

    private static final String DatabaseName = "ImageTagDatabase.db";
    private static ImageTagDatabaseHelper Instance;
    private List<OnDatabaseChangeListener> Listeners;

    private ImageTagDatabaseHelper(Context context) {
        super(context, DatabaseName, null, 1);
        Listeners = new ArrayList<>();
    }

    public static void Initialize(Context context) {
        Instance = new ImageTagDatabaseHelper(context);
    }

    public static ImageTagDatabaseHelper GetInstance() {
        return Instance;
    }

    public void Subscribe(OnDatabaseChangeListener listener) {
        Listeners.add(listener);
    }

    private boolean TryUpdate(Cursor cursor) {
        try {
            cursor.moveToFirst();
        } catch (SQLiteConstraintException exception) {
            return false;
        } finally {
            cursor.close();
        }
        NotifyListeners();
        return true;
    }

    private void NotifyListeners() {
        for (OnDatabaseChangeListener listener : Listeners) {
            listener.OnDatabaseChange();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("ImageTagDatabaseHelper", "onCreate");
        db.execSQL(CreateImageTable);
        db.execSQL(CreateTagTable);
        db.execSQL(CreateLinkTable);
        db.execSQL(SQL_CREATE_ENTRIES);
        Log.i("ImageTagDatabaseHelper", "db = " + db.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void ClearDB(SQLiteDatabase db){


            db.execSQL("DROP TABLE IF EXISTS " + "Image");
            db.execSQL("DROP TABLE IF EXISTS " + "Tag");
            db.execSQL("DROP TABLE IF EXISTS " + "Link");
            db.execSQL("DROP TABLE IF EXISTS " + "TABLE_NAME");
            onCreate(db);


    }

    public interface OnDatabaseChangeListener {
        void OnDatabaseChange();
    }
}
