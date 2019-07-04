package uk.co.alexmusgrove.applocker.Activities;

import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uk.co.alexmusgrove.applocker.Database.AppContentProvider;
import uk.co.alexmusgrove.applocker.Database.AppSQLiteDBHelper;
import uk.co.alexmusgrove.applocker.Fragments.passwordFragment;
import uk.co.alexmusgrove.applocker.Fragments.permissionFragment;
import uk.co.alexmusgrove.applocker.Fragments.settingsFragment;
import uk.co.alexmusgrove.applocker.Fragments.systemappsFragment;
import uk.co.alexmusgrove.applocker.Fragments.userappsFragment;
import uk.co.alexmusgrove.applocker.Helpers.appItem;
import uk.co.alexmusgrove.applocker.Preferences.settingsPreferences;
import uk.co.alexmusgrove.applocker.R;
import uk.co.alexmusgrove.applocker.Services.appService;

public class HomeActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (!hasUsageStatsPermission()) {
            permissionFragment dialog = permissionFragment.newInstance();
            dialog.show(this.getSupportFragmentManager(), "permissionFragment");
        }
        generateBottomNavBar();
        generateAppItems();
        startAppService();

        if(!hasPasswordSet()){
            passwordFragment dialog = passwordFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            dialog.show(fragmentManager,"passwordFragment");
        }
        loadFragment(new userappsFragment());
    }

    private ArrayList<appItem> appItems = new ArrayList<>();

    public void generateBottomNavBar () {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.action_user:
                    Toast.makeText(HomeActivity.this, "User Apps", Toast.LENGTH_SHORT).show();
                    fragment = new userappsFragment();
                    break;
                case R.id.action_system:
                    Toast.makeText(HomeActivity.this, "System Apps", Toast.LENGTH_SHORT).show();
                    fragment = new systemappsFragment();
                    break;
                case R.id.action_settings:
                    Toast.makeText(HomeActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                    fragment = new settingsFragment();
                    break;
            }
            return loadFragment(fragment);
        });
    }

    public void generateAppItems () {
        final PackageManager pm = getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            //filter all systems applications out of loop
            if (!((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)) {
                //grab application label from package name
                String appName = (String) pm.getApplicationLabel(packageInfo);
                ArrayList<String> appList = AppSQLiteDBHelper.getAllApps(this);
                //append details to appItem class
                if (!packageInfo.packageName.equals("uk.co.alexmusgrove.applocker")){
                    appItems.add(new appItem(
                            appName, //name of application
                            packageInfo.packageName, // unique package name of application
                            packageInfo.loadIcon(pm), // application icon
                            appList.contains(packageInfo.packageName) // app lock switch state
                    ));
                }
            }
        }
        Collections.sort(appItems, (Comparator) (o1, o2) -> {
            appItem app1 = (appItem) o1;
            appItem app2 = (appItem) o2;
            return app1.getmAppName().compareToIgnoreCase(app2.getmAppName());
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        int notifyID = 1;
        String CHANNEL_ID = "my_channel_1";// The id of the channel.
        CharSequence name = getString(R.string.appLockerChannel);// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_MIN;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
// Create a notification and set the notification channel.
        Notification notification = new Notification.Builder(HomeActivity.this, CHANNEL_ID)
                .setContentTitle("Locked Apps")
                .setContentText("You currently have " + AppSQLiteDBHelper.getAllApps(this).size() + " locked apps.")
                .setSmallIcon(R.drawable.ic_build_black_24dp)
                .setChannelId(CHANNEL_ID)
                .build();
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        }

// Issue the notification.
        mNotificationManager.notify(notifyID , notification);
    }

    private boolean loadFragment(Fragment fragment){
        if (fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }
        return false;
    }


    private boolean hasUsageStatsPermission () {
        AppOpsManager appOpsManager = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (appOpsManager != null) {
            mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getApplicationContext().getPackageName());
        }
        return (mode == AppOpsManager.MODE_ALLOWED);
    }

    public void startAppService () {
        if (hasUsageStatsPermission()) {
            //check if user has granted permission for usage of this service.
            Intent appService = new Intent(this, uk.co.alexmusgrove.applocker.Services.appService.class);
            appService.setAction(USAGE_STATS_SERVICE);
            startService(appService);
        }
    }

    private boolean hasPasswordSet () {
        return !(settingsPreferences.getPassword(this) == null);
    }

    public ArrayList<appItem> getAppItems() {
        return appItems;
    }
}
