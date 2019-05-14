package uk.co.alexmusgrove.applocker.Database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class AppContentProvider extends ContentProvider {
    private AppSQLiteDBHelper dbHelper;

    private static final String AUTHORITY = "uk.co.alexmusgrove.applocker.Database.ContentProvider";
    private static final String BASE_PATH = "provider";
    private static final String UNLOCKED_BASE_PATH = "unlockedProvider";

    //create content URIs from the authority be appending path to database table
    public static final Uri APP_CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    public static final Uri UNLOCKEDAPP_CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + UNLOCKED_BASE_PATH);

    private static final int APPS = 1;
    private static final int APP_ID = 2;
    private static final int UNLOCKEDAPPS = 3;
    private static final int UNLOCKEDAPP_ID = 4;

    private static HashMap<String, String> APPS_PROJECTION_MAP;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, APPS);
        uriMatcher.addURI(AUTHORITY,BASE_PATH + "/#", APP_ID);
        uriMatcher.addURI(AUTHORITY, UNLOCKED_BASE_PATH, UNLOCKEDAPPS);
        uriMatcher.addURI(AUTHORITY,UNLOCKED_BASE_PATH + "/#", UNLOCKEDAPP_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        AppSQLiteDBHelper helper = new AppSQLiteDBHelper(getContext());
        database = helper.getWritableDatabase();
        return (database==null)? false:true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)){
            case APPS:
            case APP_ID:
                qb.setTables(AppSQLiteDBHelper.TABLE_APPLIST);
                break;
            case UNLOCKEDAPPS:
            case UNLOCKEDAPP_ID:
                qb.setTables(AppSQLiteDBHelper.TABLE_UNLOCKEDAPPS);
                break;
        }
        switch (uriMatcher.match(uri)) {
            case APPS:
            case UNLOCKEDAPPS:
                qb.setProjectionMap(APPS_PROJECTION_MAP);
                break;
            case APP_ID:
            case UNLOCKEDAPP_ID:
                qb.appendWhere( "_id" + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("This is an unknown URI");
        }
        Cursor cursor = qb.query(database, projection, selection, selectionArgs, null, null, null);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }
    //Passing the following query to which will return a cursor object.

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)){
            case APPS:
            case UNLOCKEDAPPS:
                return "vnd.android.cursor.dir/vnd.applocker.Database.AppContentProvider";
            case APP_ID:
            case UNLOCKEDAPP_ID:
                return "vnd.android.cursor.item/vnd.applocker.Database.AppContentProvider";
            default:
                throw new IllegalArgumentException("This is an unknown URI");
        }
    }
    //returns a MIME type string

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch(uriMatcher.match(uri)){
            case APPS:
                    long id = database.insert(AppSQLiteDBHelper.TABLE_APPLIST,null, values);
                    if (id > 0){
                        Uri _uri = ContentUris.withAppendedId(APP_CONTENT_URI, id);
                        getContext().getContentResolver().notifyChange(_uri, null);
                        return _uri;
                    }
            case UNLOCKEDAPPS:
                long unlockedid = database.insert(AppSQLiteDBHelper.TABLE_UNLOCKEDAPPS,null, values);
                if (unlockedid > 0){
                    Uri _uri = ContentUris.withAppendedId(UNLOCKEDAPP_CONTENT_URI, unlockedid);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
        }
        throw new IllegalArgumentException("This is an unknown URI");
    }
    //takes uri, contentValues and returns a uri of newly inserted item.

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int delCount = 0;
        switch (uriMatcher.match(uri)) {
            case APPS:
                delCount = database.delete(AppSQLiteDBHelper.TABLE_APPLIST, selection, selectionArgs);
                break;
            case APP_ID:
                delCount = database.delete(AppSQLiteDBHelper.TABLE_APPLIST,
                        "_id=" + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection)
                                        ? " AND (" + selection + ")"
                                        : ""
                                ),
                        selectionArgs
                        );
                break;
            case UNLOCKEDAPPS:
                delCount = database.delete(AppSQLiteDBHelper.TABLE_UNLOCKEDAPPS, selection, selectionArgs);
                break;
            case UNLOCKEDAPP_ID:
                delCount = database.delete(AppSQLiteDBHelper.TABLE_UNLOCKEDAPPS,
                        "_id=" + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection)
                                        ? " AND (" + selection + ")"
                                        : ""
                                ),
                        selectionArgs
                );
                break;
            default:
                throw new IllegalArgumentException("This is an unknown URI");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return delCount;
    }
    //returns the number of rows that are affected by the delete method.

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int updCount = 0;
        switch (uriMatcher.match(uri)) {
            case APPS:
                updCount = database.update(AppSQLiteDBHelper.TABLE_APPLIST, values, selection, selectionArgs);
                break;
            case UNLOCKEDAPPS:
                updCount = database.update(AppSQLiteDBHelper.TABLE_UNLOCKEDAPPS, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("This is an unknown URI");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return updCount;
    }
    //returns the number of rows that are affected by the update method.
}
