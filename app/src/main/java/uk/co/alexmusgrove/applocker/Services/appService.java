package uk.co.alexmusgrove.applocker.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import uk.co.alexmusgrove.applocker.Activities.HomeActivity;
import uk.co.alexmusgrove.applocker.Activities.lockActivity;
import uk.co.alexmusgrove.applocker.Database.AppSQLiteDBHelper;
import uk.co.alexmusgrove.applocker.Helpers.unlockedApp;
import uk.co.alexmusgrove.applocker.R;

public class appService extends Service {

    private static final int timer = 1000;
    private AtomicBoolean working = new AtomicBoolean(false);
    private static String CHANNEL_ID = "my_channel_1";// The id of the channel.
    private Runnable runnable = () -> {
            while (working.get()) {
                doWork();
            }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        if (!working.get()) {
            working.set(true);
            new Thread(runnable).start();
        }
    }


    public void doWork() {
        ArrayList<unlockedApp> unlockedApps = AppSQLiteDBHelper.getAllUnlockedApps(this);
        threadSleep(timer);

        unlockedApps.forEach(unlockedApp -> unlockedApp.checkIfUnlockedAppExpired(this, unlockedApp));

        String packageName = getForegroundPackageName(this);
        if (isAppRunning(this)) {
            if (!unlockedApp.isAppUnlocked(this, packageName)) {
                invokeLock(packageName);
            }
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String unlockedPackageName = extras.getString("unlockedApp");
            if (unlockedPackageName != null) {
                unlockedApp.addUnlockedApp(this, unlockedPackageName);
            }
        }

        createNotificationChannel();
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = buildForegroundNotification(pendingIntent);

        startForeground(1, notification);
        return START_STICKY;
    }


    private static String getForegroundPackageName(appService appService) {
        UsageStatsManager usm = (UsageStatsManager) appService.getSystemService(USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> appList = null;
        SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();

        if (usm != null) {
            appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                    time - 1000 * 1000, time);
        }
        if (appList != null && appList.size() > 0) {
            appList.forEach(usageStats -> mySortedMap.put(usageStats.getLastTimeUsed(), usageStats));
        }
        return mySortedMap.get(mySortedMap.lastKey()).getPackageName();
    }


    private static boolean isAppRunning(appService appService) {
        ArrayList<String> appList = AppSQLiteDBHelper.getAllApps(appService);
        return appList.contains(getForegroundPackageName(appService));
    }

    private static void threadSleep(int timer) {
        try {
            Thread.sleep(timer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void invokeLock(String packageName) {
        Intent intent = new Intent(getApplicationContext(), lockActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra("packageName", packageName);
        startActivity(intent);
    }

    public Notification buildForegroundNotification (PendingIntent pendingIntent) {
        Integer numberOfApps = AppSQLiteDBHelper.getAllApps(this).size();
        String appSizeString = (numberOfApps > 1) ? " locked apps." : " locked app.";
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Locked Apps")
                .setContentText("You currently have " + numberOfApps + appSizeString)
                .setSmallIcon(R.drawable.ic_build_grey_24dp)
                .setChannelId(CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .build();
        return notification;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.appLockerChannel),
                    NotificationManager.IMPORTANCE_MIN
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
