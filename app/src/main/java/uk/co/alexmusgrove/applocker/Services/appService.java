package uk.co.alexmusgrove.applocker.Services;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import uk.co.alexmusgrove.applocker.Activities.lockActivity;
import uk.co.alexmusgrove.applocker.Database.AppContentProvider;
import uk.co.alexmusgrove.applocker.Database.AppSQLiteDBHelper;
import uk.co.alexmusgrove.applocker.Helpers.unlockedApp;

public class appService extends Service {

    private static final String TAG = "uk.co.alexmusgrove.applocker.Services";
    private static boolean onState = false;
    Intent testIntent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        if (!onState){
            onState = true;
            Log.i(TAG, "Service onCreate");
            //separate thread to call the service
            new Thread(() -> {
                while(true) {//recurring loop
                    ArrayList<unlockedApp> unlockedApps = AppSQLiteDBHelper.getAllUnlockedApps(this);
                    Log.i(TAG, "unlockedApps: " + unlockedApps.size());

                    try {
                        Thread.sleep(500);//delay the loop for every .5 of a second
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (unlockedApp i : unlockedApps){
                        if (!i.isUnlocked()){
                            getContentResolver().delete(AppContentProvider.UNLOCKEDAPP_CONTENT_URI,
                                    AppSQLiteDBHelper.COLUMN_PACKAGENAME + " = '" + i.getPackageName() + "'",
                                    null
                            );//if the app has expired the unlock, then delete the unlock
                            Log.i(TAG, "delete: " + i.getPackageName());
                        }
                    }
                    //REST OF CODE HERE//
                    String packageName = getForegroundPackageName();
                    Log.i(TAG, "ForegroundApp: " + getForegroundPackageName());
                    if (isMyAppRunning()){//check if it is in foreground
                        if (!isMyAppUnlocked(getForegroundPackageName())){// check app if it is not unlocked
                            Intent intent = new Intent(getApplicationContext(), lockActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("packageName", packageName);
                            startActivity(intent);//start lock with app data
                        }
                    }
                }
            }).start();
        }
    }

    private String getForegroundPackageName() {
        String currentApp = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) getSystemService(this.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                    time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();

                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(),
                            usageStats);
                }

                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(
                            mySortedMap.lastKey()).getPackageName();
                }
            }

        } else {
            ActivityManager am = (ActivityManager) getBaseContext().getSystemService(ACTIVITY_SERVICE);
            currentApp = am.getRunningTasks(1).get(0).topActivity .getPackageName();
        }

        if (currentApp == null){
            return "Could not find package name.";
        } else {
            return currentApp;
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service onStartCommand");
        testIntent = intent;
        Bundle extras = testIntent.getExtras();
        if (extras != null){
            String unlockedPackageName = extras.getString("unlockedApp");
            if (unlockedPackageName != null){
                addUnlockedApp(unlockedPackageName);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private boolean isMyAppRunning () {
        ArrayList<String> appList = AppSQLiteDBHelper.getAllApps(this);
        return appList.contains(getForegroundPackageName());
    }

    private boolean isMyAppUnlocked (String packageName) {
        ArrayList<unlockedApp> unlockedApps = AppSQLiteDBHelper.getAllUnlockedApps(this);
        for (unlockedApp i : unlockedApps){
            if (i.getPackageName().equals(packageName)){
                return true;
            }
        }
        return false;
    }

    public void addUnlockedApp (String unlockedApp) {
        ContentValues values = new ContentValues();
        unlockedApp unlockedApp1 = new unlockedApp(unlockedApp);
        values.put(AppSQLiteDBHelper.COLUMN_PACKAGENAME, unlockedApp1.getPackageName());
        values.put(AppSQLiteDBHelper.COLUMN_UNLOCKEDAT, unlockedApp1.getUnlockedAt());
        getContentResolver().insert(AppContentProvider.APP_CONTENT_URI, values);
    }
}
