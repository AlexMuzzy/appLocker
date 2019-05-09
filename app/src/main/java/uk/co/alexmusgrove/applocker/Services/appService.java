package uk.co.alexmusgrove.applocker.Services;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
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

public class appService extends Service {

    private static final String TAG = "uk.co.alexmusgrove.applocker.Services";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "Service has now started.");
        new Thread(new Runnable(){
            public void run() {
                // TODO Auto-generated method stub
                while(true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //REST OF CODE HERE//
                    String packageName = getForegroundPackageName();
                    Log.i(TAG, "ForegroundApp: " + getForegroundPackageName());
                    if (isMyAppRunning()){
                        Intent intent = new Intent(getApplicationContext(), lockActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }
        }).start();
    }

    public String getForegroundPackageName () {
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
        Log.i(TAG, "Service has now started.");
        return super.onStartCommand(intent, flags, startId);
    }

    private boolean isMyAppRunning () {
        ArrayList<String> appList = AppSQLiteDBHelper.getAllApps(this);
        if (appList.contains(getForegroundPackageName())){
            return true;
        }
        return false;
    }
}
