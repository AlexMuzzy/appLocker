package uk.co.alexmusgrove.applocker.Services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;


public class appService extends Service {

    private Timer mTimer;
    private static final String TAG = "uk.co.alexmusgrove.applocker.Services";
    public static final int FOREGROUND_ID = 0;

    private void startTimer(){
        if (mTimer == null){
            mTimer = new Timer();
            appTimerTask appLockTask = new appTimerTask(this);
            mTimer.schedule(appLockTask, 0L, 1000L);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    public void onCreate(){
        super.onCreate();
        Toast.makeText(getApplicationContext(), "service start", Toast.LENGTH_SHORT).show();
        startForeground(FOREGROUND_ID, new Notification());
    }

}
