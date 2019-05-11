package uk.co.alexmusgrove.applocker.Services;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import uk.co.alexmusgrove.applocker.Activities.MainActivity;
import uk.co.alexmusgrove.applocker.Activities.lockActivity;
import uk.co.alexmusgrove.applocker.Database.AppSQLiteDBHelper;
import uk.co.alexmusgrove.applocker.Helpers.unlockedApp;

public class appService extends Service {

    private static final String TAG = "uk.co.alexmusgrove.applocker.Services";
    private static boolean onState = false;
    Intent testIntent;

    ArrayList<unlockedApp> unlockedApps = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "unlockedApps: " + unlockedApps.size());
        if (!onState){
            onState = true;
            Log.i(TAG, "Service onCreate");
            new Thread(() -> {
                while(true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (unlockedApp i : unlockedApps){
                        if (!i.isUnlocked()){
                            unlockedApps.remove(i);
                        }
                    }
                    //REST OF CODE HERE//
                    String packageName = getForegroundPackageName();
                    Log.i(TAG, "ForegroundApp: " + getForegroundPackageName());
                    if (isMyAppRunning()){
                        if (!isMyAppUnlocked(getForegroundPackageName())){
                            Intent intent = new Intent(getApplicationContext(), lockActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("packageName", packageName);
                            startActivity(intent);
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
                unlockedApps.add(new unlockedApp(unlockedPackageName));
                Log.i(TAG, "addedUnlockedApp: " + unlockedPackageName + ", new size is: " + unlockedApps.size());
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private boolean isMyAppRunning () {
        ArrayList<String> appList = AppSQLiteDBHelper.getAllApps(this);
        return appList.contains(getForegroundPackageName());
    }

    private boolean isMyAppUnlocked (String packageName) {
        for (unlockedApp i : unlockedApps){
            if (i.getPackageName().equals(packageName)){
                return true;
            }
        }
        return false;
    }
}
