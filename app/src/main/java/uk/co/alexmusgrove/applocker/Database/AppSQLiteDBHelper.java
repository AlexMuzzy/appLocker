package uk.co.alexmusgrove.applocker.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;

import uk.co.alexmusgrove.applocker.Helpers.unlockedApp;

public class AppSQLiteDBHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "appLocker_DB.db";

    public AppSQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //Constants for table and columns
    public static final String TABLE_APPLIST = "APP_TABLE";
    public static final String TABLE_UNLOCKEDAPPS = "UNLOCKEDAPPS_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_PACKAGENAME = "PACKAGE_NAME";
    public static final String COLUMN_UNLOCKEDAT = "UNLOCKED_AT";

    public static final String[] ALL_COLUMNS =
            {COLUMN_ID,COLUMN_PACKAGENAME};


    @Override
    public void onCreate(SQLiteDatabase db) {
        //SQL Statement to create app table
        String CREATE_APP_TABLE = "CREATE TABLE " + TABLE_APPLIST +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                " " + COLUMN_PACKAGENAME + " TEXT)";

        // create apps table
        String CREATE_UNLOCKEDAPPS_TABLE = "CREATE TABLE " + TABLE_UNLOCKEDAPPS +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                " " + COLUMN_PACKAGENAME + " TEXT," +
                " " + COLUMN_UNLOCKEDAT + " BIGINT)";
        db.execSQL(CREATE_APP_TABLE);
        db.execSQL(CREATE_UNLOCKEDAPPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older apps table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPLIST);

        // create fresh apps table
        this.onCreate(db);

    }

    public static ArrayList<String> getAllApps (Context context) {
        ArrayList<String> apps = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(AppContentProvider.APP_CONTENT_URI,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()){
            apps.add(cursor.getString(1));
        }
        return apps;
    }

    public static ArrayList<unlockedApp> getAllUnlockedApps (Context context) {
        ArrayList<unlockedApp> unlockedApps = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(AppContentProvider.UNLOCKEDAPP_CONTENT_URI,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()){
            unlockedApps.add(new unlockedApp(
                    cursor.getString(1),
                    cursor.getLong(2)
            ));
        }
        return unlockedApps;
    }
}
