package uk.co.alexmusgrove.applocker.Database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

import uk.co.alexmusgrove.applocker.Helpers.appItem;

public class AppSQLiteDBHelper extends SQLiteOpenHelper {

//    private ContentResolver myCR;

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "appLocker_DB.db";

    public AppSQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        myCR = context.getContentResolver();
    }

    //Constants for table and columns
    public static final String TABLE_APPLIST = "APP_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_PACKAGENAME = "PACKAGE_NAME";

    public static final String[] ALL_COLUMNS =
            {COLUMN_ID,COLUMN_PACKAGENAME};


    @Override
    public void onCreate(SQLiteDatabase db) {
        //SQL Statement to create app table
        String CREATE_APP_TABLE = "CREATE TABLE " + TABLE_APPLIST +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                " " + COLUMN_PACKAGENAME + " TEXT)";

        // create apps table
        db.execSQL(CREATE_APP_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older apps table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPLIST);

        // create fresh apps table
        this.onCreate(db);

    }
}
