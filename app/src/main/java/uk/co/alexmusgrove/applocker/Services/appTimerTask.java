package uk.co.alexmusgrove.applocker.Services;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TimerTask;
import java.util.TreeMap;


public class appTimerTask extends TimerTask {
    private static final String TAG = "uk.co.alexmusgrove.applocker.Services";
    private UsageStatsManager usm;
    private ActivityManager am;
    private ContentResolver cr;
    private static final String AUTHORITY = "uk.co.alexmusgrove.applocker.Database.ContentProvider";
    private static final String BASE_PATH = "provider";
    private Context mContext;

    //create content URIs from the authority be appending path to database table
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    @Override
    public void run() {
        String mPackageName = getForegroundPackageName();
        ArrayList<String> lockedApps = getAllApps();
        if (lockedApps.contains(mPackageName)){
            Intent intent = new Intent();
            intent.setClassName(
                    "uk.co.alexmusgrove.applocker",
                    "uk.co.alexmusgrove.applocker.Activities.lockActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }

    }

    public appTimerTask(Context context) {
        mContext = context;
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(context.USAGE_STATS_SERVICE);
        am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        cr = context.getContentResolver();
    }
    public String getForegroundPackageName () {
        String currentApp = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                    time - 1000 * 1000, time);
            Log.i(TAG, "generateAppItems: " + appList.size());
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
            currentApp = am.getRunningTasks(1).get(0).topActivity .getPackageName();
        }

        if (currentApp == null){
            return "Could not find package name.";
        } else {
            return currentApp;
        }
    }

    public ArrayList<String> getAllApps () {
        ArrayList<String> apps = new ArrayList<>();
        Cursor cursor = cr.query(CONTENT_URI,
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

}
