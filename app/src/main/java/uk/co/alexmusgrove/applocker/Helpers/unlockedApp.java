package uk.co.alexmusgrove.applocker.Helpers;

import android.content.ContentValues;

import java.util.ArrayList;

import uk.co.alexmusgrove.applocker.Database.AppContentProvider;
import uk.co.alexmusgrove.applocker.Database.AppSQLiteDBHelper;
import uk.co.alexmusgrove.applocker.Services.appService;

public class unlockedApp {
    private String packageName;
    private long unlockedAt;
    private static final int DURATION = 180000; //180 Seconds, 3 Minutes

    private unlockedApp(String packageName) {
        this.packageName = packageName;
        this.unlockedAt = System.currentTimeMillis();
    }

    public unlockedApp(String packageName, long unlockedAt){
        this.packageName = packageName;
        this.unlockedAt = unlockedAt;
    }

    private boolean isUnlocked() {
        return ((unlockedAt + DURATION) > System.currentTimeMillis());
    }

    private String getPackageName() {
        return packageName;
    }

    private long getUnlockedAt() {
        return unlockedAt;
    }


    public static boolean isAppUnlocked(appService appService, String packageName) {
        ArrayList<unlockedApp> unlockedApps = AppSQLiteDBHelper.getAllUnlockedApps(appService);
        return unlockedApps.stream().anyMatch(s -> s.getPackageName().equals(packageName));
    }


    public static void addUnlockedApp(appService appService, String unlockedApp) {
        ContentValues values = new ContentValues();
        unlockedApp unlockedApp1 = new unlockedApp(unlockedApp);
        values.put(AppSQLiteDBHelper.COLUMN_PACKAGENAME, unlockedApp1.getPackageName());
        values.put(AppSQLiteDBHelper.COLUMN_UNLOCKEDAT, unlockedApp1.getUnlockedAt());
        appService.getContentResolver().insert(AppContentProvider.APP_CONTENT_URI, values);
    }

    public static void checkIfUnlockedAppExpired(appService appService, unlockedApp unlockedApp) {
        if (!unlockedApp.isUnlocked()) {
            appService.getContentResolver().delete(AppContentProvider.UNLOCKEDAPP_CONTENT_URI,
                    AppSQLiteDBHelper.COLUMN_PACKAGENAME + " = '" + unlockedApp.getPackageName() + "'",
                    null
            );
        }
    }
}
