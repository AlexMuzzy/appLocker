package uk.co.alexmusgrove.applocker.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class appService extends Service {

    BroadcastReceiver broadcastReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcastReceiver = new appBroadcastReceiver();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
