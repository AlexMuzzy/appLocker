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

import java.util.HashMap;

public class AppContentProvider extends ContentProvider {
    private AppSQLiteDBHelper dbHelper;

    private static final String AUTHORITY = "uk.co.alexmusgrove.applocker.Database.AppContentProvider";
    private static final String BASE_PATH = "provider";

    //create content URIs from the authority be appending path to database table
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    private static final int APPS = 1;
    private static final int APP_ID = 2;

    private static HashMap<String, String> APPS_PROJECTION_MAP;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY,BASE_PATH, APPS);
        uriMatcher.addURI(AUTHORITY,BASE_PATH + "/#", APP_ID);
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
        qb.setTables(AppSQLiteDBHelper.TABLE_APPLIST);
        switch (uriMatcher.match(uri)) {
            case APPS:
                qb.setProjectionMap(APPS_PROJECTION_MAP);
                break;

            case APP_ID:
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
                return "vnd.android.cursor.dir/vnd.applocker.Database.AppContentProvider";
            case APP_ID:
                return "vnd.android.cursor.item/vnd.applocker.Database.AppContentProvider";
            default:
                throw new IllegalArgumentException("This is an unknown URI");
        }
    }
    //returns a MIME type string

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id = database.insert(AppSQLiteDBHelper.TABLE_APPLIST,null, values);
        if (id > 0){
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, id);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
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
            default:
                throw new IllegalArgumentException("This is an unknown URI");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return updCount;
    }
    //returns the number of rows that are affected by the update method.
}
